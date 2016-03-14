package org.sakaiproject.contentreview.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.edu.person.SakaiPerson;
import org.sakaiproject.api.common.edu.person.SakaiPersonManager;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContent;
import org.sakaiproject.assignment.api.AssignmentContentEdit;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.contentreview.model.ContentReviewItem;
import org.sakaiproject.contentreview.dao.impl.ContentReviewDao;
import org.sakaiproject.contentreview.impl.turnitin.TurnitinAccountConnection;
import org.sakaiproject.contentreview.impl.turnitin.TurnitinReviewServiceImpl;
import org.sakaiproject.contentreview.mocks.FakeAssignment;
import org.sakaiproject.contentreview.mocks.FakeAssignmentContent;
import org.sakaiproject.contentreview.mocks.FakeAssignmentContentEdit;
import org.sakaiproject.contentreview.mocks.FakeSiteAdvisor;
import org.sakaiproject.contentreview.mocks.FakeSite;
import org.sakaiproject.contentreview.mocks.FakeTiiUtil;
/*import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;*/
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.lti.api.LTIService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.turnitin.util.TurnitinLTIUtil;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import static org.mockito.Mockito.*;

public class TurnitinImplTest extends AbstractTransactionalSpringContextTests {
	
	private static final Log log = LogFactory.getLog(TurnitinImplTest.class);
	
	private AssignmentService M_assi;
	private SiteService	M_ss;
	private LTIService	M_lti;
	private FakeTiiUtil M_util;
	private ServerConfigurationService M_conf;
	private SessionManager M_man;
	private SecurityService M_sec;
	private TurnitinLTIUtil turnitinLTIUtil;
	private ContentReviewDao M_dao;
	private UserDirectoryService M_use;
	private SakaiPersonManager M_per;
	private ContentHostingService M_con;
	
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
		M_assi = createMock(AssignmentService.class);
		M_ss = createMock(SiteService.class);
		M_lti = createMock(LTIService.class);
		M_util.setLtiService(M_lti);

		TurnitinReviewServiceImpl tiiService = new TurnitinReviewServiceImpl();

		FakeSiteAdvisor siteAdvisor = new FakeSiteAdvisor();
		tiiService.setSiteService(M_ss);
		tiiService.setAssignmentService(M_assi);

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
		
		Assignment assignA = new FakeAssignment("taskId");
		List l = new ArrayList();
		l.add(assignA);
		AssignmentContent contentA = new FakeAssignmentContent("taskId");
		expect(M_assi.getAssignmentContent("taskId")).andStubReturn(contentA);
		AssignmentContentEdit contentEdA = new FakeAssignmentContentEdit("taskId");
		expect(M_assi.editAssignmentContent("taskId")).andStubReturn(contentEdA);
		expect(M_assi.getAssignments(contentA)).andStubReturn(l.iterator());
		M_assi.commitEdit(contentEdA);
		EasyMock.expectLastCall();
		expect(M_assi.getSubmissions(assignA)).andStubReturn(null);
		replay(M_assi);
		
		TurnitinAccountConnection tac = new TurnitinAccountConnection();
		tac.setUseSourceParameter(false);
		/*tac.setDefaultInstructorEmail("defaultmail");
		tac.setDefaultInstructorFName("defaultname");
		tac.setDefaultInstructorLName("defaullastname");
		tac.setDefaultInstructorId("defaultid");*/
		tiiService.setTurnitinConn(tac);
		
		M_conf = createMock(ServerConfigurationService.class);
		tiiService.setServerConfigurationService(M_conf);
		expect(M_conf.getServerUrl()).andStubReturn("http://serverurl");
		expect(M_conf.getInt("contentreview.instructions.max", 1000)).andStubReturn(1000);
		replay(M_conf);
		
