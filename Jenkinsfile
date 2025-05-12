def ANDROID_KEYSTORE_ID = 'android_keystore'
def KEYSTORE_PASSWORD_ID = 'profolio_key_password'
def KEYSTORE_ALIAS_ID = 'profolio_key_alias'
def GOOGLE_SERVICE_JSON_ID = "google-services-json-portfolio"

pipeline {
    agent  any

    triggers {
        githubPush()
    }

    environment {
        GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
        _JAVA_OPTIONS = "-Duser.home=${WORKSPACE}"
        PATH = "${JAVA_HOME}/bin:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${env.PATH}"
        GRADLE_OPTS = "-Dorg.gradle.cache.size=500000000"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    echo "Building branch: ${env.BRANCH_NAME}"
                    sh '''
                        whoami
                        ls -la ${WORKSPACE}
                        java -version
                        echo $JAVA_HOME
                        echo $PATH
                        sdkmanager --version
                        docker --version || echo "Docker CLI not available"
                        ls -la /var/run/docker.sock || echo "Docker socket not accessible"
                        groups
                    '''
                }
            }
        }

        stage('Prepare Environment') {
            when {
                expression { return env.BRANCH_NAME ==~ /^release\/.*/ }
            }
            steps {
                withCredentials([file(credentialsId: GOOGLE_SERVICE_JSON_ID, variable: 'GOOGLE_SERVICES_JSON')]) {
                    sh '''
                        mkdir -p app/src/release/
                        cp ${GOOGLE_SERVICES_JSON} app/google-services.json
                    '''
                }
            }
        }

        stage('Clean Build Artifacts') {
            steps {
                sh '''
                    ./gradlew clean
                    rm -rf ${GRADLE_USER_HOME}
                '''
            }
        }

        stage('Build APK') {
            when {
                expression { return env.BRANCH_NAME ==~ /^release\/.*/ }
            }
            steps {
                withCredentials([
                    file(credentialsId: ANDROID_KEYSTORE_ID, variable: 'KEYSTORE_FILE'),
                    string(credentialsId: KEYSTORE_PASSWORD_ID, variable: 'KEYSTORE_PASSWORD'),
                    string(credentialsId: KEYSTORE_ALIAS_ID, variable: 'KEY_ALIAS')
                ]) {
                    sh '''
                        ./gradlew assembleRelease \
                            --no-daemon \
                            --no-parallel \
                            -Pandroid.injected.signing.store.file="$KEYSTORE_FILE" \
                            -Pandroid.injected.signing.store.password="$KEYSTORE_PASSWORD" \
                            -Pandroid.injected.signing.key.alias="$KEY_ALIAS" \
                            -Pandroid.injected.signing.key.password="$KEYSTORE_PASSWORD"
                    '''
                    archiveArtifacts artifacts: 'app/build/outputs/apk/release/*.apk', allowEmptyArchive: false
                }
            }
        }

        stage('Build AAB') {
            when {
                expression { return env.BRANCH_NAME ==~ /^release\/.*/ }
            }
            steps {
                withCredentials([
                    file(credentialsId: ANDROID_KEYSTORE_ID, variable: 'KEYSTORE_FILE'),
                    string(credentialsId: KEYSTORE_PASSWORD_ID, variable: 'KEYSTORE_PASSWORD'),
                    string(credentialsId: KEYSTORE_ALIAS_ID, variable: 'KEY_ALIAS')
                ]) {
                    sh '''
                        ./gradlew bundleRelease \
                            --no-daemon \
                            --no-parallel \
                            -Pandroid.injected.signing.store.file="$KEYSTORE_FILE" \
                            -Pandroid.injected.signing.store.password="$KEYSTORE_PASSWORD" \
                            -Pandroid.injected.signing.key.alias="$KEY_ALIAS" \
                            -Pandroid.injected.signing.key.password="$KEYSTORE_PASSWORD"
                    '''
                    archiveArtifacts artifacts: 'app/build/outputs/bundle/release/*.aab', allowEmptyArchive: false
                }
            }
        }
    }

    post {
        always {
            cleanWs()
            script {
                currentBuild.description = "Built APK and AAB from ${env.BRANCH_NAME}"
            }
        }
        failure {
            emailext body: "Build failed: ${env.BUILD_URL}",
                     subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     to: 'phyoaz14@gmail.com'
        }
    }
}