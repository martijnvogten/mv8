package mv8;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mv8.V8;
import com.mv8.V8Context;
import com.mv8.V8Isolate;
import com.mv8.V8Value;

import jettyv8.server.DebugServer;

public class InspectorTest {

	static Logger logger = LoggerFactory.getLogger(DebugServer.class);

	@Test
	public void hello() {
		V8Isolate isolate = V8.createIsolate(null);
		V8Context context = isolate.createContext();
		V8Value result = context.runScript("'Hello ' + 'world!'", "");
		logger.debug(result.getStringValue());
	}

}
