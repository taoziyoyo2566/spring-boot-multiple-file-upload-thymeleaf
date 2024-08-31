pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        JAVA_HOME = '/opt/java/openjdk/'
//         NEXUS_USERNAME = credentials('DEPLOY_USER')
//         NEXUS_PASSWORD = credentials('DEPLOY_USER')
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git branch to build from')
        string(name: 'PROFILE', defaultValue: 'dev', description: 'Build profile (e.g., dev, prod)')
        string(name: 'VERSION', defaultValue: '1.0.0', description: 'Build version')
        choice(name: 'DEPLOY_TYPE', choices: ['release', 'snapshot'], description: 'Select deployment type: release or snapshot')
        string(name: 'JAVA_VERSION', defaultValue: '11', description: 'Java version to use')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: params.BRANCH_NAME, url: 'https://github.com/taoziyoyo2566/spring-boot-multiple-file-upload-thymeleaf.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "${MAVEN_HOME}/bin/mvn clean package -P${params.PROFILE} -Dversion=${params.VERSION} -Djava.version=${params.JAVA_VERSION} -s settings.xml"
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'DEPLOY_USER', passwordVariable: 'DEPLOY_PASS')]) {
                    script {
                        if (params.DEPLOY_TYPE == 'release') {
                            sh "${MAVEN_HOME}/bin/mvn deploy -DaltDeploymentRepository=nexus-releases::default::http://154.23.243.155:8081/repository/maven-releases/ -P${params.PROFILE} -Dversion=${params.VERSION} -Djava.version=${params.JAVA_VERSION} -s settings.xml -Dusername=${DEPLOY_USER} -Dpassword=${DEPLOY_PASS}"
                        } else {
                            sh "${MAVEN_HOME}/bin/mvn deploy -DaltDeploymentRepository=nexus-snapshots::default::http://154.23.243.155:8081/repository/maven-snapshots/ -P${params.PROFILE} -Dversion=${params.VERSION} -Djava.version=${params.JAVA_VERSION} -s settings.xml -Dusername=${DEPLOY_USER} -Dpassword=${DEPLOY_PASS}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Build and Deploy succeeded!'
        }
        failure {
            echo 'Build or Deploy failed.'
        }
    }
}