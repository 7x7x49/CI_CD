pipeline {
    agent any

    parameters {
        choice(
            name: 'RUN_MODE',
            choices: ['local', 'remote'],
            description: 'Режим запуска тестов'
        )
        choice(
            name: 'BROWSER',
            choices: ['edge', 'chrome', 'firefox'],
            description: 'Браузер для тестов'
        )
        choice(
            name: 'TEST_TAG',
            choices: ['@Test=1', '@Test=2', '@Test=3', '@all'],
            description: 'Выбор теста для запуска'
        )
    }

    tools {
        maven 'M3'
        jdk 'jdk11'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "Начало выполнения тестов"
                    echo "Параметры:"
                    echo "RUN_MODE: ${params.RUN_MODE}"
                    echo "BROWSER: ${params.BROWSER}"
                    echo "TEST_TAG: ${params.TEST_TAG}"

                    sh """
                    mvn clean test \
                    -Drun.mode=${params.RUN_MODE} \
                    -Dbrowser=${params.BROWSER} \
                    -Dcucumber.filter.tags=${params.TEST_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            allure includeProperties: false,
                   jdk: '',
                   results: [[path: 'target/allure-results']]

            // Архивация отчетов
            archiveArtifacts artifacts: 'target/**/*', allowEmptyArchive: true

            // Публикация JUnit отчетов
            junit 'target/surefire-reports/**/*.xml'
        }
        success {
            echo "Тесты успешно завершены!"
        }
        failure {
            echo "Тесты завершены с ошибками!"
        }
    }
}