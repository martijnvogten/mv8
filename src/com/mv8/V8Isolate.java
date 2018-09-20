package com.mv8;

public class V8Isolate {
	long isolatePtr;
	V8Isolate(long isolatePtr) {
		this.isolatePtr = isolatePtr;
	}

	public V8Context createContext() {
		V8Context context = new V8Context(isolatePtr);
		context.init(_createContext(isolatePtr, context));
		return context;
	}

	public V8ObjectTemplate createObjectTemplate() {
		return new V8ObjectTemplate(isolatePtr, _createObjectTemplate(isolatePtr));
	}
	
	private static native long _createContext(long isolatePtr, V8Context javaInstance);
	private static native long _createObjectTemplate(long isolatePtr);

}