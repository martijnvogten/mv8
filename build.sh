#!/bin/bash
set -e -x

# Builds the libmv8.so/dylib shared library for use with the mv8 Java classes.
# It includes the precompiled V8 static library which is taken from the
# Ruby libv8 gem by Charles Lowell.
# Header files are included from the V8 gem and the Java JDK home directory
#
# Linux and MacOS (darwin) only, no Windows support (yet).

# prerequisites:
#  gradle
#  g++
#  java
#  gem

V8_VERSION=7.3.492.27.1
# Install libv8 Ruby gem
# sudo gem install libv8 --version $V8_VERSION

# this also generates/updates JNI header files in /jni
gradle assemble

# This should work the same in both Linux and Mac
JAVA_HOME="$(java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' | grep -oE '\S+$')"
V8_BASE="$(cd $(dirname $(gem which libv8))/../vendor/v8 ; pwd)"
INCLUDE_V8="$V8_BASE/include"
LIBS_V8="$V8_BASE/out.gn/libv8/obj"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
	JAVA_INCLUDES="-I$JAVA_HOME/include -I$JAVA_HOME/include/linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
	JAVA_INCLUDES="-I$JAVA_HOME/../include -I$JAVA_HOME/../include/darwin"
fi

g++ -shared -I$INCLUDE_V8 $JAVA_INCLUDES \
	src/main/cpp/mv8.cpp \
	-o libmv8.so \
	-Wl,$LIBS_V8/libv8_monolith.a \
	-ldl -pthread -std=c++11 -fPIC
