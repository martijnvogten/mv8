#!/bin/bash
cd jni
javah -cp ../bin com.mv8.V8 com.mv8.V8Isolate com.mv8.V8Context com.mv8.V8Value com.mv8.V8ObjectTemplate
cd ..