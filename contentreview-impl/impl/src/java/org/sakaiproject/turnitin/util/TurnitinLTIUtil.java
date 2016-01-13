package org.sakaiproject.turnitin.util;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.imsglobal.basiclti.BasicLTIUtil;
import org.sakaiproject.component.api.ServerConfigurationService;
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
	
	private static final String basicAssignmentUrl = "assignment";
	private static final String editAssignmentUrl = "assignment/edit/";//assignment_id
	private static final String infoAssignmentUrl = "resource_link_tool/";//assignment_id
	private static final String submitUrl = "upload/submit/";//assignment_id
	private static final String resubmitUrl = "upload/resubmit/";//submission_id
	private static final String infoSubmissionUrl = "outcome_tool_data/";//submission_id
	
	private String said = null;
	private String secret = null;
	private String globalId = null;
	private String endpoint = null;
	private String turnitinSite = null;
	
	private LTIService ltiService;
	public void setLtiService(LTIService ltiService) {
		this.ltiService = ltiService;
	}
	
	private ServerConfigurationService serverConfigurationService;
	public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}
	
	public void init() {
		log.debug("init - TurnitinLTIUtil");
		if(ltiService == null)
			log.warn("TurnitinLTIUtil: Could not find LTI service.");
		
		said = serverConfigurationService.getString("turnitin.aid");
		secret = serverConfigurationService.getString("turnitin.secretKey");
		endpoint = serverConfigurationService.getString("turnitin.ltiURL", "https://sandbox.turnitin.com/api/lti/1p0/");		
		turnitinSite = serverConfigurationService.getString("turnitin.lti.site", "!turnitin");
	}
	
	public boolean makeLTIcall(int type, String urlParam, Map<String, String> ltiProps){
		try {
	        
			HttpClientParams httpParams = new HttpClientParams();
			httpParams.setConnectionManagerTimeout(60000);
			HttpClient client = new HttpClient();
			client.setParams(httpParams);
			client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
			
			//Map<String,String> extra = new HashMap<String,String> ();
			String extra = "";
			
			String defUrl = formUrl(type, urlParam);
			if(defUrl == null){
				log.error("Error while getting TII LTI url.");//TODO params
				return false;
			}
			
			PostMethod method = new PostMethod(defUrl);
			//ltiProps = BasicLTIUtil.signProperties(ltiProps, defUrl, "POST", said, secret, null, null, null, null, null, extra);
			ltiProps = BasicLTIUtil.signProperties(ltiProps, defUrl, "POST", said, secret, null, null, null, null, extra);
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
			
			int statusCode = client.executeMethod(method);//TODO process values
			if(statusCode == 400){
				log.warn("Status 400: Bad request: " + defUrl);
				log.warn("LTI props " + ltiProps.toString());
				return false;
			} else if(statusCode == 200){//OJO para el submit el estado correcto es 200
				log.debug("Status 200: OK request: " + defUrl);
				log.debug("LTI props " + ltiProps.toString());
				log.debug(method.getResponseBodyAsString());
				return true;
			} else if(statusCode == 302){
				log.debug("Successful call: " + defUrl);
				log.debug("LTI props " + ltiProps.toString());
				log.debug(method.getResponseBodyAsString());
				return true;
			} else {
				log.warn("Not controlled status: " + statusCode + " - " + method.getStatusText());
				log.debug("LTI props " + ltiProps.toString());
				log.debug(method.getResponseBodyAsString());
			}
		
		} catch (Exception e) {
			log.error("Exception while making TII LTI call " + e.getMessage());//TODO addparams
			return false;
	    }
		
		return true;//TODO
	}
	
	public String getGlobalTurnitinLTIToolId(){
		if(globalId == null){
			log.debug("Setting global TII LTI tool id");
			List<Map<String, Object>> tools = ltiService.getToolsDao("lti_tools.site_id = '"+turnitinSite+"'", null, 0, 0, turnitinSite);
			if ( tools == null || tools.size() < 1  || tools.size() > 1) {
				if(tools == null)
					log.warn("No tools found");
				else
					log.warn("Found: " + tools.size());				
				log.error("getGlobalTurnitinLTIToolId: wrong global TII LTI tool configuration");
				return null;
			}
			Map<String,Object> tool  = tools.get(0);
			globalId = String.valueOf(tool.get(ltiService.LTI_ID));
			log.debug("Global tool id: " + globalId);
		}
		//TODO we might as well make a resetglobalturnitinid method just in case?
		
		return globalId;
	}
	
	public Object insertTIIToolContent(String globalToolId, Properties props){
		if(ltiService == null){
			log.error("insertTIIToolContent: Could not find LTI service.");
			return null;
		}
		return ltiService.insertToolContent(null, globalToolId, props, "!admin");
	}
	
	private String formUrl(int type, String urlParam){
		switch(type){
			case BASIC_ASSIGNMENT:
				return endpoint+basicAssignmentUrl;
			case EDIT_ASSIGNNMENT:
				return endpoint+editAssignmentUrl+urlParam;
			case INFO_ASSIGNNMENT:
				return endpoint+infoAssignmentUrl+urlParam;
			case SUBMIT:
				return endpoint+submitUrl+urlParam;
			case RESUBMIT:
				return endpoint+resubmitUrl+urlParam;
			case INFO_SUBMISSION:
				return endpoint+infoSubmissionUrl+urlParam;
			default:
				return null;
		}
	}
	
}
