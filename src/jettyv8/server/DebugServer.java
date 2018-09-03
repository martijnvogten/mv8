package jettyv8.server;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.eclipsesource.v8.InspectorCallbacks;
import com.eclipsesource.v8.V8;

public class DebugServer {
	
	private static V8 v8 = V8.createV8Runtime();
	private static boolean resume = true;
	private static boolean waitingForRun = true;
	private static Session session = null;
	private static boolean wasRunning = false;
	private static BlockingQueue<String> messageQueue = new LinkedBlockingDeque<>(10);
	
	@SuppressWarnings("serial")
	public static void main(String[] args) throws Exception {
		
		int tcpPort = 2992;
//		String script = "const a = 42; debugger;";
		String script = 
				"let counter = 0;\n"
				+ "while(true) {\n"
				+ " counter = counter + 1;\n"
				+ "};\n";
		
		if (args.length >= 2) {
			tcpPort = Integer.parseInt(args[0]);
			script = args[1];
		}
		
		v8.registerJavaMethod((receiver, parameters) -> {
			System.out.println("PRINT: " + parameters.getString(0));
		}, "print");
		
		final String theScript = script;
		
		Server server = new Server(tcpPort);
		
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.GZIP);
        handler.setContextPath("/");
        server.setHandler(handler);
        
        handler.addServlet(new ServletHolder(new WebSocketServlet() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.setCreator((req, resp) -> new WebSocketAdapter() {
					@Override
					public void onWebSocketConnect(Session sess) {
						session = sess;
//						if (resume) {
//							System.out.println("SCHEDULE PAUSE!");
//							v8.schedulePauseOnNextStatement();
//						}
					}
					
					@Override
					public void onWebSocketClose(int statusCode, String reason) {
						// Resume
						System.out.println("Close connection " + statusCode + ", sending resume command.");
						messageQueue.offer("{\"id\":36,\"method\":\"Debugger.resume\"}");
					}

					@Override
					public void onWebSocketText(String message) {
						System.out.println("Got websocket text: " + message);
						if (!waitingForRun && resume) {
							wasRunning = true;
							v8.schedulePauseOnNextStatement();
						}
						if (message.indexOf("getPossible") > -1) {
							System.out.println("Get possible");
						}
						messageQueue.offer(message);
					}
				});
			}
        }), "/ws");
        
        ServletHolder metadataHolder = new ServletHolder(new MetadataServlet());
		handler.addServlet(metadataHolder, "/*");
        
        server.start();
        
		v8.setInspectorClient(new InspectorCallbacks() {
			@Override
			public void runMessageLoopOnPause() {
				try {
					resume = false;
					while(!resume) {
						String message = messageQueue.take();
						v8.sendInspectorMessage(message);
//						if (wasRunning) {
//							wasRunning = false;
//							break;
//						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void quitMessageLoopOnPause() {
				resume = true;
			}
			
			@Override
			public void handleMessage(String theMessage) {
				try {
					if (session != null) {
						System.out.println("Sending: " + theMessage);
						session.getRemote().sendString(theMessage);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void runIfWaitingForDebugger() {
				waitingForRun = false;
			}
		});
		
		v8.sendInspectorMessage("{\"id\":0,\"method\":\"Debugger.enable\"}");
		
//		while(waitingForRun) {
//			String message = messageQueue.take();
//			v8.sendInspectorMessage(message);
//		}
		
		waitingForRun = false;
		
		v8.executeVoidScript(theScript);
		
		
//		System.out.println("Running the script");
		
		
		
		// {"id":1,"method":"Runtime.evaluate","params":{"expression":"a"}}
        
        server.join();
	}
}
