package jettyv8.server;

public class IsolateMetadata {
	String description = "V8 isolate";
	String faviconUrl = "https://nodejs.org/static/favicon.ico";
	String type = "node";
	String url = "file://";
	
	String devtoolsFrontendUrl;
	String id;
	String title;
	String webSocketDebuggerUrl;

	static IsolateMetadata create(String title, String hostName, int port, String id, String urlPath) {
		IsolateMetadata result = new IsolateMetadata();
		String urlWithoutProtocol = "//" + hostName + ":" + port + urlPath;
		result.webSocketDebuggerUrl = "ws:" + urlWithoutProtocol;
		result.devtoolsFrontendUrl = "chrome-devtools://devtools/bundled/inspector.html?experiments=true&v8only=true&ws="
				+ urlWithoutProtocol;
		result.id = id;
		result.title = title;
		return result;
	}
}
