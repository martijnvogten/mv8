package jettyv8.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class MetadataServlet extends DefaultServlet {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final long serialVersionUID = 1L;
	private Callable<List<IsolateMetadata>> isolatesMetadataProvider;
	
	public MetadataServlet(Callable<List<IsolateMetadata>> isolatesMetadataProvider) {
		super();
		this.isolatesMetadataProvider = isolatesMetadataProvider;
	}
	
	public static class IsolateMetadata {
		String description = "node.js instance";
		String devtoolsFrontendUrl = "chrome-devtools://devtools/bundled/inspector.html?experiments=true&v8only=true&ws=//127.0.0.1:9999/ws";
		String faviconUrl = "https://nodejs.org/static/favicon.ico";
		String id = "99de305f-5abc-4d73-b924-387e1ca6ef23";
		String title = "node";
		String type = "node";
		String url = "file://";
		String webSocketDebuggerUrl = "ws://localhost:9999/ws";
		
		static IsolateMetadata create(String title, String hostName, int port, String id, String urlPath) {
			IsolateMetadata result = new IsolateMetadata();
			String urlWithoutProtocol = "//" + hostName + ":" + port + urlPath;
			result.webSocketDebuggerUrl = "ws:" + urlWithoutProtocol;
			result.devtoolsFrontendUrl = "chrome-devtools://devtools/bundled/inspector.html?experiments=true&v8only=true&ws=" + urlWithoutProtocol; 
			result.id = id;
			result.title = title;
			return result;
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		
		String result = null;
		
		switch(request.getRequestURI()) {
		case "/json/list":
		case "/json":
			try {
				result = GSON.toJson(isolatesMetadataProvider.call());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			break;
		case "/json/version":
			JsonObject o = new JsonObject();
			o.addProperty("Browser", "node.js/v8.2.1");
			o.addProperty("Protocol-Version", "1.3");
			result = GSON.toJson(o);
			break;
		default:
			break;
		}
		if (result != null) {
			response.setStatus(200);
			response.getWriter().println(result);
		} else {
			response.sendError(404);
		}
		
	}

}
