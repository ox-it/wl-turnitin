package org.sakaiproject.contentreview.mocks;

import java.util.Collection;
import java.util.List;
import java.util.Stack;
/*import java.util.Date;

import org.sakaiproject.user.api.User;*/
import org.sakaiproject.entity.api.ResourceProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContent;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;

public class FakeAssignment implements Assignment {
	
	private String id;
	private String title;
	private AssignmentContent ac;
	
	public FakeAssignment(String id) {
		this.id = id;
		this.title = id;
	}
	
	public String getId() {
		return id;
	}
	
	public Element toXml(Document doc, Stack stack){
		return null;
	}
	
	public ResourceProperties getProperties()
	{
		return null;
	}
	
	public String getReference()
	{
		return null;
	}

	public String getReference(String rootProperty)
	{
		return null;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String arg0) {
		this.title = arg0;
	}
	
	public String getContentReference(){
		return null;
	}
	
	public Time getOpenTime(){
		return null;
	}
	
	public String getOpenTimeString(){
		return null;
	}
	
	public Time getDueTime(){
		return null;
	}
	
	public Time getVisibleTime(){
		return null;
	}
	
	public String getVisibleTimeString(){
		return null;
	}
	
	public String getDueTimeString(){
		return null;
	}
	
	public Time getDropDeadTime(){
		return null;
	}
	
	public String getDropDeadTimeString(){
		return null;
	}
	
	public Time getCloseTime(){
		return null;
	}
	
	public String getCloseTimeString(){
		return null;
	}
	
	public String getSection(){
		return null;
	}
	
	public boolean isGroup(){
		return false;
	}
	
	public int getPosition_order(){
		return 0;
	}
	
	public String getStatus(){
		return null;
	}
	
	public String getAuthorLastModified(){
		return null;
	}
	
	public Time getTimeLastModified(){
		return null;
	}
	
	public List getAuthors(){
		return null;
	}
	
	public Time getTimeCreated(){
		return null;
	}
	
	public String getCreator(){
		return null;
	}
	
	public boolean getDraft(){
		return false;
	}
	
	public AssignmentContent getContent(){
		return ac;
	}
	
	public void setContent(AssignmentContent ac){
		this.ac = ac;
	}
	
	protected AssignmentAccess aa = AssignmentAccess.SITE;
	public AssignmentAccess getAccess(){
		return aa;
	}
	
	public String getUrl(String rootProperty)
	{
		return null;
	}
	
	public int compareTo(Object obj){
		return 0;
	}
	
	public String getUrl()
	{
		return null;
	}
	
	public String getContext(){
		return null;
	}
	
	public Collection getGroups(){
		return null;
	}
	
	public Time getPeerAssessmentPeriod(){
		return TimeService.newTime();
	}
	
	public int getPeerAssessmentNumReviews(){
		return 0;
	}

	public String getPeerAssessmentInstructions(){
		return null;
	}
	
	public boolean getPeerAssessmentAnonEval(){
		return false;
	}
	
	public boolean getPeerAssessmentStudentViewReviews(){
		return false;
	}
	
	public boolean getAllowPeerAssessment(){
		return false;
	}
	
	public boolean isPeerAssessmentOpen(){
		return false;
	}

	public boolean isPeerAssessmentPending(){
		return false;
	}
	
	public boolean isPeerAssessmentClosed(){
		return false;
	}
}