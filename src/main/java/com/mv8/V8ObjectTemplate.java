package com.mv8;

public class V8ObjectTemplate {

	private long isolatePtr;
	private long objectTemplatePtr;

	public V8ObjectTemplate(long isolatePtr, long objectTemplatePtr) {
		this.isolatePtr = isolatePtr;
		this.objectTemplatePtr = objectTemplatePtr;
	}

	public void registerJavaCallback(String functionName, JavaCallback callback) {
		_registerJavaCallback(isolatePtr, objectTemplatePtr, functionName, callback);
	}

	private native static void _registerJavaCallback(long isolatePtr2, long objectTemplatePtr2, String functionName, JavaCallback callback);
}
