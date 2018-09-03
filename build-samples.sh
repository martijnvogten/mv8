#!/bin/bash
g++ -I. -Iinclude -Idep_includes_macosx samples/hello-world.cc \
	-o hello \
	-Wl,libs/{libv8_{base,libbase,external_snapshot,libplatform,libsampler},libicu{uc,i18n},libinspector}.a \
	-ldl -pthread -std=c++11
