package org.sakaiproject.contentreview.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContent;
import org.sakaiproject.assignment.api.AssignmentSubmissionEdit;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.contentreview.model.ContentReviewItem;
import org.sakaiproject.contentreview.service.ContentReviewService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

/** 
 * This servlet will receive callbacks from TII. Then it will process the data
 * related to the grades and store it.
 */

@SuppressWarnings("deprecation")
public class GradingCallbackServlet extends HttpServlet {
	
	private static Log M_log = LogFactory.getLog(GradingCallbackServlet.class);
	
	private ContentReviewService contentReviewService =
				(ContentReviewService)ComponentManager.get("org.sakaiproject.contentreview.service.ContentReviewService");
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		M_log.debug("init GradingCallbackServlet");
		super.init(config);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		M_log.debug("doGet GradingCallbackServlet");
		doPost(request, response);
	}
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		M_log.debug("doPost GradingCallbackServlet");
		String contentType = request.getContentType();
		if ( contentType != null && contentType.startsWith("application/xml") ) {
			doPostXml(request, response);
		} else {
			M_log.warn("GradingCallbackServlet received a not json call.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@SuppressWarnings("unchecked")
    protected void doPostXml(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
		M_log.debug("doPostXml GradingCallbackServlet");
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
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		String sourcedId = null;
        try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(sb.toString())));
			Element doc = document.getDocumentElement();
			sourcedId = doc.getElementsByTagName("sourcedId").item(0).getChildNodes().item(0).getNodeValue();
			String score = doc.getElementsByTagName("textString").item(0).getChildNodes().item(0).getNodeValue();
			M_log.debug("sourcedId " + sourcedId + ", score " + score);
					
			Session session = SessionManager.getCurrentSession();
			session.setUserId("admin");
			if(contentReviewService == null){
				M_log.warn("Can't find contentReviewService");
				return;
			}
			ContentReviewItem cri = contentReviewService.getFirstItemByContentId(sourcedId);
			if(cri == null){
				M_log.debug("Could not find the content review item for content " + sourcedId);
				return;
			} else {
				Assignment a = AssignmentService.getAssignment(cri.getTaskId());
				AssignmentContent ac = a.getContent();
				if(ac == null){
					M_log.debug("Could not find the assignment content " + cri.getTaskId());
					return;
				} else {
					M_log.debug("Got assignment content " + cri.getTaskId());
				}
				/* TODO on trunk more than one decimal is possible
				int factor = AssignmentService.getScaleFactor();
				int dec = (int)Math.log10(factor);
				int maxPoints = assign.getMaxGradePoint() / dec;*/
				int maxPoints = ac.getMaxGradePoint() / 10;
				float convertedScore = Float.valueOf(score)*maxPoints;
				String convertedScoreString = String.valueOf(convertedScore);
				M_log.debug("Maximum points: " + maxPoints + " - converted score: " + convertedScoreString);
				
				M_log.debug("cri " + cri.getId() + " - " + cri.getContentId());
				boolean itemUpdated = contentReviewService.updateExternalGrade(cri.getContentId(), convertedScoreString);
				if(!itemUpdated){
					M_log.error("Could not update cr item external grade");
					return;
				}
				
				if(convertedScore >= 0){
					if(ac == null || ac.getTypeOfSubmission() != 5){
						M_log.debug("Could not get assignment content or type setting for task " + cri.getTaskId());
						return;
					} else {
						AssignmentSubmissionEdit ase = AssignmentService.editSubmission(cri.getSubmissionId());
						if(ase != null){
							String assignmentGrade = ase.getGrade();
							if(StringUtils.isEmpty(assignmentGrade)){
								M_log.debug("Setting external grade as assignments grade");
								ase.setGrade(convertedScoreString);								
							} else {
								M_log.debug("Flagging submssion");
								ase.setExternalGradeDifferent(Boolean.TRUE.booleanValue());
							}
							AssignmentService.commitEditFromCallback(ase);
						}
					}
				}
			}
		} catch(ParserConfigurationException pce){
			M_log.error("Could not parse TII response (ParserConfigurationException): " + pce.getMessage());
		} catch(SAXException se){
			M_log.error("Could not parse TII response (SAXException): " + se.getMessage());
		} catch(Exception e){
			M_log.error("Could not update the content review item " + sourcedId);
		}

        return;
    }

}