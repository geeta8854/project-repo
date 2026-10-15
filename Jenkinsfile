def runCommand(String unixCommand, String windowsCommand) {
    if (isUnix()) {
        sh unixCommand
    } else {
        bat windowsCommand
    }
}

pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        IMAGE_NAME = 'library-management-cicd'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Unit Test') {
            steps {
                script {
                    runCommand('mvn -B clean test', 'mvn -B clean test')
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package Application') {
            steps {
                script {
                    runCommand('mvn -B -DskipTests package', 'mvn -B -DskipTests package')
                }
                archiveArtifacts artifacts: 'target/library-management-cicd.jar', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    runCommand(
                        'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest .',
                        'docker build -t %IMAGE_NAME%:%BUILD_NUMBER% -t %IMAGE_NAME%:latest .'
                    )
                }
            }
        }

        stage('Start App and Selenium') {
            steps {
                script {
                    runCommand(
                        'docker compose up -d app selenium',
                        'docker compose up -d app selenium'
                    )
                    runCommand(
                        '''
                        for i in $(seq 1 30); do
                          if curl -fsS http://localhost:8080/ >/dev/null; then
                            echo "Application is responding"
                            exit 0
                          fi
                          sleep 2
                        done
                        echo "Application did not become ready"
                        docker compose logs app
                        exit 1
                        ''',
                        '''
                        powershell -NoProfile -ExecutionPolicy Bypass -Command "$ok=$false; for ($i=0; $i -lt 30; $i++) { try { Invoke-WebRequest -UseBasicParsing http://localhost:8080/ -TimeoutSec 2 | Out-Null; $ok=$true; break } catch { Start-Sleep -Seconds 2 } }; if (-not $ok) { docker compose logs app; exit 1 }"
                        '''
                    )
                }
            }
        }

        stage('Selenium Integration Test') {
            steps {
                script {
                    runCommand(
                        'docker compose run --rm test-runner',
                        'docker compose run --rm test-runner'
                    )
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
                }
            }
        }
    }

    post {
        always {
            script {
                runCommand(
                    'docker compose down --remove-orphans || true',
                    'docker compose down --remove-orphans'
                )
            }
            archiveArtifacts artifacts: 'target/surefire-reports/**,target/failsafe-reports/**', allowEmptyArchive: true
        }
        success {
            echo 'Library Management CI/CD pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Review the stage console output and test reports.'
        }
    }
}
