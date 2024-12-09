#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2025-07'

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream("knime-expressions/${env.BRANCH_NAME.replaceAll('/', '%2F')}" +
            ", knime-js-pagebuilder/${env.BRANCH_NAME.replaceAll('/', '%2F')}" +
            ", knime-svg/${env.BRANCH_NAME.replaceAll('/', '%2F')}")
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
            workflowTests.runIntegratedWorkflowTests(profile: 'test')
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

/* vim: set shiftwidth=4 expandtab smarttab: */
