package mv8;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mv8.V8;
import com.mv8.V8Context;
import com.mv8.V8Isolate;
import com.mv8.V8Value;

import jettyv8.server.DebugServer;

public class InspectorThreadsTest {

	static Logger logger = LoggerFactory.getLogger(DebugServer.class);

	@Test
	public void hello() throws Exception {
		
		DebugServer debugServer = DebugServer.start(9999);
		
		V8Isolate isolate = V8.createIsolate(null);
		debugServer.attachIsolate(isolate);
		
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Thread thread = new Thread(() -> {
				try (V8Context context = isolate.createContext("default")) {
//				V8Value result = context.runScript("'Hello ' + 'world!" + i + "'", "");
					V8Value result = context.runScript("debugger; 'henk'", "");
					logger.debug(result.getStringValue());
				}
			});
			thread.start();
			threads.add(thread);
		}
		
		for(Thread t : threads) {
			t.join();
		}
	}

}
