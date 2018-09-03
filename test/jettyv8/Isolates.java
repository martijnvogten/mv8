package jettyv8;

import org.junit.Test;

import com.eclipsesource.v8.V8;

public class Isolates {
	
	@Test
	public void testIt() {

		
		V8 v8 = V8.createV8Runtime();
		
		v8.registerJavaMethod((receiver, params) -> {
			System.out.println(params.get(0));
		}, "print");
		

		long before = System.nanoTime();
		
		v8.registerJavaMethod((receiver, params) -> {
			System.out.println(params.get(0));
		}, "printje");
		
		long after = System.nanoTime();
		
//		v8.executeVoidScript("print(String._myprop + ', now ' + (String._myprop = 'piet'))");
		
//		
		v8.resetContext();
//		
//		v8context = v8.createV8Context();
		
		v8.registerJavaMethod((receiver, params) -> {
			System.out.println(params.get(0));
		}, "print");
		
//		v8.registerJavaMethod((receiver, params) -> {
//			System.out.println(params.get(0));
//		}, "print");
//		
		v8.executeVoidScript("print(String._myprop + ', now ' + (String._myprop = 'piet'))");
		v8.executeVoidScript("print(String._myprop + ', now ' + (String._myprop = 'piet'))");
		
		v8.resetContext();
		
		v8.registerJavaMethod((receiver, params) -> {
			System.out.println(params.get(0));
		}, "print");
		
		v8.executeVoidScript("print(String._myprop + ', now ' + (String._myprop = 'piet'))");
		v8.executeVoidScript("print(String._myprop + ', now ' + (String._myprop = 'piet'))");
		
		System.out.println("Create context took " + (after - before) / 1_000 + " µs");
		
	}

}
