node('unix') {
    stage('Git checkout') {
        checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/7x7x49/CI_CD.git']])
    }
    
    stage('Run tests') {
        withMaven(globalMavenSettingsConfig: '', jdk: '', maven: 'Default', mavenSettingsConfig: '', traceability: true) {
            sh 'mvn clean test -Dtype.browser=${browser} -Dtype.driver=${driver}'
        }
    }
    
    stage('Allure') {
        allure(includeProperties: false, jdk: '', results: [[path: 'target/allure-results']])
    }
}
