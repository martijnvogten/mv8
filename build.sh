#!/bin/bash
set -e -x

# Builds both Java .jar and the libmv8.{so,dylib} shared library.
# Header files and libraries are included from the v8 git folder
# which is assumed to be ~/git/v8
#
# Linux and MacOS only, no Windows support (yet).

# prerequisites:
#  v8 compiled as a monolith static library
#  gradle
#  g++
#  java

V8_VERSION=8.4.255.0

# Install libv8 Ruby gem
sudo gem install libv8 --version $V8_VERSION
V8_BASE="$(cd $(dirname $(gem which libv8))/../vendor/v8 ; pwd)"

# this also generates/updates JNI header files in src/main/cpp/
gradle assemble

JAVA_HOME="$(java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' | grep -oE '\S+$')"
V8_INCLUDE="$V8_BASE/include"
V8_OBJ="$V8_BASE/out.gn/libv8/obj"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
	JAVA_INCLUDES="-I$JAVA_HOME/include -I$JAVA_HOME/include/linux"
	OUTPUT_FILE=libmv8.so
elif [[ "$OSTYPE" == "darwin"* ]]; then
	JAVA_INCLUDES="-I$JAVA_HOME/include -I$JAVA_HOME/include/darwin"
	OUTPUT_FILE=libmv8.dylib
fi

g++ -shared -I$V8_INCLUDE $JAVA_INCLUDES \
	-DV8_COMPRESS_POINTERS \
	src/main/cpp/mv8.cpp \
	-o $OUTPUT_FILE \
	-Wl,$V8_OBJ/libv8_monolith.a \
	-ldl -pthread -std=c++11 -fPIC -fno-rtti
# Note: Omitting V8_COMPRESS_POINTERS will lead to segfaults
# https://stackoverflow.com/questions/59533323/v8-quickisundefined-crushes-randomly-when-using-isconstructcall

