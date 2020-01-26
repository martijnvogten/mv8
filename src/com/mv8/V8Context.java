package com.mv8;

public class V8Context implements AutoCloseable {
	private long ptr;
	private long isolatePtr;
	private boolean closed = false;
	private JavaCallback callback = null;
	private InspectorChannel inspectorChannel;
	
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

	public void sendInspectorMessage(String message) {
		_sendInspectorMessage(isolatePtr, ptr, message);
	}
	
	public void runIfWaitingForDebugger() {
		System.out.println("RUNIFWAITINGFORDEBUGGER");
	}
	
	public void quitMessageLoopOnPause() {
		System.out.println("QUITMESSAGELOOPONPAUSE");
	}
	
	public void runMessageLoopOnPause() {
		System.out.println("RUNMESSAGELOOPONPAUSE");
	}
	
	public void handleInspectorMessage(String message) {
		if (inspectorChannel != null) {
			inspectorChannel.handleInspectorMessage(message);
		}
	}
	
	public void setInspectorChannel(InspectorChannel channel) {
		this.inspectorChannel = channel;
	}

	private static native void _sendInspectorMessage(long isolatePtr, long contextPtr, String message);

}
