// Copyright 2015 the V8 project authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <thread>

#include "include/libplatform/libplatform.h"
#include "include/v8.h"
#include <unistd.h>
#include <sys/time.h>

using namespace v8;

Isolate * isolate;
void runInIsolate(const char * script);
Persistent<ObjectTemplate> * global;

void doit(const char * script) {
	runInIsolate(script);
}

long microtime() {
	struct timeval time;
	gettimeofday(&time, NULL);
	return ((unsigned long long)time.tv_sec * 1000000) + time.tv_usec;
}

// get called from js, switch context, do something, switch context back, continue
// switch scope, wait for result in different thread

static void LogCallback(const v8::FunctionCallbackInfo<v8::Value>& args) {
  if (args.Length() < 1) return;
  HandleScope scope(args.GetIsolate());
  Local<Value> arg = args[0];
  String::Utf8Value value(arg);
  printf("log: %s\n", *value);
  {
	long start = microtime();
  	v8::Unlocker unlock(isolate);
	printf("Unlock took %ld\n", microtime() - start);fflush(stdout);
  }
  // Wait for some I/O
  usleep(500000);
  {
	long start = microtime();
	v8::Locker lock(isolate);
	printf("Lock took %ld\n", microtime() - start);fflush(stdout);
  }
}

void runInIsolate(const char * scriptSource) {
	v8::Locker locker(isolate);

	Isolate::Scope isolate_scope(isolate);
	HandleScope handle_scope(isolate);

	Local<String> source = String::NewFromUtf8(isolate, scriptSource, NewStringType::kNormal).ToLocalChecked();

	Local<Context> context = Context::New(isolate, NULL, global->Get(isolate));
	Context::Scope context_scope(context);

	Local<Script> script = Script::Compile(context, source).ToLocalChecked();
	Local<Value> result = script->Run(context).ToLocalChecked();
	String::Utf8Value utf8(result);
	printf("%s\n", *utf8);
}

int main(int argc, char *argv[])
{

	// Initialize V8.
	// V8::InitializeICUDefaultLocation(argv[0]);
	V8::InitializeExternalStartupData(argv[0]);

	Platform *platform = platform::CreateDefaultPlatform();
	V8::InitializePlatform(platform);
	V8::Initialize();

	// Create a new Isolate and make it the current one.
	Isolate::CreateParams create_params;
	create_params.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();

	isolate = Isolate::New(create_params);
	Isolate::Scope isolate_scope(isolate);
	HandleScope handle_scope(isolate);

	global = new Persistent<ObjectTemplate>();
	global->Reset(isolate, ObjectTemplate::New(isolate));
	global->Get(isolate)->Set(String::NewFromUtf8(isolate, "log", NewStringType::kNormal).ToLocalChecked(), FunctionTemplate::New(isolate, LogCallback));

	std::thread t1(doit, "for(let i = 0; i < 10; i++) { log('looping' + i);}");

	usleep(100000);

	std::thread t2(doit, "log('t2: one'); log('t2: two');");

	usleep(100000);
	
	std::thread t3(doit, "log('t3: one'); log('t3: two');");

	usleep(100000);

	runInIsolate("log('main: one'); log('main: two');");

	t1.join();
	t2.join();
	t3.join();
	printf("Did it");

	// usleep(1000000);
	// Dispose the isolate and tear down V8.
	isolate->Enter();

	isolate->Dispose();
	V8::Dispose();
	V8::ShutdownPlatform();
	delete platform;
	delete create_params.array_buffer_allocator;
	return 0;
}
