package com.mv8;

public class V8Value {

	private long ptr;
	private long isolatePtr;
	private long contextPtr;

	V8Value(long isolatePtr, long contextPtr, long valuePtr) {
		this.isolatePtr = isolatePtr;
		this.contextPtr = contextPtr;
		this.ptr = valuePtr;
	}
	
	public String getStringValue() {
		return _getStringValue(isolatePtr, contextPtr, ptr);
	}
	
	private native static String _getStringValue(long isolatePtr, long contextPtr, long valuePtr);

}
