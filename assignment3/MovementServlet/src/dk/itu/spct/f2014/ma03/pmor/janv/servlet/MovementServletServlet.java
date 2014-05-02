package dk.itu.spct.f2014.ma03.pmor.janv.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

import org.apache.tools.ant.taskdefs.Filter;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

@SuppressWarnings("serial")
public class MovementServletServlet extends HttpServlet {
	private Entity trainingRecording = new Entity("TrainingRecording", "TrainingRecording");
	private Entity testRecording = new Entity("TestRecording", "TestRecording");
	private String uploadName = "data";
	private String modeName = "mode";
	private String idName = "id";
	private String trainingMode = "training";
	private String testMode = "test";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		String id = req.getParameter(idName);
		if(id != null && !id.isEmpty()) {
			Query q = new Query("TestRecording").setFilter(new FilterPredicate(idName, FilterOperator.EQUAL, id));
			List<Entity> list = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			
			for(Entity e : list)
				resp.getWriter().println(e.getProperty(uploadName));
		}
		else {
			Query q = new Query("TrainingRecording");
			List<Entity> list = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			
			int count = 0;
			for(Entity e : list) {
				String data = e.getProperty(uploadName).toString();
				String[] lines = data.split("\n");
				int start = count == 0 ? 0 : 1;
				for(int i = start; i < lines.length; i++)
					resp.getWriter().println(lines[i]);
				count++;
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		String mode = req.getParameter(modeName);
		Entity e;
		if(mode.equals(testMode)) {
			e = new Entity("TestRecording", testRecording.getKey());
		}
		else {
			e = new Entity("TrainingRecording", trainingRecording.getKey());
		}
		
		String data = req.getParameter(uploadName);
		e.setProperty("id", req.getParameter(idName));
		e.setProperty("data", data);
		datastore.put(e);
	}
	
}
