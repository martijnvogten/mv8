package jettyv8.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mv8.V8;
import com.mv8.V8Context;
import com.mv8.V8Isolate;

public class WebServer {
	static Logger logger = LoggerFactory.getLogger(WebServer.class);
	
	static ExecutorService worker = Executors.newSingleThreadExecutor();

	private static V8Isolate isolate;
	
	public static class HelloWorldServlet extends DefaultServlet {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			try {
				response.setContentType("text/html");
				response.setCharacterEncoding(StandardCharsets.UTF_8.name());
				
				final String contextName = "webserver-" + Thread.currentThread().getName();
				
				FutureTask<String> task = new FutureTask<String>(() -> {
					try (V8Context context = isolate.createContext(contextName)) {
						context.setCallback((payload) -> {
							logger.debug("Got callback with payload: " + payload);
							return "";
						});
						
						context.runScript(readJsFiles(Paths.get("typescript", "build.js")), "typescript/build.js");
						
						return context.runScript("renderHTML()", "");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				
				worker.submit(task);
				String html = task.get();
				
				response.setStatus(200);
				response.getWriter().println(html);
			} catch (InterruptedException | ExecutionException e) {
				response.setStatus(500);
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		DebugServer debugServer = DebugServer.start(9999);
		
		worker.execute(() -> {
			try {
				
				String reactJs = readJsFiles(
						Paths.get("js", "react.js"), 
						Paths.get("js", "react-dom.js"), 
						Paths.get("js", "react-dom-server.js"));
				
				byte[] startupData = V8.createStartupDataBlob("process = {pid: 12345, version: '8.3.14', arch: 'darwin'};\n\n" + reactJs, "<embedded>");
				isolate = V8.createIsolate(startupData);
						
				debugServer.attachIsolate(isolate);
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);
        
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);
        
		handler.addServlet(HelloWorldServlet.class, "/");
		
		server.start();
		
		server.join();
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
