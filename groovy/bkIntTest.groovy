def appVersion = new String(env.Version ?: false)
def deploy = new Boolean(env.Deploy ?: false)
def deploySandbox = new Boolean(env.DeploySandbox ?: false)
def cleanTarget = new Boolean(env.cleanTarget ?: false)
def testOperaHappyPath = new Boolean(env.OperaHappyPath ?: false)
def testOperaDecision = new Boolean(env.OperaDecision ?: false)
def testOperaCDP = new Boolean(env.OperaCDP ?: false)
def testIntegration = new Boolean(env.IntegrationTest ?: false)
def testCucumber = new Boolean(env.CucumberTest ?: false)
def testVaadin = new Boolean(env.VaadinTest ?: false)
def generateReport = new Boolean(env.GenerateReport ?: false)
String testRunVaadinTest = (env.RunVaadinTest ?: 'default')
String testRunIntegrationTest = (env.RunIntegrationTest ?: 'default')
String testRunCucumberTest = (env.RunCucumberTest ?: 'default')
def newShell = new Boolean(env.VaadinNewShell ?: true)
def buildJavaHome = "C:/Programs/Java/Java-11.0.8_10"  // Build parameters
def buildMavenHome = "C:/Programs/Maven/apache-maven-3.1.1"
def enableExtentReporting = new Boolean(env.enableExtentReporting ?: false)
def isReleaseCut = new Boolean(env.isReleaseCut ?: false)
def enableAutomaticDefectReporting = new Boolean(env.enableAutomaticDefectReporting ?: false)

