#!/bin/bash
g++ -I. -Iinclude -Idep_includes_macosx samples/threads.cc \
	-o threads \
	-Wl,libs/libv8_{libbase,libplatform,monolith}.a \
	-ldl -pthread -std=c++11
