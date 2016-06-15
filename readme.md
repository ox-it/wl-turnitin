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

	

Design of LTI TII Integration
-----------------------------

The integration can work in 2 modes, the first mode tries to hide the TII interface from students with them still 
submitting to Sakai directly. In this mode when an instructor sets up an assignment upon creating a submission in Sakai
a LTI launch is also made to Turnitin by the server which attempts to create the assignment there as well. If this 
completes successfully there should then be a callback to /sakai-contentreview-tool/tii-servlet from Turnitin. This callback
is used to get the Turnitin ID and the servlet handling this request stores the ID on the assignment. If this callback
fails for any reason then all assignment submissions will fail to be sent to Turnitin.

Once the callback has been relieved student submissions can be made, these are first made to Sakai and then when the 
quartz job is run sent on the Turnitin using the XML LTI launch for submitting files. These launches don't actually
upload the files directly to Turnitin but instead launch, giving a URL where Turnitin can download the file from. 

After turnitin has retrieved the file it should make a request to the submission callback URL
/sakai-contentreview-tool/submission-servlet which takes the request and finds the submission it relates to and sets
the a turnitin_id on the assignment to the internal ID of the submission in Turnitin. This is needed to build a URL
directly to the submission to allow the score to be retrieved.

After we have the Turnitin ID of the paper we can make requests to a turnitin URL to retrieve the score. This again is made
as an LTI launch and the response it JSON that we attempt to parse and extract out the score.

In the second mode students submit directly into Turnitin so when they go to submit to the assignment they are taken
directly to the Turnitin Inbox which allows them to submit directly. This is done by a standard LTI launch to Turnitin.

Developing against the integration
----------------------------------

When submissions are made against Turnitin callback URLs are supplied, by default the URL of the request is used to 
extract the hostname that callbacks should be made against. As a developer this is typically localhost or some VM which
means that the callback URLs would not be resolvable by Turnitin. To get around this you can use a tool such as
https://ngrok.com/ which allows you to expose ports. However if you do this, you then need to access Sakai through this
published URL when testing and also set the `serverUrl` in the `sakai.properties` to the custom ngrok endpoint.


SETUP
-----

Quartz Jobs
-----------

Remembers to set up Quartz job

        * Content Review Queue
        * Content Review Reports

you will have to run jobs manually unless they're set up to auto-run.

LTI Tool Config
---------------

Add an LTI tool via Admin Workspace > External Tools, the configuration for the tool in production is given below. 
Please note, **do not create a site with ID '!turnitin'** as this will prevent the integration from working correctly; the '!turnitin' site ID is simply used as a form of "reserved namespace".

        * Site ID: !turnitin
        * Tool Title: Turnitin
        * Allow tool title to be changed
        * Set Button Text - Turnitin
        * Do not allow button text to be changed
        * Description - optional
        * Tool status: Enabled
        * Tool visibility: stealthed
        * Launch URL - https://api.turnitinuk.com/api/lti/1p0/assignment or equivalent for the US, Spain etc.
        * Do not allow URL to be changed
        * Tool Key - ????
        * Do not allow Launch Key to be changed
        * Secret - ??Get via Turnitin web site??
        * Do not allow Launch Secret to be changed
        * Do not allow frame height to be changed
        * Open in a New Windows - checked.
        * Send Names to the External Tool - checked.
        * Send Email Addresses to the External Tool. - checked.
        * Allow External Tool to return grades - checked.
        * Never Launch in pop-up
        * (In production) Never launch in debug mode
        * Allow additional custom parameters

and on a dev / test environment (which points at the sandbox server)

        * Site ID: !turnitin
        * Launch URL - https://sandbox.turnitin.com/api/lti/1p0/assignment
        * Tool Key - ????
        * Secret - ??Get via Turnitin web site??

Other settings are as for the production service.

More details at: http://turnitin.com/en_us/support/integrations/lti/