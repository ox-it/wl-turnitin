TII LTI integration
-------------------

First step: select the ContentReviewSiteAdvisor you want on the components.xml file. Uncomment the specific lines and deploy the tool.

SITEPROPERTYADVISOR:
--------------------
The new TII LTI integration includes some new site properties needed for configuration. If they aren't added to the site, the default value will be used:

	* useContentReviewService : this site property indicates whether we are using the TII integration or not
		Default value: false
	
	* useContentReviewLTIService : this site property indicates whether we are using the new LTI TII integration or the old API
		Default value: false
		Note: it will only be checked if the previous property is true
		
	* useContentReviewDirectSubmission : this site property indicates whether we are using the direct/external submission way or the Sakai submission method
		Default value: false
		Note: it will only be checked if the previous properties are true
	
	For instance, if we want to set up a new site for using the new LTI integration we'd need these properties:

		useContentReviewService = true
		useContentReviewLTIService = true
	
GLOBALPROPERTYADVISOR:
----------------------
This advisor will be used when we want all our sites to behave the same way. If they aren't included to the sakai.properties file, false will be taken as the default value.

	* assignment.useContentReviewService : this property indicates whether we are using the TII integration or not
	
	* assignment.useContentReviewLTI : this property indicates whether we are using the new LTI TII integration or the old API
		Note: it will only be checked if the previous property is true
		
	* assignment.useContentReviewDirect : this property indicates whether we are using the direct/external submission way or the Sakai submission method
		Note: it will only be checked if the previous properties are true
	
	For instance, if we want all the sites to use the new LTI integration, we'd need these properties (besides all the other TII configuration):

		assignment.useContentReviewService = true
		assignment.useContentReviewLTI = true

	
TODO COMPLETE