		M_man = createMock(SessionManager.class);
		tiiService.setSessionManager(M_man);
		M_sec = createMock(SecurityService.class);
		tiiService.setSecurityService(M_sec);
		tiiService.setTiiUtil(M_util);
		tiiService.createAssignment("siteId", "taskId", opts);
		
	}
	
	private String newDefaultValue = "Changed";
	
	public void testProcessQueue() throws Exception {
		
		TurnitinReviewServiceImpl tiiService = new TurnitinReviewServiceImpl();
	
		M_conf = createMock(ServerConfigurationService.class);
		expect(M_conf.getServerId()).andStubReturn("serverId");
		expect(M_conf.getServerUrl()).andStubReturn("http://serverurl");
		tiiService.setServerConfigurationService(M_conf);
		replay(M_conf);
		
		M_use = createMock(UserDirectoryService.class);
		//M_use = (UserDirectoryService)getService(UserDirectoryService.class.getName());
  
		User user = createMock(User.class);
		expect(user.getEid()).andStubReturn("userId");
		expect(user.getId()).andStubReturn("userId");
		expect(user.getFirstName()).andStubReturn("First");
		expect(user.getLastName()).andStubReturn("Last");
		expect(user.getEmail()).andStubReturn("email@mail.es");
		replay(user);
		expect(M_use.getUser("userId")).andStubReturn(user);
		tiiService.setUserDirectoryService(M_use);
		replay(M_use);
		
		M_per = createMock(SakaiPersonManager.class);
		tiiService.setSakaiPersonManager(M_per);
		SakaiPerson person = createMock(SakaiPerson.class);
		expect(M_per.getSystemMutableType()).andStubReturn(null);
		expect(M_per.getSakaiPerson("userId",null)).andStubReturn(person);
		replay(M_per);
		
		M_ss = createMock(SiteService.class);
		tiiService.setSiteService(M_ss);
		Site siteA = new FakeSite("siteId");
		expect(M_ss.getSite("siteId")).andStubReturn(siteA);
		replay(M_ss);
		
		M_con = createMock(ContentHostingService.class);
		tiiService.setContentHostingService(M_con);
		ContentResource r = createMock(ContentResource.class);
		ResourceProperties rp = createMock(ResourceProperties.class);
		expect(M_con.getResource("contentId")).andStubReturn(r);
		expect(r.getProperties()).andStubReturn(rp);
		expect(r.getId()).andStubReturn("contentId");
		expect(rp.getNamePropDisplayName()).andStubReturn("displayName");
		expect(rp.getProperty("displayName")).andStubReturn("fileName");
		expect(rp.getProperty("turnitin_id")).andStubReturn("123456");
		replay(M_con);
		replay(r);
		replay(rp);
		
		FakeSiteAdvisor siteAdvisor = new FakeSiteAdvisor();
		tiiService.setSiteAdvisor(siteAdvisor);
		
		M_assi = createMock(AssignmentService.class);
		tiiService.setAssignmentService(M_assi);
		org.sakaiproject.assignment.api.Assignment assignA = createMock(org.sakaiproject.assignment.api.Assignment.class);
		expect(M_assi.getAssignment("taskId")).andStubReturn(assignA);
		AssignmentContent contentA = createMock(AssignmentContent.class);
		expect(assignA.getContent()).andStubReturn(contentA);
		ResourceProperties rp2 = createMock(ResourceProperties.class);
		expect(contentA.getProperties()).andStubReturn(rp2);
		expect(rp2.getProperty("turnitin_id")).andStubReturn("123456");
		replay(M_assi);
		replay(assignA);
		replay(contentA);
		replay(rp2);
		
		FakeTiiUtil M_util = new FakeTiiUtil();
		tiiService.setTiiUtil(M_util);
		
		TurnitinAccountConnection tac = new TurnitinAccountConnection();
		tac.setUseSourceParameter(false);
		tiiService.setTurnitinConn(tac);
		
		M_dao = (ContentReviewDao)applicationContext.getBean("org.sakaiproject.contentreview.dao.ContentReviewDao");
		if (M_dao == null) {
			throw new NullPointerException("DAO could not be retrieved from spring context");
		}

		tiiService.setDao(M_dao);

		/*ContentReviewItem item = new ContentReviewItem("userId", "siteId", "taskId", "contentId", new Date(), ContentReviewItem.NOT_SUBMITTED_CODE);
		item.setNextRetryTime(new Date());
		item.setUrlAccessed(false);
		item.setSubmissionId("submissionId");*/
		
		//TEST 1
		tiiService.queueContent("userId", "siteId", "taskId", "contentId", "submissionId");	
		
		//TEST 2
		tiiService.processQueue();//it will retrieve the previously queued content
		
		List<ContentReviewItem> awaitingReport = M_dao.findByProperties(ContentReviewItem.class,
				new String[] { "status" }, new Object[] { ContentReviewItem.SUBMITTED_AWAITING_REPORT_CODE});
		Iterator<ContentReviewItem> listIterator = awaitingReport.iterator();
		ContentReviewItem currentItem;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -30);
		Date dateBefore30Days = cal.getTime();
		tiiService.setMaxRetry(Long.valueOf(100));
		while (listIterator.hasNext()) {
			currentItem = (ContentReviewItem) listIterator.next();
			currentItem.setNextRetryTime(dateBefore30Days);
			M_dao.update(currentItem);
		}
		
		//TEST 3
		tiiService.checkForReports();//it will retrieve the previously submitted content
	}

}
