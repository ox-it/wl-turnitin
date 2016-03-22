package org.sakaiproject.contentreview.servlet;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.lti.api.LTIService;

public class ServletUtils {

	private static Log M_log = LogFactory.getLog(ServletUtils.class);

	public static Map<String,Object> obtainGlobalTurnitinLTITool(String turnitinSite){
		LTIService  ltiService = (LTIService) ComponentManager.get(LTIService.class);
		Objects.requireNonNull(ltiService);
		M_log.debug("Getting global TII LTI tool");
		List<Map<String, Object>> tools = ltiService.getToolsDao("lti_tools.site_id = '"+turnitinSite+"'", null, 0, 0, turnitinSite);
		if ( tools == null || tools.size() != 1 ) {
			String warnMessage = "";
			if(tools == null){
				warnMessage = "no tools found";
			} else {
				warnMessage = "found: " + tools.size();
			}
			M_log.error("obtainGlobalTurnitinLTIToolData: wrong global TII LTI tool configuration - " + warnMessage);
			return null;
		}
		return tools.get(0);
	}

}