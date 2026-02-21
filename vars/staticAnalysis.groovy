def call(Map config = [:]) {

    def manualAbort = config.get('abortPipeline', false)
    
    def branchName = env.BRANCH_NAME ?: ""

    echo "Analizando rama: ${branchName}"

    def scannerHome = tool 'SonarScanner'
    
    withSonarQubeEnv('Sonar Local') { 
        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=devops_ws -Dsonar.sources=."
    }

    timeout(time: 5, unit: 'MINUTES') {

        def qg = waitForQualityGate()

        
        def debeAbortar = false

        if (manualAbort) {
            debeAbortar = true 
        } else if (branchName.contains("master") || branchName.contains("main")) {
            debeAbortar = true 
        } else if (branchName.startsWith("hotfix")) {
            debeAbortar = true 
        }

        echo "Estado SonarQube: ${qg.status} | Política: Rama=${rama}, Abortar=${debeAbortar}"

        if (qg.status != 'OK' || debeAbortar) {
            error "Pipeline abortado por política de rama (${branchName}) o parámetro manual."
        } else {
            echo "Análisis superado o permitido en rama: ${branchName}. Abortar: ${debeAbortar}"
        }
    }
}