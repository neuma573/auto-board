pipeline {
    agent any

    environment {
        DB_HOST = credentials('db-host-credential-id')
        DB_USER = credentials('db-user-credential-id')
        DB_PASSWORD = credentials('db-password-credential-id')
        REDIS_HOST = credentials('redis-host-credential-id')
        REDIS_PORT = credentials('redis-port-credential-id')
        REDIS_PASSWORD = credentials('redis-password-credential-id')
        REDIS_USERNAME = credentials('redis-username-credential-id')
        MAIL_HOST = credentials('mail-host-credential-id')
        MAIL_PORT = credentials('mail-port-credential-id')
        GOOGLE_MAIL_ADDRESS = credentials('google-mail-address-credential-id')
        GOOGLE_APP_PASSWORD = credentials('google-app-password-credential-id')
        JWT_SECRET = credentials('jwt-secret-credential-id')
        JWT_ISSUER = credentials('jwt-issuer-credential-id')
        MAIL_PATH = credentials('mail-path-credential-id')
        DOMAIN = credentials('domain-credential-id')

        KEY_STORE = credentials('key-store-credential-id')
        KEY_STORE_PASSWORD = credentials('key-store-password-credential-id')
        KEY_TYPE = credentials('key-type-credential-id')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare SSL') {
            steps {
                withCredentials([file(credentialsId: 'ssl_file', variable: 'SSL_FILE')]) {

                    sh 'cp $SSL_FILE src/main/resources/autoboard.site.pfx'
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Run JAR') {
            steps {
                script {
                    def jarPath = 'build/libs/auto-board-0.0.1-SNAPSHOT.jar'
                    sh "java -jar ${jarPath}"
                }
            }
        }
    }

    post {
        success {
            echo 'Build and run succeeded.'
        }
        failure {
            echo 'Build and run failed.'
        }
    }
}
