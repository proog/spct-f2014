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
		Query query = new Query("Measurement").addSort("timestamp", SortDirection.DESCENDING);
		List<Entity> list = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		String output = "<!DOCTYPE html><html><body>";
		
		if (!list.isEmpty()) {
			output = output + "<table>";
			
			for (Entity e : list) {
				output = output + "<tr><td>" + e.getProperty("timestamp").toString() + "</td><td>"
											+ e.getProperty("elemvalue").toString() + "</td></tr>";
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
		String type = req.getParameter("type");
		String xAxis = req.getParameter("x_acc");
		String yAxis = req.getParameter("y_acc");
		String zAxis = req.getParameter("z_acc");
		
		Entity measurement = new Entity("Measurement", parent.getKey());
		measurement.setProperty("timestamp", timestamp);
		measurement.setProperty("type", type);
		measurement.setProperty("x_acc", xAxis);
		measurement.setProperty("y_acc", yAxis);
		measurement.setProperty("z_acc", zAxis);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(measurement);
	}
	
}
