// Copyright 2015 the V8 project authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/libplatform/libplatform.h"
#include "include/v8.h"

using namespace v8;

int main(int argc, char* argv[]) {
  // Initialize V8.
  // V8::InitializeICUDefaultLocation(argv[0]);
  V8::InitializeExternalStartupData(argv[0]);
  Platform* platform = platform::CreateDefaultPlatform();
  V8::InitializePlatform(platform);
  V8::Initialize();

  // Create a new Isolate and make it the current one.
  Isolate::CreateParams create_params;
  create_params.array_buffer_allocator =
      v8::ArrayBuffer::Allocator::NewDefaultAllocator();
  Isolate* isolate = Isolate::New(create_params);
  {
    Isolate::Scope isolate_scope(isolate);

    // Create a stack-allocated handle scope.
    HandleScope handle_scope(isolate);

    Local<Value> resultA;
    Local<Value> resultB;

    Local<String> source = String::NewFromUtf8(isolate, "'Hello' + String._myprop + ', ' + (String._myprop = 'henk') + new Date().getTime() + ', World!'", NewStringType::kNormal).ToLocalChecked();
    {
      Local<Context> context = Context::New(isolate);
      Context::Scope context_scope(context);
      Local<Script> script = Script::Compile(context, source).ToLocalChecked();
      resultA = script->Run(context).ToLocalChecked();
    }

    {
      Local<Context> context = Context::New(isolate);
      Context::Scope context_scope(context);
      Local<Script> script = Script::Compile(context, source).ToLocalChecked();
      resultB = script->Run(context).ToLocalChecked();
    }

    // Convert the result to an UTF8 string and print it.
    String::Utf8Value utf8(resultA);
    printf("%s\n", *utf8);
    String::Utf8Value utf8B(resultB);
    printf("%s\n", *utf8B);
  }

  // Dispose the isolate and tear down V8.
  isolate->Dispose();
  V8::Dispose();
  V8::ShutdownPlatform();
  delete platform;
  delete create_params.array_buffer_allocator;
  return 0;
}
