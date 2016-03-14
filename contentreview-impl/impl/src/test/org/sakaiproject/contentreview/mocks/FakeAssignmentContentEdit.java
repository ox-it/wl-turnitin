package org.sakaiproject.contentreview.mocks;

import java.util.List;
import java.util.Stack;

import org.sakaiproject.assignment.api.AssignmentContentEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.util.BaseResourcePropertiesEdit;
import org.sakaiproject.entity.api.AttachmentContainer;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.user.api.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FakeAssignmentContentEdit extends FakeAssignmentContent implements AssignmentContentEdit {

	private String id;
	private String title;

	public FakeAssignmentContentEdit(String id) {
		super(id);
	}
	
	public ResourcePropertiesEdit getPropertiesEdit(){
		return new BaseResourcePropertiesEdit();
	}

	public void setTitle(String title){}

	public void setInstructions(String instructions){}

	public void setContext(String context){}

	public void setTypeOfSubmission(int subType){}

	public void setTypeOfGrade(int gradeType){}

	public void setMaxGradePoint(int maxPoints){}

	public void setGroupProject(boolean groupProject){}

	public void setIndividuallyGraded(boolean individGraded){}

	public void setReleaseGrades(boolean release){}

	public void setHonorPledge(int pledgeType){}

	public void setAllowAttachments(boolean allow){}

	public void setHideDueDate(boolean hide){}

	public void setAllowReviewService(boolean allow){}
	
	public void setAllowStudentViewReport(boolean allow){}
	
	public void setAllowStudentViewExternalGrade(boolean allow){}
	
	public void addAuthor(User author){}

	public void removeAuthor(User author){}

	public void setTimeLastModified(Time lastmod){}
	
	public void clearAttachments(){}
	
	public void replaceAttachments(List attachments){}
	
	public void addAttachment(Reference ref){}
	
	public void removeAttachment(Reference ref){}
	
	protected void closeEdit(){}

}