node('mn4dg3xenvw017') {
    withCredentials([
            [$class: 'StringBinding', credentialsId: 'c5d74771-a584-4504-91cb-ac53beaaaccd', variable: 'rallyApiKey'],
            [$class: 'UsernamePasswordMultiBinding', credentialsId: 'qa.jira.creds', usernameVariable: 'jiraUser', passwordVariable: 'jirapassword']
    ])
            {
                // set rally creds


                ws() {
                    dir('repo') {
                        def repoPath = pwd() // Get and set the repo fill path
                        def testSettings = "${repoPath}/modules/G3/Maven/integration.xml"
                        currentBuild.setDescription("Version: ${appVersion}")
                        stage('Pull') {
                            
                            // Set git branch variables for checkout
                            buildBranch = 'main-' + appVersion
                            buildRefspec = '+refs/heads/' + buildBranch + ':refs/remotes/origin/' + buildBranch
                            // Delete directories matching 'target' from repo
                            def clean = "Import-Module -Name C:/AlphaFS/DotNet35/AlphaFS.dll; Get-ChildItem ${repoPath} -Recurse ^| Where {\$_.PSIsContainer -and (\$_ -match 'target.*')} ^| ?{[Alphaleonis.Win32.Filesystem.Directory]::Delete(\$(\$_.FullName), \$True)}"

                            if (cleanTarget) {
                                bat "powershell.exe -Command $clean"
                            }

                            // Start checkout from git
                            checkout(poll: false, changelog: true, scm: [
                                    $class                           : 'GitSCM',
                                    branches                         : [[name: buildBranch]],
									browser							 : [$class: 'GithubWeb', repoUrl: 'https://github.com/ideasorg/g3.git'],
                                    extensions                       : [
                                            [$class: 'CleanBeforeCheckout'],
											[$class: 'BuildChooserSetting', buildChooser: [$class: 'DefaultBuildChooser']],
                                            [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [
                                                    [path: 'pom.xml'], [path: 'source/pom.xml'], [path: 'source/ui/pom.xml'], [path: 'source/ui/vaadin'], [path: 'app-test/integration'], [path: 'modules']
                                            ]],
                                            [$class: 'CloneOption', timeout: 30, noTags: true, reference: '', shallow: true]
                                    ],
                                    doGenerateSubmoduleConfigurations: false,
                                    submoduleCfg                     : [],
                                    userRemoteConfigs				 : [
											[credentialsId: '14501a3d-1a80-4894-93d9-14f8fc3ed10a', name: 'origin', refspec: buildRefspec, url: 'https://github.com/ideasorg/g3.git']		
                                    ]
                            ])
                        }
                        // Set the environment variables for the maven calls for tests
                        withEnv([
                                "JAVA_HOME=${buildJavaHome}",
                                "JAVA_OPTS=-Xmx2048m",
                                "M2_HOME=${buildMavenHome}",
                                "MAVEN_OPTS=-Xmx512m",
                                "PATH+=${buildJavaHome}/bin;${buildMavenHome}/bin"
                        ]) {

                            // println "Rally API Key is : ${rallyApiKey}"
                            // Set integration command
                            integrationBaseDir = pwd() + "/app-test/integration"
                            integrationCmd = "mvn integration-test -B -f ${integrationBaseDir}/pom.xml -s ${testSettings} -Dconnection.pool.size.per.tenant.db=1000 -DappVersion=${appVersion} -Dmaven.test.failure.ignore -DenvHost=mn4dg3xenvw017 -DenvDomain=ideasdev.int "
                            integrationCmd += " -Dis.Release.Cut=${isReleaseCut}"
                            integrationCmd += " -Denable.Automation.Defect.Reporting=${enableAutomaticDefectReporting}"
                            integrationCmd += " -Ddefect.apiKey=${rallyApiKey}"
                            integrationCmd += " -Dextent.test.report.enabled=${enableExtentReporting}"
                            integrationCmd += " -Ddefect.jiraId=${jiraUser}"
                            integrationCmd += " -Ddefect.jiraApiKey=${jirapassword}"
                            integrationCmd += " -DjenkinsBuildNumber=${BUILD_NUMBER}"

                            // Set opera command
                            operaCmd = "mvn integration-test -s ${testSettings} -B -f ${integrationBaseDir}/pom.xml -DappVersion=${appVersion} -Dconnection.pool.size.per.tenant.db=1000 -Dmaven.test.failure.ignore -DenvHost=mn4dg3xenvw017 -DenvDomain=ideasdev.int "

                            // Set cucumber command
                            cucumberBaseDir = pwd() + "/app-test/integration"
                            cucumberCmd = "mvn test -B -f ${cucumberBaseDir}/pom.xml -s ${testSettings} -DappVersion=${appVersion} -Dconnection.pool.size.per.tenant.db=1000 -DfailIfNoTests=false -Dmaven.test.failure.ignore -DenvHost=mn4dg3xenvw017 -DenvDomain=ideasdev.int "
                            cucumberCmd += " -Dis.Release.Cut=${isReleaseCut}"
                            cucumberCmd += " -Denable.Automation.Defect.Reporting=${enableAutomaticDefectReporting}"
                            cucumberCmd += " -Ddefect.apiKey=${rallyApiKey}"
                            cucumberCmd += " -Dextent.test.report.enabled=false"
                            cucumberCmd += " -Ddefect.jiraId=${jiraUser}"
                            cucumberCmd += " -Ddefect.jiraapiKey=${jirapassword}"
                            cucumberCmd += " -DjenkinsBuildNumber=${BUILD_NUMBER}"

                            // Set vaadin command
                            vaadinBaseDir = pwd() + "/source/ui/vaadin/vaadin-web-testbench"
                            vaadinTestChromeDriverPath = vaadinBaseDir + "/src/test/java/com/programs/chromedriver"
                            vaadinBaseUrl = "http://mn4dg3xenvw017.ideasdev.int/"
                            vaadinCmd = "mvn integration-test -B -s ${testSettings} -Dmaven.test.failure.ignore -DenvHost=mn4dg3xenvw017 -DenvDomain=ideasdev.int -Ptestbench,-includeJunitSurefirePlugin"
                            vaadinCmd += " -Dtestbench.domain=$vaadinBaseUrl"
                            vaadinCmd += " -DexcludedGroups=incubator"
                            vaadinCmd += " -Dextent.test.report.enabled=$enableExtentReporting"

                            vaadinCmd += " -DappVersion=$appVersion"
                            vaadinCmd += " -Dtestbench.chromeDriver.path=$vaadinTestChromeDriverPath"
                            vaadinCmd += " -Dtestbench.driver=chromeHeadless"
                            vaadinCmd += " -DjenkinsBuildNumber=${BUILD_NUMBER}"
                            vaadinCmd += " -Dtestbench.test.new.shell=${newShell}"
                            vaadinCmd += " -Dis.Release.Cut=${isReleaseCut}"
                            vaadinCmd += " -Denable.Automation.Defect.Reporting=${enableAutomaticDefectReporting}"
                            vaadinCmd += " -Ddefect.apiKey=${rallyApiKey}"
                            vaadinCmd += " -Ddefect.jiraId=${jiraUser}"
                            vaadinCmd += " -Ddefect.jiraApiKey=${jirapassword}"

                            testngXmlCmd = "mvn integration-test -B -s ${testSettings} -f ${vaadinBaseDir}/pom.xml -Dmaven.test.failure.ignore -Ptestbench,-includeJunitSurefirePlugin"
                            testngXmlCmd += " -Dtest=com.ideas.tetris.ui.api.DailyRunXMLSuiteGenerator"
                        }

                        stage('Deploy') {
                            if (deploy) {
                                parallel(
                                        QAStandaloneSQLHybrid02: {
                                            DeployOnServer('G3Environment-DockerDB', appVersion)
                                        }
                                )
                            }
                        }

                        stage('IntegrationTest') {
                            if (testIntegration) {
                                try {
                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobalAndJob('G3Environment-DockerDB', deploySandbox, appVersion, 'DABDE')
                                    }

                                    Integration_CRS_Data_Extract:
                                    {
                                        bat "$integrationCmd -DbuildDirectory=target\\Integration-CRS_Data_Extract -Dtest=integration.g3.idg.CrsExtractWorkflowFromPullExtractToProcessDecisionAckTest"
                                    }

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobalAndJob('G3Environment-DockerDB', deploySandbox, appVersion, 'MEMPR,ISYNC')
                                    }
                                    parallel(
                                            Integration_Purge: {
                                                println "Inside Stage Integration Test parallel thread"
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Purge_Property -Dtest=integration.g3.jobs.purge.PurgePropertyJobTest"
                                            },
                                            Integration_Purge_Global: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Purge_Global -Dtest=integration.g3.jobs.purge.PurgeGlobalDataJobTest"
                                            },
                                            Integration_Add_Client: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Add_Client -Dtest=integration.g3.support.installation.AddClientTest"
                                            }
                                    )
                                   
                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATVDW5,RATVDW4')
                                    }
                                    parallel(
                                            Integration_Variable_Decision_Window_Disabled: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Variable_Decision_Window_Disabled -Dtest=integration.g3.decision.hiltondecision.VariableDecisionWindowDisabled"
                                            },
                                            Integration_Variable_Decision_Window_Override_Sync_ON: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Variable_Decision_Window_Override_Sync_ON -Dtest=integration.g3.decision.hiltondecision.VariableDecisionWindowEnabledDifferentialVDWOverrideSyncJob"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATHIL,RATCHD,XNAES')
                                    }
                                    parallel(

                                            Integration_LDB_MS_Clone_Job: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-LDB_MS_Clone_Job -Dtest=integration.g3.configure.forecasts.limiteddatabuild.LDBJobWithMSCloneTest"
                                            },
                                            Integration_LMS_Refresh_Property_Job: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-LMS_Refresh_Property_Job -Dtest=integration.learning.RefreshPropertyJobTest"
                                            }

                                    )
                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'DALMC')
                                    }
                                    parallel(
                                            Integration_FGCreate_Commit_NewMS: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-FGCreate_Commit_NewMS -Dtest=integration.g3.jobs.createcommit.PostExtractForecastGroupCreateCommitNewMarketSegmentTest"
                                            },
                                            Integration_Create_Commit_Multiple_Scenarios: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Create_Commit_Multiple_Scenarios -Dtest=integration.g3.jobs.createcommit.CreateCommitForecastGroupsTest,integration.g3.population.ratchet.ExtractFileTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'ISYNC,OPER2,RATCHD,RATVDW4')
                                    }
                                    parallel(
                                            Integration_Sync_All_Scenarios: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Sync_All_Scenarios -Dtest=integration.g3.jobs.forcesync.*,!integration.g3.jobs.forcesync.ForceSyncCalibrationJobTest"
                                            },
                                            Integration_Hilton_Sufficient_Booked_Data_Test: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Hilton_Sufficient_Booked_Data_Test -Dtest=integration.g3.decision.hiltondecision.HiltonSufficientBookedDataTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATHIL')
                                    }
                                    parallel(
                                            Integration_Ratchet_Hilstar_Extract_Processing: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Hilton_BDE_Processing -Dtest=RatchetHilstarMcatFlowWithApplyTaxTrueUseGroupDataJobTest,RatchetHilstarMcatFlowWithApplyTaxTrueUseGroupDataFalseJobTest,RatchetHilstarMcatFlowWithApplyTaxTrueJobTest,RatchetHilstarMcatFlowWithESFalseJobTest,RatchetHilstarMcatFlowWithESTrueJobTest,RatchetHilstarUseMarketOccupancyJobTest,RatchetHilstarYieldCurrenyJobTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'XNAES,RATCHD,LAXAG,ESA,CPGP01,CPGP02,OPERA,OPERA3,TLHTL')
                                    }
                                    parallel(
                                            Integration_LDB_Self_Clone: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-LDB_Job_With_Self_Clone -Dtest=integration.g3.configure.forecasts.limiteddatabuild.LDBJobWithSelfCloneTest"
                                            },
                                            Integration_LDB_Generic_Flow: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-LDB_Job_With_Generic_Flow -Dtest=integration.g3.configure.forecasts.limiteddatabuild.LDBJobWithGenericFlowTest"
                                            },
                                            Integration_Rate_Service: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Rate_Service -Dtest=integration.g3.configure.decisions.rateplanconfiguration.RateService"
                                            },
                                            Integration_Dailyrun_Report_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Dailyrun_Report_Excel -Dtest=integration.g3.report.excel.daily_run_suite.PostBDEExcelCompareTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'ESA')
                                    }
                                    parallel(
                                            Integration_ESA_Daily_Bar: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-ESA_Daily_Bar -Dtest=integration.g3.decision.esadecision.ESDailyBarDecision"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CPGP02')
                                    }
                                    parallel(
                                            Integration_rcrtautoconfigurationtest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-rcrtautoconfigurationtest -Dtest=integration.g3.jobs.rcrtautoconfiguration.rcrtautoconfigurationtest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CPGP01')
                                    }
                                    parallel(
                                            Integration_ProfitPopulation: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-ProfitPopulation -Dtest=integration.g3.jobs.profitpopulation.ProfitPopulationTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'XNAES,TLHTL')
                                    }
                                    parallel(
                                            Integration_Suite1_Booking_Pace_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite1-Booking_Pace_Excel -Dtest=integration.g3.report.excel.suite1.BookingPaceExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,XNAES,CPGP01')
                                    }
                                    parallel(
                                            Integration_Suite1_Booking_Situation_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite1-Booking_Situation_Excel -Dtest=integration.g3.report.excel.suite1.BookingSituationExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'XNAES')
                                    }
                                    parallel(
                                            Integration_Suite1_Forecast_Validation_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite1-Forecast_Validation_Excel -Dtest=integration.g3.report.excel.suite1.ForecastValidationExcelCompareTestsForG3Reports"
                                            },
                                            Integration_Suite1_Operations_Report: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite1-Operations_Report -Dtest=integration.g3.report.excel.suite1.OperationsReportExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,TLHTL')
                                    }
                                    parallel(
                                            Integration_Suite2_Market_Segment_Mapping: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite2-Market_Segment_Mapping -Dtest=integration.g3.report.excel.suite2.MarketSegmentMappingExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CPGP01')
                                    }
                                    parallel(
                                            Integration_HTNGDecisionDeliveryJobStepsWithNewStyleParams: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-HTNGDecisionDeliveryJobStepsWithNewStyleParams -Dtest=integration.g3.decision.htngdecision.HTNGDecisionDeliveryJobStepsWithNewStyleParams"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,CPGP02')
                                    }
                                    parallel(
                                            Integration_HTNGDecision: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-HTNGDecision -Dtest=integration.g3.decision.htngdecision.HTNGDecision"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,CPGP02')
                                    }
                                    parallel(

                                            Integration_HTNGDecisionWithNewStyleParams: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-HTNGDecisionWithNewStyleParams -Dtest=integration.g3.decision.htngdecision.HTNGDecisionWithNewStyleParams"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,XNAES,CPGP01')
                                    }
                                    parallel(
                                            Integration_Suite2_Performance_Comparison: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite2-Performance_Comparison -Dtest=integration.g3.report.excel.suite2.PerformanceComparisonExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,CPGP02,CPGP01,OPERA3')
                                    }
                                    parallel(
                                            Integration_Suite3_Pick_Up_Or_Change_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite3-Pick_Up_Or_Change_Excel -Dtest=integration.g3.report.excel.suite3.PickUpOrChangeExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'ESA')
                                    }
                                    parallel(

                                            Integration_ESA_Daily_Bar: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Booking_Pace_Notificatio -Dtest=integration.g3.report.excel.suite1.BookingPace_NotificationConditionEvaluationTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHD')
                                    }
                                    parallel(
                                            Integration_Reservation_Data: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Reservation_Data -Dtest=integration.g3.population.ratchet.RatchdReservationDataTest,integration.g3.population.opera.ReservationDataTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHT')
                                    }
                                    parallel(
                                            Integration_Ratchet_PCRS_Extract_Processing: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-PCRS_Processing -Dtest=ESA_MCAT_RezviewFlowJobTest,ESABDECDPRezviewFlowJobTest,RatchetPcrsMcatApplyTaxTrueJobTest,RatchetPcrsMcatDailyESTrueFlowJobTest,RatchetPcrsMcatESFalseFlowJobTest,RatchetPcrsMcatESTrueFlowJobTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CTYHOCN,DALMC')
                                    }
                                    parallel(
                                            Integration_RateShopping_File_Arrived_Job: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-RateShopping_File_Arrived_Job -Dtest=integration.g3.population.rateshopping.RateShoppingFileArrivedJobTest"
                                            },
											Integration_G3_RDL_Rates_Response_Comparison: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-G3_RDL_Rates_Response_Comparison -Dtest=integration.g3.population.rateshopping.G3RDLRatesResponseComparisonTest"
                                            },
											Integration_Rdl_Full_Service_Stored_Proc: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Rdl_Full_Service_Stored_Proc -Dtest=integration.g3.population.rateshopping.RdlFullServiceStoredProcTest"
                                            }
                                    )
									
									if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'ESP')
                                    }
                                    parallel(
                                            Integration_Competitor_Rate_Rest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Competitor_Rate_Rest -Dtest=integration.g3.population.rateshopping.CompetitorRateRestTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHT')
                                    }
                                    parallel(
                                            Integration_Ratchet_PCRS_Mcat_Special: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-PCRS_Mcat_Special -Dtest=RatchetPcrsMcatSpecialSRPHandlingDailyESTrueDiffPopulationTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHT')
                                    }
                                    parallel(
                                            Integration_Ratchet_PCRS_Resolve_Hilton: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-PCRS_Resolve_Hilton -Dtest=RatchetPcrsResolveHiltonRawRateOverlapTrueFlowJobTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHT')
                                    }
                                    parallel(
                                            Integration_Ratchet_PCRS_Use_Market_Occupancy: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-PCRS_Use_Market_Occupancy -Dtest=RatchetPcrsUseMarketOccupancyJobTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHT')
                                    }
                                    parallel(
                                            Integration_Ratchet_PCRS_Yield_Currency: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-PCRS_Yield_Currency -Dtest=RatchetPcrsYieldCurrenyJobTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'NIKKOSF,RATCHD')
                                    }
                                    parallel(
                                            Integration_LDB_Job_Continuous_Pricing: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-LDB_Job_With_Continuous_Pricing -Dtest=integration.g3.configure.forecasts.limiteddatabuild.LDBJobWithCPTest"
                                            },
                                            Integration_LMS_Build_Properties: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Build_Lms_Properties_Job -Dtest=integration.learning.BuildLmsPropertiesJobTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA')
                                    }
                                    parallel(
                                            Integration_Opera_Manual_BAR_Upload: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Opera_Manual_BAR_Upload_Decision -Dtest=integration.g3.decision.operadecision.OperaManualBARUploadDecisionTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATVDW2')
                                    }
                                    parallel(
                                            Integration_Statistical_Outlier_Test: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Statistical_Outlier -Dtest=integration.g3.monitor.informationmanager.alerts.HighestFPLOSCheckForHiltonBDETest,integration.g3.monitor.informationmanager.alerts.LowestFPLOSCheckForHiltonBDETest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHD')
                                    }
                                    parallel(
                                            Integration_Ratchet_CDP_Extract_Processing: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Hilton_CDP_Processing -Dtest=RatchdCDPExtractJobTest,RatchdDailyExtractJobTest,RatchdDailyExtractWithResumableTrueTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobalAndJob('G3Environment-DockerDB', deploySandbox, appVersion, 'ISYNC,YYZBO')
                                    }
                                    parallel(
                                            Integration_LDB_Budget_Data_Processing: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-LDB_Budget_Processing -Dtest=integration.g3.configure.forecasts.limiteddatabuild.BudgetSheetTest"

                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobalAndJob('G3Environment-DockerDB', deploySandbox, appVersion, 'YYZBO')
                                    }
                                    parallel(
                                            Integration_RatchetCPMigrationFlowE2ETest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-RatchetCPMigrationFlowE2ETest -Dtest=integration.g3.population.ratchet.RatchetCPMigrationFlowE2ETest"

                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL')
                                    }
                                    parallel(
                                            Integration_Suite3_Rate_Plan_Production: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite3-Rate_Plan_Production -Dtest=integration.g3.report.excel.suite3.RatePlanProductionExcelCompareTestsForG3Reports"
                                            },
                                            Integration_SaleforceSync: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Saleforce_Sync -Dtest=integration.g3.jobs.salesforcesynchronization.SalesforceSyncSanity"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,OPERA3,TERRANEA,CPGP01,CPGP02')
                                    }
                                    parallel(
                                            Integration_Suite4_Data_Extraction_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite4-Data_Extraction_Excel -Dtest=integration.g3.report.excel.suite4.DataExtractionExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'XNAES,CPGP01')
                                    }
                                    parallel(
                                            Integration_Suite4_Output_Override_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite4-Output_Override_Excel -Dtest=integration.g3.report.excel.suite4.OutputOverrideExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,OPERA3,CPGP01')
                                    }
                                    parallel(
                                            Integration_Suite4_Pricing_Excel_Compare: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite4-Pricing_Excel_Compare -Dtest=integration.g3.report.excel.suite4.PricingExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,CPGP02')
                                    }
                                    parallel(
                                            Integration_Suite4_Pricing_Override_History: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite4-Pricing_Override_History -Dtest=integration.g3.report.excel.suite4.PricingOverrideHistoryExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,XNAES')
                                    }
                                    parallel(
                                            Integration_Suite5_Comparative_Booking_Pace: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite5-Comparative_Booking_Pace -Dtest=integration.g3.report.excel.suite5.ComparativeBookingPaceExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'TLHTL,CPGP02,CPGP01')
                                    }
                                    parallel(
                                            Integration_Suite5_Input_Override_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite5-Input_Override_Excel -Dtest=integration.g3.report.excel.suite5.InputOverrideExcelCompareTestsForG3Reports"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'LAXAG,XNAES')
                                    }
                                    parallel(
                                            Integration_Suite5_Inventory_History_Excel: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Suite5-Inventory_History_Excel -Dtest=integration.g3.report.excel.suite5.InventoryHistoryExcelCompareTestsForG3Reports"
                                            }
                                    )


                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATHIL,RATCHT,YYZBO')
                                    }
                                    parallel(
                                            Integration_PcrsRoomTypeRecodingTest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-PcrsRoomTypeRecodingTest -Dtest=integration.g3.population.ratchet.PcrsRoomTypeRecodingTest"
                                            },
                                            Integration_HilstarRoomTypeRecodingTest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-HilstarRoomTypeRecodingTest -Dtest=integration.g3.population.ratchet.HilstarRoomTypeRecodingTest"
                                            },
                                            Integration_RatchetCPMigrationESFlowE2ETest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-RatchetCPMigrationESFlowE2ETest -Dtest=integration.g3.population.ratchet.RatchetCPMigrationESFlowE2ETest"
                                            },
                                            Integration_ESLDBAddPropertyTest: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-ESLDBAddPropertyTest -Dtest=integration.g3.support.installation.ESLDBAddPropertyTest"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'MEMPR')
                                    }
                                    parallel(
                                            Integration_Alert_Notification: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-Alert_Notification -Dtest=integration.g3.monitor.informationmanager.notifications.*"
                                            }
                                    )
									 
									if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobalAndJob('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHT')
                                    }
                                    parallel(
                                            Integration_REZVIEW_RoomTypeRecoding: {
                                                bat "$integrationCmd -DbuildDirectory=target\\Integration-Slow-REZVIEW-RoomTypeRecoding -Dtest=integration.g3.population.ratchet.RezviewRoomTypeRecodingTest"
                                            }
                                    )

                                } catch (Exception ex) {
                                    println ex
                                } finally {

                                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'app-test/integration/target/test-classes/ExtentReports/', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
                                }
                            }
                        }

                        stage('CucumberTest') {
                            if (testCucumber) {
                                try {
                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobalAndJob('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,OPER2,TERRANEA,XNAES')
                                    }

                                    parallel(
                                            Cucumber_Opera_Population_Daily_Run: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Opera_Population_Daily_Run -Dtest=OperaDailyRunTestRunner"
                                            },
                                            Cucumber_Component_Room: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Component_Room -Dtest=ComponentRoomWithNewSASFlowTestRunner"
                                            },
                                            Cucumber_Rate_Plan_Strategy: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Rate_Plan_Strategy -Dtest=RatePlanStrategyTestRunner"
                                            },
                                            Cucumber_Monitoring_Dashboard: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Monitoring_Dashboard -Dtest=MonitoringDashboardTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'MEMPR,OPERA,CPGP01,CPGP02,OPER2,TLHTL')
                                    }
                                    parallel(
                                            Cucumber_Reports: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Reports -Dtest=ReportsTestRunner"
                                            },
                                            Cucumber_Trans_Population: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Trans_Population -Dtest=TransPopulationTestRunner"
                                            },
                                            Cucumber_Multi_Property_Group_Pricing: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Multi_Property_Group_Pricing -Dtest=MultiPropertyGroupPricingTestRunner"
                                            },
                                            Cucumber_Opera_Decision: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Opera_Decision -Dtest=OperaEndToEndTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'MEMPR,OPERA,CPGP01,CPGP02,OPER2,TLHTL')
                                    }
                                    parallel(
                                            Cucumber_Decision: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Decision -Dtest=DecisionDailyRunTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CPGP02')
                                    }
                                    parallel(
                                            Cucumber_AllInclusivePostDepartureTest: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-AllInclusivePostDepartureTest -Dtest=AllInclusivePostDepartureTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CPGP01,OPER2,LAXAG,OPERA,TLHTL')
                                    }
                                    parallel(
                                            Cucumber_Continuous_Pricing: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Continuous_Pricing -Dtest=ContinuousPricingTestRunner"
                                            },
                                            Cucumber_Group_Pricing: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Group_Pricing -Dtest=GroupPricingTestRunner"
                                            },
                                            Cucumber_Opera_Room_Type_Recoding: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Opera_Room_Type_Recoding -Dtest=OperaRoomTypeRecodeTestRunner"
                                            },
                                            Cucumber_Sum_Of_Parts: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Sum_Of_Parts -Dtest=SumOfPartsTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,CPGP01,MEMPR,ESA,LAXAG,CPGP02,TLHTL,RATVDW2,RATHIL,RATCHD')
                                    }
                                    parallel(
                                            Cucumber_Population_Overhaul: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Population_Overhaul -Dtest=PopulationOverhaulTestRunner"
                                            },
                                            Cucumber_Rest_API_User_Management: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Rest_API_User_Management -Dtest=RESTAPIUserManagementTestRunner"
                                            },
											Cucumber_Rollback_Property: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Rollback_Property -Dtest=RollbackPropertyRunner"
                                            },
                                            Cucumber_Group_Floor: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Group_Floor -Dtest=GroupFloorTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,RATCHT,RATHIL,CPGP02')
                                    }
                                    parallel(
                                            Cucumber_Opera_Population: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-OperaPopulation -Dtest=OperaPopulationTestRunner"
                                            },
                                            Cucumber_Authentication: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Authentication  -Dtest=AuthenticationTestRunner "
                                            },
                                            Cucumber_Recommendation: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Recommendation  -Dtest=RecommendationTestRunner "
                                            },
                                            Cucumber_Agile_Rates: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Agile_Rates -Dtest=AgileRatesTestRunner"
                                            }
                                    )
                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATHIL,ISYNC,OPER2,RATCHD,RATVDW4,CPGP01')
                                    }
                                    parallel(
                                            Cucumber_Per_Person_Pricing: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Fast-Per_Person_Pricing -Dtest=PerPersonPricingTestRunner"
                                            },
                                            Cucumber_AMS_Rebuild_Runner: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-AMS_Rebuild_Runner -Dtest=AMSRebuildRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'RATCHD,RATCHT,LAXAG,TLHTL,XNAES,ESA,CPGP01,CTYHOCN,RATVDW2')
                                    }
                                    parallel(
                                            Cucumber_Analytical_Market_Segment: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Fast-Analytical_Market_Segment -Dtest=AMSDailyRunTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPER2,OPERA,LAXAG,CPGP02')
                                    }
                                    parallel(
                                            Cucumber_PostBde: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-PostBde -Dtest=ScheduledReportAfterBDEEndToEndTestRunner"
                                            },
                                            Cucumber_HospitalityAgent: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-HospitalityAgent -Dtest=HospitalityAgentTestRunner"
                                            },
                                            Cucumber_AgileRatesOverride: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-AgileRatesOverrides -Dtest=AgileRatesOverridesTestRunner"
                                            },
                                            Cucumber_Bar_Entry: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Bar_Entry -Dtest=BarEntryRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'CPGP02,XNAES,ESA,LAXAG,CTYHOCN')
                                    }
                                    parallel(
                                            Cucumber_OverrideAndPerformSync: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Override_Perform_Sync -Dtest=OverrideAndPerformSyncRunner"
                                            },
                                            Cucumber_ESAPostmanModule: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-ESA_Postman_Module -Dtest=ESAProductConfigurationTestRunner"
                                            },
                                            Cucumber_Rate_Qualified_Entry: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Rate_Qualified_Entry -Dtest=RateQualifiedEntryRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'XNAES')
                                    }
                                    parallel(
                                            Cucumber_Discover: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Discover -Dtest=DiscoverTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'DALMC')
                                    }
                                    parallel(
                                            Cucumber_RDL: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-RDL -Dtest=RdlTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, '0046')
                                    }
                                    parallel(
                                            Cucumber_MarketSegmentRecodingTest: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-MarketSegmentRecodingTest -Dtest=MarketSegmentRecodingTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'XNAES')
                                    }
                                    parallel(
                                            Cucumber_Discoverv3Test: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Discoverv3Test -Dtest=Discoverv3TestRunner"
                                            }
                                    )
									
									if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'YYZBO')
                                    }
                                    parallel(
                                            Cucumber_CentralRMSRemoveOvrJob: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-CentralRMSRemoveOvrJob -Dtest=CentralRMSRemoveOvrJobTestRunner"
                                            }
                                    )
									
									if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'YYZBO')
                                    }
                                    parallel(
                                            Cucumber_CentralRMSTaxEnabled: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-CentralRMSTaxEnabled -Dtest=CentralRMSTaxEnabledTestRunner"
                                            }
                                    )

                                    if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'YYZBO')
                                    }
                                    parallel(
                                            Cucumber_CentralRMS: {
                                                bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-CentralRMS -Dtest=CentralRMSTestRunner"
                                            }
                                    )
									
									if (deploySandbox) {
                                        DeploySpecificSandboxPropertiesWithCleanGlobal('G3Environment-DockerDB', deploySandbox, appVersion, 'OPERA,CPGP01,MEMPR,ESA,LAXAG,CPGP02,TLHTL,RATVDW2,RATHIL')
                                    }
                                    parallel(
                                            Cucumber_Alerts: {
												bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Slow-Alerts -Dtest=AlertsTestRunner"      
											}
                                    )

                                } catch (Exception ex) {
                                    println ex
                                } finally {

                                }
                            }
                        }

                        stage('VaadinTest') {


                            stage('Opera') {
                                if (testOperaHappyPath) {
                                    try {
                                        parallel(
                                                operaAgentHappyPath: {
                                                    bat "$operaCmd -DbuildDirectory=target\\Integration-Opera_Agent_Happy_Path -Dtest=integration.g3.agent.OperaAgentHappyPathTest"
                                                }
                                        )
                                    } finally {
                                        step([$class: 'JUnitResultArchiver', testResults: '**/app-test/integration/target/Integration-*/surefire-reports/*.xml'])
                                        step([$class: 'ArtifactArchiver', artifacts: '**/app-test/integration/target/', fingerprint: true])
                                    }
                                }
                                if (testOperaDecision) {
                                    try {
                                        parallel(
                                                operaAgentDecisions: {
                                                    bat "$operaCmd -DbuildDirectory=target\\Integration-Opera_Agent_Decisions -Dtest=integration.g3.agent.OperaAgentDecisionsTest"
                                                }
                                        )
                                    } finally {
                                        step([$class: 'JUnitResultArchiver', testResults: '**/app-test/integration/target/Integration-*/surefire-reports/*.xml'])
                                        step([$class: 'ArtifactArchiver', artifacts: '**/app-test/integration/target/', fingerprint: true])
                                    }
                                }
                                if (testOperaCDP) {
                                    try {
                                        parallel(
                                                operaAgentCDP: {
                                                    bat "$operaCmd -DbuildDirectory=target\\Integration-Opera_Agent_CDP -Dtest=integration.g3.agent.OperaAgentCdpTest"
                                                }
                                        )
                                    }
                                    finally {
                                        step([$class: 'JUnitResultArchiver', testResults: '**/app-test/integration/target/Integration-*/surefire-reports/*.xml'])
                                        step([$class: 'ArtifactArchiver', artifacts: '**/app-test/integration/target/', fingerprint: true])
                                    }
                                }
                            }

                            stage('RunVaadinTest') {
                                if (testRunVaadinTest != "default") {
                                    try {
                                        parallel(
                                                Vaadin_RandomTests: {
                                                    bat "$vaadinCmd -f ${vaadinBaseDir}/pom.xml -DbuildDirectory=target\\Vaadin-Random-${testRunVaadinTest} -Dgroups=${testRunVaadinTest}"
                                                }
                                        )
                                    }
                                    finally {
                                        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'source/ui/vaadin/vaadin-web-testbench/target/test-classes/ExtentReports/', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
                                        step([$class: 'Publisher', reportFilenamePattern: '**/surefire-reports/testng-results.xml'])
                                        step([$class: 'ArtifactArchiver', artifacts: '**/app-test/integration/target/', fingerprint: true])
                                    }
                                }
                            }

                            stage('RunIntegrationTest') {
                                if (testRunIntegrationTest != "default") {
                                    try {
                                        parallel(
                                                Integration_RandomTests: {
                                                    bat "$integrationCmd -DbuildDirectory=target\\Integration-Random-${testRunIntegrationTest} -Dtest=${testRunIntegrationTest}"
                                                }
                                        )
                                    }
                                    finally {
                                        step([$class: 'JUnitResultArchiver', testResults: '**/app-test/integration/target/Integration-*/surefire-reports/*.xml'])
                                        step([$class: 'ArtifactArchiver', artifacts: '**/app-test/integration/target/', fingerprint: true])
                                    }
                                }
                            }

                            stage('RunCucumberTest') {
                                if (testRunCucumberTest != "default") {
                                    try {
                                        parallel(
                                                Cucumber_RandomTests: {
                                                    bat "$cucumberCmd -DbuildDirectory=target\\Cucumber-Random-${testRunCucumberTest} -Dtest=${testRunCucumberTest}"
                                                }
                                        )
                                    }
                                    finally {
                                        step([$class: 'JUnitResultArchiver', testResults: '**/app-test/integration/target/Integration-*/surefire-reports/*.xml'])
                                        step([$class: 'JUnitResultArchiver', testResults: '**/app-test/integration/target/'])
                                    }
                                }
                            }

                            stage('GenerateReport') {
                                //Set Report Command
                                def reportJarDir = "C:/Automation_New_report/"
                                reportCmd = "${reportJarDir}/report.bat"
                                if (generateReport) {
                                    // Generate Report if generate report flag is true i.e. execute report.bat file
                                    bat "$reportCmd"
                                    println "Report Generated!"

                                }
                                if (enableExtentReporting) {

                                    if (testIntegration) {
                                        bat "$integrationCmd -DbuildDirectory=target\\Integration-MergedExtentReport -Dtest=common.MergeJSONExtentReports"
                                        def status = powershell(returnStatus: true, script: 'C:\\GenerateExtentReport.ps1')
                                        if (status == 0) {
                                            println "Extent Report converted to JSON Successfully."
                                        }
                                        bat "$integrationCmd -Dtest=common.GenerateFinalExtentReport"
                                        publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'app-test/integration/target/test-classes/ExtentReports', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
                                    }
                                    if (testVaadin) {
                                        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'source/ui/vaadin/vaadin-web-testbench/target/test-classes/ExtentReports/', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
                                        // Generate Report if generate report flag is true i.e. execute report.bat file
                                        bat "$reportCmd"
                                    }
                                }
                            }
                        }
                    }
                }
            }
}


