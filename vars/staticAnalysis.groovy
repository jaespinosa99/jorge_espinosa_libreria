def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)

    withSonarQubeEnv('Sonar Local') {
        sh 'echo "Ejecución de las pruebas de calidad de código"'
    }

   
    timeout(time: 5, unit: 'MINUTES') {
        def qg = waitForQualityGate() 
        
        if (qg.status != 'OK' && abortPipeline) {
            error "Pipeline abortado debido a que el Quality Gate falló y abortPipeline es True" 
        } else {
            echo "Continuando pipeline. Estado Quality Gate: ${qg.status}. AbortPipeline es: ${abortPipeline}" 
        }
    }
}