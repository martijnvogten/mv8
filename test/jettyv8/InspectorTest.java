package jettyv8;

import org.junit.Test;

import jettyv8.server.DebugServer;

public class InspectorTest {
	
	public static void main(String args[]) throws Exception {
		String script = 
				"const a = 42; \n" +
				"debugger;";
		
		DebugServer.main(new String[] {"2992", script});
	}
	
	@Test
	public void testInspector() {
	}
}
