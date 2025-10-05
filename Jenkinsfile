- node('unix') {
    stage('Git checkout') {
        checkout scm
    }
        stage('Run tests') {
        withMaven (globalMavenSettings Config: ", jdk: ", maven: 'Default', mavenSettingsConfig:, traceability: true) {
            sh 'mvn clean test -Dtype.browser-S(browser)'
        }
    }
    stage('Allure') {
        allure includeProperties: false, jdk:, results: [[path: 'target/reports/allure-results']]

    }
}