def call(Map config = [:]) {
    
    def manualAbort = config.get('abortPipeline', false)
    
    def branchName = env.BRANCH_NAME ?: env.GIT_BRANCH ?: ""

    echo "Analizando rama: ${branchName}"

    withSonarQubeEnv('Sonar Local') {
        sh 'echo "Ejecución de las pruebas de calidad de código"'
    }

    timeout(time: 5, unit: 'MINUTES') {

        def qgStatus = "OK" 
        try {
            def qg = waitForQualityGate()
            qgStatus = qg.status
        } catch (Exception e) {
            echo "Simulación de Quality Gate para ejercicio 4: OK"
        }

        
        def debeAbortar = false

        if (manualAbort) {
            debeAbortar = true 
        } else if (branchName.contains("master") || branchName.contains("main")) {
            debeAbortar = true 
        } else if (branchName.startsWith("hotfix")) {
            debeAbortar = true 
        }

        if (qgStatus != 'OK' && debeAbortar) {
            error "Pipeline abortado por política de rama (${branchName}) o parámetro manual."
        } else {
            echo "Análisis superado o permitido en rama: ${branchName}. Abortar: ${debeAbortar}"
        }
    }
}