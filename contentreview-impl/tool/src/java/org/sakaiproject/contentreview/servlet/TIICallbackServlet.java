package org.sakaiproject.contentreview.servlet;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentEdit;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

/** 
 * This servlet will receive callbacks from TII. Then it will process the data
 * related to the assignment and submissions and store it.
 *
 * TODO complete
 */

@SuppressWarnings("deprecation")
public class TIICallbackServlet extends HttpServlet {
	
	private static Log M_log = LogFactory.getLog(TIICallbackServlet.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		M_log.debug("init TIICallbackServlet");
		super.init(config);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		M_log.debug("doGet TIICallbackServlet");
		doPost(request, response);
	}
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		M_log.debug("doPost TIICallbackServlet");
		String contentType = request.getContentType();
		if ( contentType != null && contentType.startsWith("application/json") ) {
			doPostJSON(request, response);
		} else M_log.warn("TIICallbackServlet received a not json call.");
	}
	
	@SuppressWarnings("unchecked")
    protected void doPostJSON(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
		M_log.debug("doPostJSON TIICallbackServlet");
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		M_log.debug(sb.toString());
		
		JSONObject json = new JSONObject(sb.toString());
		String assignmentId = json.getString("resource_link_id");
		int tiiId = json.getInt("assignmentid");
		//ext_resource_tool_placement_url parameter can also be processed if necessary
		try{
			Session session = SessionManager.getCurrentSession();
			session.setUserId("admin");
			AssignmentEdit ae = AssignmentService.editAssignment(assignmentId);
			ResourcePropertiesEdit aPropertiesEdit = ae.getPropertiesEdit();
			aPropertiesEdit.addProperty("turnitin_id", String.valueOf(tiiId));
			AssignmentService.commitEdit(ae);
		}catch(Exception e){
			M_log.error("Could not find assignment " + assignmentId + " or store the TII assignment id.");
		}

        return;
    }
	
}