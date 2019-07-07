#!groovy

pipeline {

    agent any

    stages {

        stage("Clean") {
            steps {
                sh './scripts/clean.sh'
            }
        }

        stage("Build") {
            steps {
                sh './scripts/build.sh'
            }
        }

        stage("Test") {
            steps {
                sh './scripts/test.sh'
            }
        }

        stage("Dockerize") {
            steps {
                sh './scripts/dockerize.sh'
            }
        }

        stage("Release") {
            steps {
                sh './scripts/release.sh'
            }
        }

        stage("Trigger Deploy") {
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'git', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
                    sh './scripts/trigger-deploy.sh ${GIT_USERNAME} ${GIT_PASSWORD}'
                }
            }
        }

    }

}
