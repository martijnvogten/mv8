package jettyv8.server;

import org.junit.Test;

import com.mv8.JavaCallback;
import com.mv8.V8;
import com.mv8.V8Context;
import com.mv8.V8Isolate;
import com.mv8.V8Value;


public class DebugServer {
	

	@Test
	public void doit() {
		V8Isolate isolate = V8.createIsolate("sayIt = function() {return 'it'};");
		
		V8Context context = isolate.createContext();
		JavaCallback cb = command -> {
			return "42";
		};
		context.setCallback(cb);
		
		V8Value result = context.runScript("__calljava('henk');");
		
		System.out.println(result.getStringValue());
		
		for(int i = 0; i < 10; i++) {
			TimeIt.time("Create context", () -> {
				try (V8Context context2 = isolate.createContext()) {
					context2.setCallback(cb);
					context2.runScript("for(var i = 0; i < 100000; i++) {__calljava('')};");
				}
			});
		}
	}
}
