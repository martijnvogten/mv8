package mv8;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mv8.JavaCallback;
import com.mv8.V8;
import com.mv8.V8Context;
import com.mv8.V8Isolate;
import com.mv8.V8Value;

import jettyv8.server.DebugServer;
import jettyv8.server.TimeIt;

public class PerformanceTest {

	static Logger logger = LoggerFactory.getLogger(DebugServer.class);

	@Test
	public void hello() {
		V8Isolate isolate = V8.createIsolate(null);
		V8Context context = isolate.createContext();
		V8Value result = context.runScript("'Hello ' + 'world!'", "");
		logger.debug(result.getStringValue());
	}

	@Test
	public void doit() {
		V8Isolate isolate = V8.createIsolate("sayIt = function() {return 'it' + new Date().getTime()};");
		
		V8Context context = isolate.createContext();
		JavaCallback cb = command -> {
			if (command.startsWith("print:")) {
				logger.debug(command.substring("print:".length()));
			}
			return "42";
		};
		context.setCallback(cb);
		
		V8Value result = context.runScript("__calljava('print:henk');", "");
		
		logger.debug(result.getStringValue());
		
		for(int i = 0; i < 10; i++) {
			try (V8Context context2 = isolate.createContext()) {
				context2.setCallback(cb);
				context2.runScript("__calljava('print:' + sayIt());", "");
				TimeIt.time("100000 invocations", () -> {
					context2.runScript("for(var i = 0; i < 100000; i++) {__calljava(sayIt())};", "");
				});
			}
		}
	}

}
