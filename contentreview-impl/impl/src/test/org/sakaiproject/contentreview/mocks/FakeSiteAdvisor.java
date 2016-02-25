package org.sakaiproject.contentreview.mocks;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.contentreview.service.ContentReviewSiteAdvisor;

public class FakeSiteAdvisor implements ContentReviewSiteAdvisor{
	
	public boolean siteCanUseReviewService(Site site){
		return true;
	}
	
	public boolean siteCanUseLTIReviewService(Site site){
		return true;
	}
	
	public boolean siteCanUseLTIDirectSubmission(Site site){
		return false;
	}
}