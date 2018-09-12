package com.eclipsesource.v8;

public class V8Context extends V8Object {

	public V8Context(V8 v8, long contextPtr) {
		this.objectHandle = contextPtr;
		this.v8 = v8;
		this.released = false;
	}

}
