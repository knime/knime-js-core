#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
	pipelineTriggers([
		upstream('knime-expressions/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
		upstream('knime-svg/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
	]),
    parameters(workflowTests.getConfigurationsAsParameters()),
	buildDiscarder(logRotator(numToKeepStr: '5')),
	disableConcurrentBuilds()
])

try {
    buildConfigurations = [
        Tycho: {
	        knimetools.defaultTychoBuild('org.knime.update.js.core')
        },
        Testing: {
	        runIntegratedWorkflowTests('workflow-tests && ubuntu18.04')
        },
    ]

    parallel buildConfigurations

	stage('Sonarqube analysis') {
		env.lastStage = env.STAGE_NAME
		workflowTests.runSonar([])
	}
} catch (ex) {
	currentBuild.result = 'FAILURE'
	throw ex
} finally {
	notifications.notifyBuild(currentBuild.result);
}

def runIntegratedWorkflowTests(String image){
    node(image) { 
        stage('Platform specific testing'){
            env.lastStage = env.STAGE_NAME
            checkout scm

            knimetools.runIntegratedWorkflowTests(profile: 'test')
        }
    }
}
/* vim: set shiftwidth=4 expandtab smarttab: */
