TII LTI integration
-------------------

The new TII LTI integration includes some new site properties needed for configuration. If they aren't added to the site the default value will be used:

	* useContentReviewService : this site property indicates whether we are using the TII integration or not
		Default value: false
	
	* useContentReviewLTIService : this site property indicates whether we are using the new LTI TII integration or the old API
		Default value: false
		Note: it will only be checked if the previous property is true
		
	* useContentReviewDirectSubmission : this site property indicates whether we are using the direct/external submission way or the Sakai submission method
		Default value: false
		Note: it will only be checked if the previous properties are true
		
NOTE: This properties will be checked when using the SitePropertyAdvisor as the ContentReviewSiteAdvisor implementation.

For instance, if we want to set up a new site for using the new LTI integration we'd need these properties:

	useContentReviewService = true
	useContentReviewLTIService = true

TODO COMPLETE