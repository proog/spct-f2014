package dk.itu.spct.f2014.ma03.pmor.janv.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

@SuppressWarnings("serial")
public class MovementServletServlet extends HttpServlet {
	private Entity parent = new Entity("Measurement", "Measurement"); 
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		String output = "<!DOCTYPE html><html><body>";
		
		Query query = new Query("Measurement").addSort("timestamp", SortDirection.DESCENDING);
		List<Entity> list = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		if (list.size() > 0) {
			output = output + "<table>";
			
			for (int i = 0; i < list.size(); i++ ) {
				output = output + "<tr><td>" + list.get(i).getProperty("timestamp").toString() + 
						"</td><td>" + list.get(i).getProperty("elemvalue").toString() + "</td></tr>";
			}
			
			output = output + "</table>";
		}
		else {
			output = output + "<p>Database Empty</p>";
		}
	    
		output = output + "</body></html>";
	    resp.setContentType("text/html");
	    resp.getWriter().println(output);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String timestamp = req.getParameter("timestamp");
		String elemvalue = req.getParameter("elemvalue");
		Entity measurement = new Entity("Measurement", parent.getKey());
		measurement.setProperty("timestamp", timestamp);
		measurement.setProperty("elemvalue", elemvalue);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(measurement);
	}
	
}
