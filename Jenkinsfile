pipeline {
    agent any

    environment {
        HARBOR_REGISTRY = 'harbor.hadiubaidillah.com'
        HARBOR_PROJECT  = 'todo'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def isFirstBuild = (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT == null)

                    def changedFiles = isFirstBuild ? 'ALL' :
                        sh(script: "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${env.GIT_COMMIT}",
                           returnStdout: true).trim()

                    echo "Changed files:\n${changedFiles}"

                    def all = isFirstBuild || changedFiles == 'ALL'
                    def sharedBackend = all || changedFiles.split('\n').any {
                        it.startsWith('backend/build.gradle') ||
                        it.startsWith('backend/settings.gradle') ||
                        it.startsWith('backend/gradle/')
                    }

                    env.BUILD_FRONTEND     = (all || changedFiles.split('\n').any { it.startsWith('frontend/') }) ? 'true' : 'false'
                    env.BUILD_GATEWAY      = (sharedBackend || changedFiles.split('\n').any { it.startsWith('backend/platform/gateway-server/') }) ? 'true' : 'false'
                    env.BUILD_TODO         = (sharedBackend || changedFiles.split('\n').any { it.startsWith('backend/services/todo-service/') }) ? 'true' : 'false'
                    env.BUILD_NOTIFICATION = (sharedBackend || changedFiles.split('\n').any { it.startsWith('backend/platform/notification-service/') }) ? 'true' : 'false'
                    env.BUILD_DISCOVERY    = (sharedBackend || changedFiles.split('\n').any { it.startsWith('backend/platform/discovery-server/') }) ? 'true' : 'false'

                    echo """Services to build:
  frontend:             ${env.BUILD_FRONTEND}
  gateway-server:       ${env.BUILD_GATEWAY}
  todo-service:         ${env.BUILD_TODO}
  notification-service: ${env.BUILD_NOTIFICATION}
  discovery-server:     ${env.BUILD_DISCOVERY}"""
                }
            }
        }

        stage('Build & Push') {
            when {
                expression {
                    env.BUILD_FRONTEND == 'true' ||
                    env.BUILD_GATEWAY == 'true' ||
                    env.BUILD_TODO == 'true' ||
                    env.BUILD_NOTIFICATION == 'true' ||
                    env.BUILD_DISCOVERY == 'true'
                }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'harbor-credentials',
                    usernameVariable: 'HARBOR_USER',
                    passwordVariable: 'HARBOR_PASS'
                )]) {
                    sh "docker login ${HARBOR_REGISTRY} -u \${HARBOR_USER} -p \${HARBOR_PASS}"
                }

                script {
                    def buildStages = [:]

                    if (env.BUILD_FRONTEND == 'true') {
                        buildStages['frontend'] = {
                            sh """
                                docker build -t ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/frontend:latest \
                                    -f frontend/Dockerfile frontend/
                                docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/frontend:latest
                            """
                        }
                    }
                    if (env.BUILD_GATEWAY == 'true') {
                        buildStages['gateway-server'] = {
                            sh """
                                docker build -t ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/gateway-server:latest \
                                    -f backend/platform/gateway-server/Dockerfile backend/
                                docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/gateway-server:latest
                            """
                        }
                    }
                    if (env.BUILD_TODO == 'true') {
                        buildStages['todo-service'] = {
                            sh """
                                docker build -t ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/todo-service:latest \
                                    -f backend/services/todo-service/Dockerfile backend/
                                docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/todo-service:latest
                            """
                        }
                    }
                    if (env.BUILD_NOTIFICATION == 'true') {
                        buildStages['notification-service'] = {
                            sh """
                                docker build -t ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/notification-service:latest \
                                    -f backend/platform/notification-service/Dockerfile backend/
                                docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/notification-service:latest
                            """
                        }
                    }
                    if (env.BUILD_DISCOVERY == 'true') {
                        buildStages['discovery-server'] = {
                            sh """
                                docker build -t ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/discovery-server:latest \
                                    -f backend/platform/discovery-server/Dockerfile backend/
                                docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/discovery-server:latest
                            """
                        }
                    }

                    parallel buildStages
                }
            }
        }

        stage('Deploy') {
            when {
                expression {
                    env.BUILD_FRONTEND == 'true' ||
                    env.BUILD_GATEWAY == 'true' ||
                    env.BUILD_TODO == 'true' ||
                    env.BUILD_NOTIFICATION == 'true' ||
                    env.BUILD_DISCOVERY == 'true'
                }
            }
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        if (env.BUILD_FRONTEND == 'true') {
                            sh 'kubectl rollout restart deployment/frontend -n todo'
                        }
                        if (env.BUILD_GATEWAY == 'true') {
                            sh 'kubectl rollout restart deployment/gateway-server -n todo'
                        }
                        if (env.BUILD_TODO == 'true') {
                            sh 'kubectl rollout restart deployment/todo-service -n todo'
                        }
                        if (env.BUILD_NOTIFICATION == 'true') {
                            sh 'kubectl rollout restart deployment/notification-service -n todo'
                        }
                        if (env.BUILD_DISCOVERY == 'true') {
                            sh 'kubectl rollout restart deployment/discovery-server -n todo'
                        }
                    }

                    sh '''
                        for dep in $(kubectl get deployments -n todo \
                            -o jsonpath="{.items[*].metadata.name}"); do
                            kubectl rollout status deployment/$dep -n todo --timeout=120s
                        done
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
