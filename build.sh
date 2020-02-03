#!/bin/bash
set -e -x

# Builds both Java .jar and the libmv8.{so,dylib} shared library.
# The latter will contain the precompiled V8 static library from Charles Lowell's libv8 Rubygem.
# Header files are included from the V8 gem and the Java JDK home directory.
#
# Linux and MacOS (darwin) only, no Windows support (yet).

# prerequisites:
#  gradle
#  g++
#  java
#  gem

V8_VERSION=7.3.492.27.1
# Install libv8 Ruby gem
sudo gem install libv8 --version $V8_VERSION

# this also generates/updates JNI header files in src/main/cpp/
gradle assemble

JAVA_HOME="$(java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' | grep -oE '\S+$')"
V8_BASE="$(cd $(dirname $(gem which libv8))/../vendor/v8 ; pwd)"
V8_INCLUDE="$V8_BASE/include"
V8_OBJ="$V8_BASE/out.gn/libv8/obj"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
	JAVA_INCLUDES="-I$JAVA_HOME/include -I$JAVA_HOME/include/linux"
	OUTPUT_FILE=libmv8.so
elif [[ "$OSTYPE" == "darwin"* ]]; then
	JAVA_INCLUDES="-I$JAVA_HOME/../include -I$JAVA_HOME/../include/darwin"
	OUTPUT_FILE=libmv8.dylib
fi

g++ -shared -I$V8_INCLUDE $JAVA_INCLUDES \
	src/main/cpp/mv8.cpp \
	-o $OUTPUT_FILE \
	-Wl,$V8_OBJ/libv8_monolith.a \
	-ldl -pthread -std=c++11 -fPIC
