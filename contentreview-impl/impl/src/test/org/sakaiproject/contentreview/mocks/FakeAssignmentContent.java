package org.sakaiproject.contentreview.mocks;

import java.util.List;
import java.util.Stack;

import org.sakaiproject.assignment.api.AssignmentContent;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.AttachmentContainer;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FakeAssignmentContent implements AssignmentContent {

	private String id;
	private String title;

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

	public FakeAssignmentContent(String id) {
		this.id = id;
		this.title = id;
	}
	
	public String getCreator(){
		return null;
	}

	public String getTitle(){
		return null;
	}

	public String getContext(){
		return null;
	}

	public String getInstructions(){
		return null;
	}

	public Time getTimeCreated(){
		return null;
	}

	public Time getTimeLastModified(){
		return null;
	}

	public String getAuthorLastModified(){
		return null;
	}

	public int getTypeOfSubmission(){
		return 1;
	}

	public int getTypeOfGrade(){
		return 1;
	}


	public String getTypeOfGradeString(int gradeType){
		return null;
	}

	public int getMaxGradePoint(){
		return 1;
	}

	public String getMaxGradePointDisplay(){
		return null;
	}

	public boolean getGroupProject(){
		return false;
	}

	public boolean individuallyGraded(){
		return false;
	}

	public boolean releaseGrades(){
		return false;
	}

	public int getHonorPledge(){
		return 0;
	}

	public boolean getAllowAttachments(){
		return false;
	}
	
	public boolean getAllowReviewService(){
		return false;
	}

	public boolean getAllowStudentViewReport(){
		return false;
	}
	
	public boolean getAllowStudentViewExternalGrade(){
		return false;
	}

	public List getAuthors(){
		return null;
	}

	public boolean inUse(){
		return false;
	}
	
	public String getSubmitReviewRepo(){
		return null;
	}

	public void setSubmitReviewRepo(String m_submitReviewRepo){}

	public String getGenerateOriginalityReport(){
		return null;
	}

	public void setGenerateOriginalityReport(String m_generateOriginalityReport){}

	public boolean isCheckTurnitin(){
		return false;
	}

	public void setCheckTurnitin(boolean m_checkTurnitin){}

	public boolean isCheckInternet(){
		return false;
	}

	public void setCheckInternet(boolean m_checkInternet){}

	public boolean isCheckPublications(){
		return false;
	}

	public void setCheckPublications(boolean m_checkPublications){}

	public boolean isCheckInstitution(){
		return false;
	}

	public boolean getHideDueDate(){
		return false;
	}

	public void setCheckInstitution(boolean m_checkInstitution){}
	
	public boolean isExcludeBibliographic(){
		return false;
	}

	public void setExcludeBibliographic(boolean m_excludeBibliographic){}
	
	public boolean isExcludeQuoted(){
		return false;
	}

	public void setExcludeQuoted(boolean m_excludeQuoted){}
	
	public boolean isAllowAnyFile(){
		return false;
	}

	public void setAllowAnyFile(boolean m_allowAnyFile){}

	public int getExcludeType(){
		return 0;
	}
	
	public void setExcludeType(int m_excludeType){}

	public int getExcludeValue(){
		return 0;
	}
	
	public void setExcludeValue(int m_excludeValue){}
	
		public String getReference()
	{
		return null;
	}

	public String getReference(String rootProperty)
	{
		return null;
	}
	
	public String getUrl(String rootProperty)
	{
		return null;
	}
	
	public String getUrl()
	{
		return null;
	}
	
	public List getAttachments()
	{
		return null;
	}
	
	public boolean isActiveEdit(){
		return false;
	}
}
