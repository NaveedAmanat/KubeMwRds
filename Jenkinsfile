pipeline{
    agent any
    
    environment {
        IMAGE_TAG = "v${BUILD_NUMBER}"
	JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64/jre'
    }
    
    tools{
         maven 'MAVEN_HOME'
    }
    
    stages{
        stage('Build Maven'){
            steps{
                sh 'mvn clean install'
            }
        }
        stage('Build docker image'){
            steps{
                script{
                    sh 'sudo docker build -t navedamanat/rds-service:${IMAGE_TAG} .'
                }
            }
        }
        stage('Push image to Docker Hub'){
            steps{
                script{
                    withCredentials([string(credentialsId: 'DOCKER_KEY_MWX', variable: 'DOCKER_KEY_MWX')]) {
                        sh 'echo login to docker hub'
                    }
                }
            }
        }
		stage('Trigger Artifact'){
            steps{
                sh 'echo env.IMAGE_TAG'
                build job: 'KubeMwRdsArtifact', parameters: [string(name: 'IMAGE_TAG', value: env.IMAGE_TAG)]
            }
        }
    }
}
