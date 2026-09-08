pipeline {
    agent any
    tools {
        jdk 'JDK17'
        maven 'Maven3'
        nodejs 'Node20'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test - Backend') {
            parallel {
                stage('Auth Service') {
                    steps {
                        dir('backend/auth-service') {
                            sh 'mvn clean verify'
                        }
                    }
                }
                stage('Employee Service') {
                    steps {
                        dir('backend/employee-service') {
                            sh 'mvn clean verify'
                        }
                    }
                }
                stage('Expense Service') {
                    steps {
                        dir('backend/expense-service') {
                            sh 'mvn clean verify'
                        }
                    }
                }
                stage('Eureka Server') {
                    steps {
                        dir('backend/eureka-server') {
                            sh 'mvn clean verify'
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis - Backend') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    dir('backend/auth-service') { sh 'mvn sonar:sonar' }
                    dir('backend/employee-service') { sh 'mvn sonar:sonar' }
                    dir('backend/expense-service') { sh 'mvn sonar:sonar' }
                    dir('backend/eureka-server') { sh 'mvn sonar:sonar' }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
    }
}