package org.sakaiproject.contentreview.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.contentreview.service.ContentReviewService;
import org.sakaiproject.contentreview.service.ContentReviewSiteAdvisor;
import org.sakaiproject.contentreview.impl.turnitin.TurnitinAccountConnection;
import org.sakaiproject.contentreview.impl.turnitin.TurnitinReviewServiceImpl;
import org.sakaiproject.contentreview.impl.advisors.SitePropertyAdvisor;
import org.sakaiproject.contentreview.mocks.FakeAssignment;
import org.sakaiproject.contentreview.mocks.FakeSiteAdvisor;
import org.sakaiproject.contentreview.mocks.FakeSite;
import org.sakaiproject.contentreview.mocks.FakeTiiUtil;
import org.sakaiproject.lti.api.LTIService;
import org.sakaiproject.turnitin.util.TurnitinLTIUtil;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import static org.easymock.EasyMock.*;
import static org.mockito.Mockito.*;

public class TurnitinImplTest extends AbstractTransactionalSpringContextTests {
	private static final Log log = LogFactory.getLog(TurnitinImplTest.class);
	
	private SiteService	M_ss;
	private LTIService	M_lti;
	private FakeTiiUtil M_util;
	private ServerConfigurationService M_conf;
	private SessionManager M_man;
	private SecurityService M_sec;
	private TurnitinLTIUtil turnitinLTIUtil;
	
	protected String[] getConfigLocations() {
	      // point to the needed spring config files, must be on the classpath
	      // (add component/src/webapp/WEB-INF to the build path in Eclipse),
	      // they also need to be referenced in the maven file
	      return new String[] {"hibernate-test.xml", "spring-hibernate.xml"};
	   }

	
	public void testFileEscape() {
		TurnitinReviewServiceImpl tiiService = new TurnitinReviewServiceImpl();
		String someEscaping = tiiService.escapeFileName("Practical%203.docx", "contentId");
		assertEquals("Practical_3.docx", someEscaping);
		
		someEscaping = tiiService.escapeFileName("Practical%203%.docx", "contentId");
		assertEquals("contentId", someEscaping);
		
		someEscaping = tiiService.escapeFileName("Practical3.docx", "contentId");
		assertEquals("Practical3.docx", someEscaping);
		
		
	}
	
	public void testCreateAssignment() throws Exception {
		
		M_util = new FakeTiiUtil();
		M_ss = createMock(SiteService.class);
		M_lti = createMock(LTIService.class);
		M_util.setLtiService(M_lti);

		TurnitinReviewServiceImpl tiiService = new TurnitinReviewServiceImpl();

		FakeSiteAdvisor siteAdvisor = new FakeSiteAdvisor();
		tiiService.setSiteService(M_ss);

		Map opts = new HashMap();        
        opts.put("submit_papers_to", "0");
		opts.put("report_gen_speed", "1");
        opts.put("institution_check", "0");
        opts.put("internet_check", "0");
        opts.put("journal_check", "0");
        opts.put("s_paper_check", "0");
        opts.put("s_view_report", "0");
		opts.put("allow_any_file", "0");
       	opts.put("exclude_biblio", "0");
		opts.put("exclude_quoted", "0");    
        opts.put("exclude_type", "1");
        opts.put("exclude_value", "5");
		opts.put("late_accept_flag", "1");
        SimpleDateFormat dform = ((SimpleDateFormat) DateFormat.getDateInstance());
		dform.applyPattern("yyyy-MM-dd'T'HH:mm");
		opts.put("isostart", dform.format(new Date()));
		opts.put("isodue", dform.format(new Date()));
		opts.put("title", "Title");
		opts.put("descr", "Instructions");
		opts.put("points", 100);		
		
		Site siteA = new FakeSite("siteId");
		
		expect(M_ss.getSite("siteId")).andStubReturn(siteA);
		replay(M_ss);
		tiiService.setSiteAdvisor(siteAdvisor);
		
		TurnitinAccountConnection tac = new TurnitinAccountConnection();
		tac.setUseSourceParameter(false);
		tiiService.setTurnitinConn(tac);
		
		M_conf = createMock(ServerConfigurationService.class);
		tiiService.setServerConfigurationService(M_conf);
		M_man = createMock(SessionManager.class);
		tiiService.setSessionManager(M_man);
		M_sec = createMock(SecurityService.class);
		tiiService.setSecurityService(M_sec);
		tiiService.setTiiUtil(M_util);
		tiiService.createAssignment("siteId", "taskId", opts);		
		
	}
	
}