def DeployOnServer(jobName, appVersion) {
// Call the QA deployment job and pass in the selected version, then wait for completion.
    def job = build(job: jobName, wait: true, propagate: true, parameters: [
            [$class: 'StringParameterValue', name: 'Version', value: appVersion],
            [$class: 'BooleanParameterValue', name: 'DeploySandbox', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySASClient', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployLMS', value: true],
            [$class: 'BooleanParameterValue', name: 'StopProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'StartProcessing', value: false]
    ])
}

def DeploySpecificSandboxPropertiesWithCleanGlobal(jobName, deploySandbox, appVersion, props) {


    def machineName = "mn4dg3xenvw017"

    // Call the QA deployment job and pass in the selected version, then wait for completion.
    def job = build(job: jobName, wait: true, propagate: true, parameters: [
            [$class: 'StringParameterValue', name: 'Version', value: appVersion],
            [$class: 'BooleanParameterValue', name: 'StopProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployDB', value: true],
            [$class: 'BooleanParameterValue', name: 'DeploySAS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySASClient', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySandbox', value: true],
            [$class: 'BooleanParameterValue', name: 'DeployLMS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployOpenDS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployOpenAM', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployJasper', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployWildFly', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployApache', value: false],
            [$class: 'BooleanParameterValue', name: 'StartProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'CleanDatabase', value: true],
            [$class: 'StringParameterValue', name: 'SandboxPropertiesToDeploy', value: props]
    ])
    try {
        //Enable G3 to use New Style HTNG Parameter
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/systemProperties/disable.comparing.oldStyleAndNewStyle/true?propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.preProduction.isNewStyleOfConfigParamEnabled?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.preProduction.isParameterMigrationWorkAroundEnabled?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.PreProduction.isNewStyleConfigParamMigrated?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"

        //Clearing Cache for BSTN and SandBox Client
        bat "powershell Invoke-WebRequest -Method POST -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/cache/clearAll?propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method POST -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/cache/clearAll?propertyId=11022' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
    }
    finally {
    }
}

def DeploySpecificSandboxPropertiesWithCleanGlobalAndJob(jobName, deploySandbox, appVersion, props) {

    def machineName = "mn4dg3xenvw017"

    // Call the QA deployment job and pass in the selected version, then wait for completion.
    def job = build(job: jobName, wait: true, propagate: true, parameters: [
            [$class: 'StringParameterValue', name: 'Version', value: appVersion],
            [$class: 'BooleanParameterValue', name: 'StopProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployDB', value: true],
            [$class: 'BooleanParameterValue', name: 'DeploySAS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySASClient', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySandbox', value: true],
            [$class: 'BooleanParameterValue', name: 'DeployLMS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployOpenDS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployOpenAM', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployJasper', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployWildFly', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployApache', value: false],
            [$class: 'BooleanParameterValue', name: 'StartProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'CleanDatabase', value: true],
            [$class: 'StringParameterValue', name: 'SandboxPropertiesToDeploy', value: props]
    ])
    try {
        //Enable G3 to use New Style HTNG Parameter
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/systemProperties/disable.comparing.oldStyleAndNewStyle/true?propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.preProduction.isNewStyleOfConfigParamEnabled?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.preProduction.isParameterMigrationWorkAroundEnabled?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.PreProduction.isNewStyleConfigParamMigrated?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"

        //Clearing Cache for BSTN and SandBox Client
        bat "powershell Invoke-WebRequest -Method POST -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/cache/clearAll?propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method POST -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/cache/clearAll?propertyId=11022' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
    }
    finally {
    }
}

def DeploySpecificSandboxPropertiesWithCleanJob(jobName, deploySandbox, appVersion, props) {
    def importModule = "Import-Module  C:/Programs/Jenkins/Slave/workspace/G3Environment-AutomationTest03-2/repo/modules/G3/G3.psd1;"
    def machineName = "mn4dg3xenvw017"
    try {
        bat "powershell ${importModule} Deploy-G3DatabaseJOB -DBplatformhost ${machineName} -dbclean -G3Version ${appVersion}"
    } finally {
    }
    // Call the QA deployment job and pass in the selected version, then wait for completion.
    def job = build(job: jobName, wait: true, propagate: true, parameters: [
            [$class: 'StringParameterValue', name: 'Version', value: appVersion],
            [$class: 'BooleanParameterValue', name: 'StopProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployDB', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySAS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySASClient', value: false],
            [$class: 'BooleanParameterValue', name: 'DeploySandbox', value: true],
            [$class: 'BooleanParameterValue', name: 'DeployLMS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployOpenDS', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployOpenAM', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployJasper', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployWildFly', value: false],
            [$class: 'BooleanParameterValue', name: 'DeployApache', value: false],
            [$class: 'BooleanParameterValue', name: 'StartProcessing', value: false],
            [$class: 'BooleanParameterValue', name: 'CleanDatabase', value: false],
            [$class: 'StringParameterValue', name: 'SandboxPropertiesToDeploy', value: props]
    ])
    try {
        //Enable G3 to use New Style HTNG Parameter
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/systemProperties/disable.comparing.oldStyleAndNewStyle/true?propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.preProduction.isNewStyleOfConfigParamEnabled?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.preProduction.isParameterMigrationWorkAroundEnabled?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method PUT -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/configParam/pacman.PreProduction.isNewStyleConfigParamMigrated?value=true\"&\"context=pacman\"&\"propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"

        //Clearing Cache for BSTN and SandBox Client
        bat "powershell Invoke-WebRequest -Method POST -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/cache/clearAll?propertyId=5' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
        bat "powershell Invoke-WebRequest -Method POST -Uri 'http://${machineName}.ideasdev.int:8080/pacman-platformsecurity/rest/cache/clearAll?propertyId=11022' -Headers @{Authorization = 'Basic c3NvQGlkZWFzLmNvbTpwYXNzd29yZA=='} -UseBasicParsing"
    }
    finally {
    }
}
