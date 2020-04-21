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

	runIntegratedWorkflowTests('workflow-tests && ubuntu18.04')

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
            withMavenJarsignerCredentials(options: [artifactsPublisher(disabled: true)]) {
                withCredentials([usernamePassword(credentialsId: 'ARTIFACTORY_CREDENTIALS', passwordVariable: 'ARTIFACTORY_PASSWORD', usernameVariable: 'ARTIFACTORY_LOGIN')]) {
                    sh '''
                        export TEMP="${WORKSPACE}/tmp"
                        rm -rf "${TEMP}"; mkdir "${TEMP}"
                        
                        XVFB=$(which Xvfb) || true
                        if [[ -x "$XVFB" ]]; then
                            Xvfb :$$ -pixdepths 24 -screen 0 1280x1024x24 +extension RANDR &
                            XVFB_PID=$!
                            export DISPLAY=:$$
                        fi

                        mvn -Dmaven.test.failure.ignore=true -Dknime.p2.repo=${P2_REPO} clean verify -P test
                        rm -rf "${TEMP}"
                        if [[ -n "$XVFB_PID" ]]; then
                            kill $XVFB_PID
                        fi
                    '''
                }
            }
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        }
    }
}



/* vim: set shiftwidth=4 expandtab smarttab: */
