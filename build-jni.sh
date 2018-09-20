#!/bin/bash

g++ -shared -I. -Iinclude -Idep_includes_macosx jni/com_mv8_V8.cpp \
	-o libmv8.dylib \
	-Wl,libs/libv8_{libbase,libplatform,monolith}.a \
	-ldl -pthread -std=c++11 -fPIC 

# -Wl,libs/{libv8_{base,libbase,external_snapshot,libplatform,libsampler},libicu{uc,i18n},libinspector}.a \

# g++ -I. -Iinclude -Idep_includes_macosx samples/hello-world.cc \
# 	-o hello \
# 	-Wl,libs/libv8_{base,libbase,libplatform,libsampler,nosnapshot}.a \
# 	-ldl -pthread -std=c++11
