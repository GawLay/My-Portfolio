pipeline {
    agent any
    triggers {
        githubPush()
    }
    environment {
            ANDROID_HOME = '/opt/android-sdk'
    }
    parameters {
        choice(name: 'BUILD_TYPE', choices: ['apk', 'bundle'], description: 'Choose build type')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Latest Release Branch') {
            when {
                expression { env.BRANCH_NAME?.contains('release') ?: false }
            }
            steps {
                script {
                    // Get the latest release branch by sorting tags or branches
                    def latestRelease = sh(script: "git ls-remote --heads origin | grep 'refs/heads/release/' | sort -V | tail -n 1 | awk '{print \$2}' | sed 's/refs\\/heads\\///'", returnStdout: true).trim()
                    if (env.BRANCH_NAME != latestRelease) {
                        error "This is not the latest release branch (${latestRelease}). Stopping build."
                    }
                }
            }
        }

        stage('Prepare Environment') {
            when {
                expression { env.BRANCH_NAME?.contains('release') ?: false }
            }
            steps {
                sh 'chmod +x ./gradlew'
            }
        }

        stage('Build and Sign APK') {
            when {
                expression { env.BRANCH_NAME?.contains('release') ?: false }
            }
            steps {
                withCredentials([
                    file(credentialsId: 'android_keystore', variable: 'KEYSTORE_FILE'),
                    string(credentialsId: 'profolio_key_password', variable: 'KEYSTORE_PASSWORD'),
                    string(credentialsId: 'profolio_key_alias', variable: 'KEY_ALIAS')
                ]) {
                    script {
                        def keystorePath = env.KEYSTORE_FILE
                        echo "Keystore Path: ${keystorePath}"
                        echo "Keystore Password: ${env.KEYSTORE_PASSWORD}"
                        echo "Key Alias: ${env.KEY_ALIAS}"
                        if (params.BUILD_TYPE == 'apk') {
                            sh """
                            ./gradlew clean assembleRelease \
                            -Pandroid.injected.signing.store.file='${keystorePath}' \
                            -Pandroid.injected.signing.store.password='${env.KEYSTORE_PASSWORD}' \
                            -Pandroid.injected.signing.key.alias='${env.KEY_ALIAS}' \
                            -Pandroid.injected.signing.key.password='${env.KEYSTORE_PASSWORD}'
                            """
                        } else {
                            sh """
                            ./gradlew clean bundleRelease \
                            -Pandroid.injected.signing.store.file='${keystorePath}' \
                            -Pandroid.injected.signing.store.password='${env.KEYSTORE_PASSWORD}' \
                            -Pandroid.injected.signing.key.alias='${env.KEY_ALIAS}' \
                            -Pandroid.injected.signing.key.password='${env.KEYSTORE_PASSWORD}'
                            """
                        }
                    }
                }
            }
        }

        // stage('Upload to Google Play') {
        //     when {
        //         expression { env.BRANCH_NAME?.contains('release') ?: false }
        //     }
        //     steps {
        //         withCredentials([file(credentialsId: "my-portfolio-f5976-3c69f2d2c4f9.json", variable: 'SERVICE_ACCOUNT_JSON')]) {
        //             if (params.BUILD_TYPE == 'apk') {
        //                 googlePlay {
        //                     serviceAccountCredentialsId = 'jenkin-playservice-secret-json'
        //                     trackName = 'production'
        //                     apkFilesPattern = 'app/build/outputs/apk/release/app-release-aligned.apk'
        //                 }
        //             } else {
        //                 googlePlay {
        //                     serviceAccountCredentialsId = 'jenkin-playservice-secret-json'
        //                     trackName = 'production'
        //                     bundleFilesPattern = 'app/build/outputs/bundle/release/app-release.aab'
        //                 }
        //             }
        //         }
        //     }
        // }
    }
    post {
        always {
            cleanWs()
        }
    }
}