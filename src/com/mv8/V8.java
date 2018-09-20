package com.mv8;

import org.junit.Test;

public class V8 {
	static {
		System.loadLibrary("mv8");
	}

	public static V8Isolate createIsolate(String snapshotBlob) {
		return new V8Isolate(_createIsolate(snapshotBlob));
	}

	private static native long _createIsolate(String snapshotBlob);

	@Test
	public void doit() {
	}
}