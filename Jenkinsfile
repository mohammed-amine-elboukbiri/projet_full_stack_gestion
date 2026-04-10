pipeline {
    agent any

    environment {
        REGISTRY = 'ghcr.io'
        IMAGE_NAMESPACE = 'mohammed-amine-elboukbiri'
        BACKEND_IMAGE = "${REGISTRY}/${IMAGE_NAMESPACE}/project-management-backend"
        FRONTEND_IMAGE = "${REGISTRY}/${IMAGE_NAMESPACE}/project-management-frontend"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_CREDENTIALS_ID = 'ghcr-creds'
        CD_JOB_NAME = 'project-management-cd'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Test') {
            steps {
                sh './mvnw test'
            }
        }

        stage('Frontend Build') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                sh '''
                    if command -v dependency-check.sh >/dev/null 2>&1; then
                      dependency-check.sh --scan . --format XML --out dependency-check-report || true
                    else
                      echo "OWASP Dependency Check is not installed on this Jenkins agent."
                    fi
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        ./mvnw sonar:sonar \
                          -Dsonar.projectKey=project-management-backend \
                          -Dsonar.projectName=project-management-backend \
                          -Dsonar.host.url=http://sonarqube:9000 \
                          -Dsonar.token=${SONAR_TOKEN} || true
                    '''
                }
            }
        }

        stage('Trivy Filesystem Scan') {
            steps {
                sh '''
                    if command -v trivy >/dev/null 2>&1; then
                      trivy fs --scanners vuln,secret,config --exit-code 0 .
                    else
                      echo "Trivy is not installed on this Jenkins agent."
                    fi
                '''
            }
        }

        stage('Package Backend') {
            steps {
                sh './mvnw -DskipTests package'
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    docker.build("${BACKEND_IMAGE}:${IMAGE_TAG}")
                    docker.build("${FRONTEND_IMAGE}:${IMAGE_TAG}", './frontend')
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry("https://${REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        docker.image("${BACKEND_IMAGE}:${IMAGE_TAG}").push()
                        docker.image("${FRONTEND_IMAGE}:${IMAGE_TAG}").push()
                        docker.image("${BACKEND_IMAGE}:${IMAGE_TAG}").push('latest')
                        docker.image("${FRONTEND_IMAGE}:${IMAGE_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Trivy Image Scan') {
            steps {
                sh '''
                    if command -v trivy >/dev/null 2>&1; then
                      trivy image --exit-code 0 ${BACKEND_IMAGE}:${IMAGE_TAG}
                      trivy image --exit-code 0 ${FRONTEND_IMAGE}:${IMAGE_TAG}
                    else
                      echo "Trivy is not installed on this Jenkins agent."
                    fi
                '''
            }
        }

        stage('Trigger Jenkins CD Job') {
            steps {
                build job: CD_JOB_NAME, wait: false, parameters: [
                    string(name: 'IMAGE_TAG', value: IMAGE_TAG),
                    string(name: 'IMAGE_NAMESPACE', value: IMAGE_NAMESPACE)
                ]
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'dependency-check-report/**/*', onlyIfSuccessful: false
        }
        success {
            emailext(
                subject: "CI Success - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "The CI pipeline succeeded and triggered the CD job.\nImage tag: ${IMAGE_TAG}",
                to: 'team@example.com'
            )
        }
        failure {
            emailext(
                subject: "CI Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "The CI pipeline failed. Please check Jenkins logs.",
                to: 'team@example.com'
            )
        }
    }
}
