TII LTI integration
-------------------

The new TII LTI integration includes some new site properties needed for configuration. If they aren't added to the site the default value will be used:

	* useContentReviewService : this property indicates whether we are using the TII integration or not
		Default value: false
	
	* useContentReviewLTIService : this property indicates whether we are using the new LTI TII integration or the old API
		Default value: false
		
	* useContentReviewDirectSubmission : this property indicates whether we are using the direct submission way or the Sakai submission method
		Default value: false
		
NOTE: This properties will be checked when using the ContentReviewSiteAdvisor as the SiteAdvisor implementation.


TODO COMPLETE