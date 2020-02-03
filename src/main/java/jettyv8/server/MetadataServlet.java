package jettyv8.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class MetadataServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private Callable<List<IsolateMetadata>> isolatesMetadataProvider;
	
	public MetadataServlet(Callable<List<IsolateMetadata>> isolatesMetadataProvider) {
		super();
		this.isolatesMetadataProvider = isolatesMetadataProvider;
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
