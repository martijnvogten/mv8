package com.mv8;

public class V8Context implements AutoCloseable {
	private long ptr;
	private long isolatePtr;
	private boolean closed = false;
	private JavaCallback callback = null;
	
	V8Context(long isolatePtr) {
		this.isolatePtr = isolatePtr;
	}
	
	public void init(long ptr) {
		this.ptr = ptr;
	}
	
	public void setCallback(JavaCallback cb) {
		this.callback = cb;
	}
	
	public V8Value runScript(String script) {
		if (closed) {
			throw new RuntimeException("context closed");
		}
		return new V8Value(isolatePtr, ptr, _runScript(isolatePtr, ptr, script));
	}

	public String __calljava(String message) {
		if (callback == null) {
			throw new RuntimeException("No callback set");
		}
		return callback.call(message);
	}

	private static native long _runScript(long isolatePtr, long contextPtr, String script);
	private static native void _dispose(long isolatePtr, long contextPtr);

	@Override
	public void close() {
		closed = true;
		_dispose(isolatePtr, ptr);
	}

}
