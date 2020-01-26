#include <libplatform/libplatform.h>
#include <iostream>
#include <v8.h>
#include <string.h>
#include <v8-inspector.h>
#include <cstdlib>
#include <jni.h>
#include "mv8.h"

void getJNIEnv(JNIEnv*& env);
void handleInpectorMessage(v8::Local<v8::String> message);

