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

import org.sakaiproject.assignment.api.AssignmentSubmissionEdit;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.contentreview.model.ContentReviewItem;
import org.sakaiproject.contentreview.service.ContentReviewService;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

/** 
 * This servlet will receive callbacks from TII. Then it will process the data
 * related to the submissions and resubmission and store it.
 */

@SuppressWarnings("deprecation")
public class SubmissionCallbackServlet extends HttpServlet {
	
	private static Log M_log = LogFactory.getLog(SubmissionCallbackServlet.class);
	
	private ContentReviewService contentReviewService =
				(ContentReviewService)ComponentManager.get("org.sakaiproject.contentreview.service.ContentReviewService");
				
	@Override
	public void init(ServletConfig config) throws ServletException {
		M_log.debug("init SubmissionCallbackServlet");
		super.init(config);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		M_log.debug("doGet SubmissionCallbackServlet");
		doPost(request, response);
	}
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		M_log.debug("doPost SubmissionCallbackServlet");
		String contentType = request.getContentType();
		if ( contentType != null && contentType.startsWith("application/json") ) {
			doPostJSON(request, response);
		} else {
			M_log.warn("SubmissionCallbackServlet received a not json call.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@SuppressWarnings("unchecked")
    protected void doPostJSON(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
		M_log.debug("doPostJSON SubmissionCallbackServlet");
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
		String submissionId = json.getString("lis_result_sourcedid");
		int tiiPaperId = json.getInt("paperid");
		//ext_outcomes_tool_placement_url parameter can also be processed if necessary
		try{
			Session session = SessionManager.getCurrentSession();
			session.setUserId("admin");
			ContentReviewItem cri = contentReviewService.getFirstItemByContentId(submissionId);
			if(cri == null){
				M_log.debug("Could not find the content review item for content " + submissionId);
				return;
			} else {
				ContentResourceEdit cre = ContentHostingService.editResource(cri.getContentId());
				M_log.debug("Got content resource");
				ResourcePropertiesEdit aPropertiesEdit = cre.getPropertiesEdit();
				aPropertiesEdit.addProperty("turnitin_id", String.valueOf(tiiPaperId));
				ContentHostingService.commitResource(cre, NotificationService.NOTI_NONE);//TODO check
				M_log.debug("Successfully stored external id into content resource.");
				//NOTE: storing it on the submission too, resubmission process has to be revised
				AssignmentSubmissionEdit ase = AssignmentService.editSubmission(submissionId);
				aPropertiesEdit = ase.getPropertiesEdit();
				aPropertiesEdit.addProperty("turnitin_id", String.valueOf(tiiPaperId));
				AssignmentService.commitEditFromCallback(ase);
			}
		}catch(Exception e){
			M_log.error("Could not find submission with id " + submissionId + " or store the TII submission id: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

        return;
    }
	
}