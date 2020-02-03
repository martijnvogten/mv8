package com.mv8;

import org.junit.Test;

public class V8 {
	static {
		System.loadLibrary("mv8");
	}

	public static V8Isolate createIsolate(String snapshotBlob) {
		V8Isolate isolate = new V8Isolate();
		isolate.init(_createIsolate(isolate, snapshotBlob));
		return isolate;
	}

	private static native long _createIsolate(V8Isolate isolate, String snapshotBlob);

	@Test
	public void doit() {
	}
}