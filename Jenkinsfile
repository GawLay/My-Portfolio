pipeline {
    agent any
    parameters {
        choice(name: 'BUILD_TYPE', choices: ['apk', 'bundle'], description: 'Choose build type')
    }
    stages {
         stage('Checkout') {
                    steps {
                    // Checkout the latest code from the branch that triggered the build
                    checkout scm
                    }
                 }

        stage('Prepare Environment') {
            when {
                            expression { env.BRANCH_NAME.contains('release') }
            }
            steps {
                    // Ensure Gradle wrapper is executable
                    sh 'chmod +x ./gradlew'
             }
        }

        stage('Build and Sign APK') {
            when {
                  expression { env.BRANCH_NAME.contains('release') }
             }
            steps {
                withCredentials([
                    file(credentialsId: 'android_keystore', variable: 'KEYSTORE_FILE'),
                    string(credentialsId: 'profolio_key_password', variable: 'KEYSTORE_PASSWORD'),
                    string(credentialsId: 'profolio_key_alias', variable: 'KEY_ALIAS')
                ]) {
                    script {
                        // Set the environment variables for Gradle
                        def keystorePath = env.KEYSTORE_FILE

                        // Echo environment variables to check if they're correctly set
                        echo "Keystore Path: ${keystorePath}"
                        echo "Keystore Password: ${env.KEYSTORE_PASSWORD}"
                        echo "Key Alias: ${env.KEY_ALIAS}"

                        // Build and sign APK using Gradle
                        // Interpolate environment variables in the shell script
                     if (params.BUILD_TYPE == 'apk') {
                        sh """
                        ./gradlew clean assembleRelease \
                        -Pandroid.injected.signing.store.file='${keystorePath}' \
                        -Pandroid.injected.signing.store.password='${env.KEYSTORE_PASSWORD}' \
                        -Pandroid.injected.signing.key.alias='${env.KEY_ALIAS}' \
                        -Pandroid.injected.signing.key.password='${env.KEYSTORE_PASSWORD}'
                         """
                     }else{
                        // Upload App Bundle
                       // Build and sign App Bundle using Gradle
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
        stage('Upload to Google Play') {
            when {
                    expression { env.BRANCH_NAME.contains('release') }
            }
            steps {
                withCredentials([file(credentialsId: "my-portfolio-f5976-3c69f2d2c4f9.json", variable: 'SERVICE_ACCOUNT_JSON')]) {
                    if (params.BUILD_TYPE == 'apk') {
                        googlePlay {
                            serviceAccountCredentialsId = 'jenkin-playservice-secret-json' // Ensure this matches your credential ID
                            trackName = 'production'
                            apkFilesPattern = 'app/build/outputs/apk/release/app-release-aligned.apk'
                        }
                    }else{
                        googlePlay {
                            serviceAccountCredentialsId = 'jenkin-playservice-secret-json' // Ensure this matches your credential ID
                            trackName = 'production'
                            bundleFilesPattern = 'app/build/outputs/bundle/release/app-release.aab'
                        }
                    }
                }
            }
        }

    }
    }
    post {
        always {
            cleanWs()
        }
    }
}