package com.eclipsesource.v8;

public interface InspectorCallbacks {
	void handleMessage(String theMessage);
	void runMessageLoopOnPause();
	void quitMessageLoopOnPause();
	void runIfWaitingForDebugger();
}
