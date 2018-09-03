#!/bin/bash

g++ -shared -I. -Iinclude -Idep_includes_macosx jni/com_eclipsesource_v8_V8Impl.cpp \
	-o libj2v8_macosx_x86_64.dylib \
	-Wl,libs/{libv8_{base,libbase,external_snapshot,libplatform,libsampler},libicu{uc,i18n},libinspector}.a \
	-ldl -pthread -std=c++11 -fPIC 


# g++ -I. -Iinclude -Idep_includes_macosx samples/hello-world.cc \
# 	-o hello \
# 	-Wl,libs/libv8_{base,libbase,libplatform,libsampler,nosnapshot}.a \
# 	-ldl -pthread -std=c++11
