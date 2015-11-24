package org.sakaiproject.turnitin.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.imsglobal.basiclti.BasicLTIUtil;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lti.api.LTIService;

/**
 * This is a utility class for wrapping the LTI calls to the TurnItIn Service.
 * 
 * @author bgarcia
 *
 */
public class TurnitinLTIUtil {
	private static final Log log = LogFactory.getLog(TurnitinLTIUtil.class);
	
	private static final Log apiTraceLog = LogFactory.getLog("org.sakaiproject.turnitin.util.TurnitinLTIUtil.apicalltrace");

	public static final int BASIC_ASSIGNMENT = 0;
	public static final int EDIT_ASSIGNNMENT = 1;
	public static final int INFO_ASSIGNNMENT = 2;
	public static final int SUBMIT = 3;
	public static final int RESUBMIT = 4;
	public static final int INFO_SUBMISSION = 5;
	
	private static final String basicAssignmentUrl = "https://sandbox.turnitin.com/api/lti/1p0/assignment";
	private static final String editAssignmentUrl = "https://sandbox.turnitin.com/api/lti/1p0/assignment/edit/";//assignment_id
	private static final String infoAssignmentUrl = "https://sandbox.turnitin.com/api/lti/1p0/resource_link_tool/";//assignment_id
	private static final String submitUrl = "https://sandbox.turnitin.com/api/lti/1p0/upload/submit/";//assignment_id
	private static final String resubmitUrl = "https://sandbox.turnitin.com/api/lti/1p0/upload/resubmit/";//submission_id
	private static final String infoSubmissionUrl = "https://sandbox.turnitin.com/api/lti/1p0/outcome_tool_data/";//submission_id
	
	private String said = null;
	private String secret = null;
	private String globalId = null;
	
	private LTIService ltiService;
	public void setLtiService(LTIService ltiService) {
		this.ltiService = ltiService;
	}
	
	public void init() {
		log.debug("init - TurnitinLTIUtil");
		if(ltiService == null)
			log.warn("TurnitinLTIUtil: Could not find LTI service.");
		
		said = ServerConfigurationService.getString("turnitin.said");
		secret = ServerConfigurationService.getString("turnitin.secretKey");
		globalId = ServerConfigurationService.getString("turnitin.global.tool");
	}
	
	public boolean makeLTIcall(int type, String urlParam, Map<String, String> ltiProps){
		try {
	        HttpClient client = new HttpClient();
			Map<String,String> extra = new HashMap<String,String> ();
			
			String defUrl = formUrl(type, urlParam);
			if(defUrl == null){
				log.error("Error while getting TII LTI url.");//TODO params
				return false;
			}
			
			PostMethod method = new PostMethod(defUrl);
			ltiProps = BasicLTIUtil.signProperties(ltiProps, defUrl, "POST", said, secret, null, null, null, null, null, extra);
			if(ltiProps == null){
				log.error("Error while signing TII LTI properties.");//TODO params
				return false;
			}
			
			for (Entry<String, String> entry : ltiProps.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (value == null)
					continue;
				method.addParameter(key,value);
			}
			log.debug("ltiprops " + ltiProps.toString());
			
			int statusCode = client.executeMethod(method);//TODO process values
			log.debug("status: " + statusCode + " - " + method.getStatusText());
	        log.debug(method.getResponseBodyAsString());
		
		} catch (Exception e) {
			log.error("Exception while making TII LTI call " + e.getMessage());//TODO addparams
	        e.printStackTrace();
			return false;
	    }
		
		return true;//TODO
	}
	
	public String getGlobalTurnitinLTIToolId(){
		//TODO different way to generate or obtain it
		return globalId;
	}
	
	public Object insertTIIToolContent(String globalToolId, Properties props){
		if(ltiService == null){
			log.error("insertTIIToolContent: Could not find LTI service.");
			return null;
		}
		return ltiService.insertTIIToolContent(null, globalToolId, props);
	}
	
	private String formUrl(int type, String urlParam){
		switch(type){
			case BASIC_ASSIGNMENT:
				return basicAssignmentUrl;
			case EDIT_ASSIGNNMENT:
				return editAssignmentUrl+urlParam;
			case INFO_ASSIGNNMENT:
				return infoAssignmentUrl+urlParam;
			case SUBMIT:
				return submitUrl+urlParam;
			case RESUBMIT:
				return resubmitUrl+urlParam;
			case INFO_SUBMISSION:
				return infoSubmissionUrl+urlParam;
			default:
				return null;
		}
	}
	
}
