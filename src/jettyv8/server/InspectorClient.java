package jettyv8.server;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mv8.InspectorChannel;

public class InspectorClient implements InspectorChannel {
	
	public interface V8Inspector {
		void sendInspectorMessage(String message);
	}
	
	private V8Inspector inspector;

	private boolean quitMessageLoop;

	private InspectorFrontEnd inspectorFrontEnd;

	public InspectorClient(V8Inspector inspector, InspectorFrontEnd frontEnd) {
		this.inspector = inspector;
		this.inspectorFrontEnd = frontEnd;
	}
	
	@Override
	public void handleInspectorMessage(String message) {
        try {
        	inspectorFrontEnd.handleInspectorResponseOrNotification(message);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
	}
	
	static Logger logger = LoggerFactory.getLogger(InspectorClient.class);
	
	private static LinkedBlockingQueue<String> messagesFromInspectorFrontEnd = new LinkedBlockingQueue<>();

	public void sendMessage(String message) {
    	messagesFromInspectorFrontEnd.offer(message);
	}
	
	public void runMessageLoop() throws Exception {
		while (true) {
			String message = messagesFromInspectorFrontEnd.poll();
			if (message != null) {
				logger.debug("Processing message: " + message);
				inspector.sendInspectorMessage(message);
			}
			if (quitMessageLoop) {
				quitMessageLoop = false;
				break;
			} else {
				Thread.yield();
			}
		}
	}
	
	public void quitMessageLoopOnPause() {
		quitMessageLoop = true;
	}
	
	public void runMessageLoopOnPause() throws Exception {
		runMessageLoop();
	}

}
