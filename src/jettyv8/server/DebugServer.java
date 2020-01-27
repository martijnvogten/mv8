package jettyv8.server;

import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.Test;

import com.mv8.InspectorChannel;
import com.mv8.JavaCallback;
import com.mv8.V8;
import com.mv8.V8Context;
import com.mv8.V8Isolate;
import com.mv8.V8Value;


public class DebugServer {
	
	private static V8Context context;
	
	private static LinkedBlockingQueue<String> messagesFromInspectorFrontEnd = new LinkedBlockingQueue<>();
	
	private static boolean quitMessageLoop;

	public static class MessagingAdapter extends WebSocketAdapter implements InspectorChannel {
		
	    private Session session;
	    
	    @Override
	    public void onWebSocketConnect(Session session) {
	        super.onWebSocketConnect(session); 
	        this.session = session;
	        context.setInspectorChannel(this);
	    }
	    
	    @Override
	    public void onWebSocketClose(int statusCode, String reason) {
	        this.session = null;
	        System.err.println("Close connection " + statusCode + ", " + reason);
	        super.onWebSocketClose(statusCode, reason); 
	    }
	    
	    @Override
	    public void onWebSocketText(String message) {
	    	super.onWebSocketText(message);
	    	
	    	messagesFromInspectorFrontEnd.offer(message);
	    }
	    
	    public void sendText(String text) throws Exception {
	    	session.getRemote().sendString(text);
	    }

		@Override
		public void handleInspectorMessage(String message) {
	        try {
	        	sendText(message);
	        } catch (Exception e) {
	        	throw new RuntimeException(e);
	        }
		}
	}
	
	public static class InspectorDebugServlet extends WebSocketServlet {
		private static final long serialVersionUID = 1L;

		@Override
	    public void configure(WebSocketServletFactory factory) {
	        factory.register(MessagingAdapter.class);
	    }
	}
	
	public static void main(String[] args) throws Exception {
		
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
        connector.setPort(9999);
        server.addConnector(connector);
        
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);
        
		V8Isolate isolate = V8.createIsolate(
				"sayIt = function() {return 'it' + new Date().getTime()};\n"
			);
		context = isolate.createContext();
		V8Value result = context.runScript("debugIt = function() {\n"
				+ " const a = 6, b = 7;\n"
				+ " debugger;\n"
				+ " return 'did it' + (a * b);\n"
				+ "};\n");
		System.out.println(result.getStringValue());

		handler.addServlet(InspectorDebugServlet.class, "/ws");
        handler.addServlet(MetadataServlet.class, "/json");
        handler.addServlet(MetadataServlet.class, "/json/list");
        handler.addServlet(MetadataServlet.class, "/json/version");
        
        server.start();
        new Thread(() -> {
        	try {
				runMessageLoop();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }).start();
        server.join();
	}
	
	public static void runMessageLoop() throws Exception {
		while (true) {
			String message = messagesFromInspectorFrontEnd.poll();
			if (message != null) {
				System.out.println("Sending message: " + message);
				context.sendInspectorMessage(message);
			}
			if (quitMessageLoop) {
				quitMessageLoop = false;
				break;
			} else {
				Thread.yield();
			}
		}
	}
	
	public static void quitMessageLoopOnPause() {
		quitMessageLoop = true;
	}
	
	public static void runMessageLoopOnPause() throws Exception {
		runMessageLoop();
	}

	@Test
	public void hello() {
		V8Isolate isolate = V8.createIsolate(null);
		V8Context context = isolate.createContext();
		V8Value result = context.runScript("'Hello ' + 'world!'");
		System.out.println(result.getStringValue());
	}

	@Test
	public void doit() {
		V8Isolate isolate = V8.createIsolate("sayIt = function() {return 'it' + new Date().getTime()};");
		
		V8Context context = isolate.createContext();
		JavaCallback cb = command -> {
			if (command.startsWith("print:")) {
				System.out.println(command.substring("print:".length()));
			}
			return "42";
		};
		context.setCallback(cb);
		
		V8Value result = context.runScript("__calljava('print:henk');");
		
		System.out.println(result.getStringValue());
		
		for(int i = 0; i < 10; i++) {
			try (V8Context context2 = isolate.createContext()) {
				context2.setCallback(cb);
				context2.runScript("__calljava('print:' + sayIt());");
				TimeIt.time("100000 invocations", () -> {
					context2.runScript("for(var i = 0; i < 100000; i++) {__calljava(sayIt())};");
				});
			}
		}
	}
}
