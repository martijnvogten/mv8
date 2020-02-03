package mv8;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		V8Context context = isolate.createContext("hello");
		V8Value result = context.runScript("'Hello ' + 'world!'", "");
		logger.debug(result.getStringValue());
	}
	
	@Test
	public void testDispose() {
		for (int i = 0; i < 1000; i++) {
			try (V8Isolate isolate = V8.createIsolate(null);) {
				try (V8Context context = isolate.createContext("hello");) {
					context.runScript("'Hello ' + 'world!'", "");
//					logger.info(result.getStringValue());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		logger.info("Done!");
	}

	@Test
	public void doit() {
		byte[] startupData = V8.createStartupDataBlob("const sayIt = function() {return 'it' + new Date().getTime()};", "<embedded>");
		
		V8Isolate isolate = V8.createIsolate(startupData);
		
		V8Context context = isolate.createContext("doit");
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
			try (V8Context context2 = isolate.createContext("context" + i)) {
				context2.setCallback(cb);
				context2.runScript("__calljava('print:' + sayIt());", "");
				TimeIt.time("100000 invocations", () -> {
					context2.runScript("for(var i = 0; i < 100000; i++) {__calljava(sayIt())};", "");
				});
			}
		}
	}
	
	@Test
	public void testStartupData() throws Exception {
		String reactJs = readJsFiles(
				Paths.get("js", "react.js"), 
				Paths.get("js", "react-dom.js"), 
				Paths.get("js", "react-dom-server.js"));
		
		byte[] startupData = V8.createStartupDataBlob(reactJs, "<embedded>");
		
		System.out.println("Startup data blob size: " + startupData.length);
		
		TimeIt.time("Reload React", () -> {
			try (V8Isolate isolate = V8.createIsolate();
			     V8Context context = isolate.createContext("context");) {
				context.runScript(reactJs, "<react>");
				V8Value value = context.runScript("ReactDOMServer.renderToStaticMarkup(React.createElement('h1'))", "");
				System.out.println(value.getStringValue());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		TimeIt.time("Using startupData", () -> {
			try (V8Isolate isolate = V8.createIsolate(startupData);
			     V8Context context = isolate.createContext("context");) {
				V8Value value = context.runScript("ReactDOMServer.renderToStaticMarkup(React.createElement('h1'))", "");
				System.out.println(value.getStringValue());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		try (V8Isolate isolate = V8.createIsolate(startupData)) {
			StringBuilder results = new StringBuilder();
			TimeIt.time("run 1000 contexts", () -> {
				for (int i = 0; i < 1000; i++) {
					try (V8Context context = isolate.createContext("context");) {
						V8Value value = context.runScript("ReactDOMServer.renderToStaticMarkup(React.createElement('h1'))", "");
						results.append(value.getStringValue());
						results.append("\n");
					}
				}
			});
			System.out.println("RESULTS: "+ results);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String readJsFiles(Path... paths) throws Exception {
		StringBuilder result = new StringBuilder();
		for (Path p : paths) {
			if (result.length() > 0) {
				result.append("\n\n");
			}
			result.append(new String(Files.readAllBytes(p), StandardCharsets.UTF_8.name()));
		}
		return result.toString();
	}

}
