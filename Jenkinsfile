#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
	pipelineTriggers([
		upstream('knime-expressions/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
		upstream('knime-svg/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
	]),
	buildDiscarder(logRotator(numToKeepStr: '5')),
	disableConcurrentBuilds()
])

try {
	knimetools.defaultTychoBuild('org.knime.update.js.core')

//	workflowTests.runTests(
//		"org.knime.features.js.core.testing.feature.group",
//		false,
//		["knime-core", "knime-svg", "knime-shared", "knime-expressions", "knime-base", "knime-tp"],
//	)

//	stage('Sonarqube analysis') {
//		env.lastStage = env.STAGE_NAME
//		// passing the test configuration is optional but must be done when they are
//		// used above in the workflow tests
//		workflowTests.runSonar(testConfigurations)
//	}
 } catch (ex) {
	 currentBuild.result = 'FAILED'
	 throw ex
 } finally {
	 notifications.notifyBuild(currentBuild.result);
 }

/* vim: set ts=4: */
