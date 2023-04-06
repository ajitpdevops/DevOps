Function Deploy-G3Sandbox {
    [CmdletBinding(DefaultParametersetName = 'Standalone')]
    Param(
    # The target computername for a standalone deployment.
        [Parameter(ParameterSetName = "Standalone", Mandatory = $False)][String]$ComputerName = (Get-OpsHostEntry -ComputerName $env:ComputerName),
    # The target Platform hostname for a clustered deployment.
        [Parameter(ParameterSetName = "Cluster", Mandatory = $True)][String]$DBPlatformHost,
    # The target Tenant hostname for a clustered deployment.
        [Parameter(ParameterSetName = "Cluster", Mandatory = $True)][String]$DBTenantHost,
    # The target SAS hostname for a clustered deployment.
        [Parameter(ParameterSetName = "Cluster", Mandatory = $True)][String]$SASHost,
    # The target App hostname for a clustered deployment.
        [Parameter(ParameterSetName = "Cluster", Mandatory = $True)][String]$AppHost,
    # The environment definition to use defined in G3-Environments.json
        [Parameter(Mandatory = $False)][String]$Environment,
    # Override any defined environment properties.
        [Parameter(Mandatory = $False)][Hashtable]$Overrides,
    # For environments using encryption pass the decrypt key.
        [Parameter(Mandatory = $False)][SecureString]$PSKey,
    # To enable a secure prompt to provide the pskey.
        [Parameter(Mandatory = $False)][Switch]$PSKeyPrompt,
    # The G3 version to use for deployment.
        [Parameter(Mandatory = $False )][String]$G3Version = $Global:G3GlobalVersion,
    # List of properties to deploy.
        [Parameter(Mandatory = $False)]
        [ValidateSet('1111','ctyhocn', 'dabde', 'dalmc', 'esp', 'laxag',
                'mempr', 'ngi', 'ngi02', 'nikkosf', 'oper1',
                'oper2', 'opera', 'ratchd', 'ratcht', 'rathil',
                'ratvdw', 'tlhtl', 'isync', 'ratvdw2', 'ratvdw3',
                'ratvdw4', 'ratvdw5', 'roomcfg', 'xnaes',
                'terranea', 'cpgp01', 'cpgp02', 'opera3', 'esa','yyzbo','0046','cpip01','0026')]
        [String[]]$SandboxProperty = @(
    'ctyhocn', 'dabde', 'dalmc', 'esp', 'laxag',
    'mempr', 'ngi', 'ngi02', 'nikkosf', 'oper1',
    'oper2', 'opera', 'ratchd', 'ratcht', 'rathil',
    'ratvdw', 'tlhtl', 'isync', 'ratvdw2', 'ratvdw3',
    'ratvdw4', 'ratvdw5', 'roomcfg', 'xnaes',
    'terranea', 'cpgp01', 'cpgp02', 'opera3', 'esa','yyzbo','0046','cpip01','0026'
    ),
    # Skip seeding of global database with sandbox properties.
        [Parameter(Mandatory = $False )][Switch]$SkipGlobalSeed,
    # To skip update Roles for Seeded client.
        [Parameter(Mandatory = $False)][Switch]$SkipRolesUpdate,
    # Skip deploying of sandbox extracts.
        [Parameter(Mandatory = $False )][Switch]$SkipExtracts
    )
    Try {
        # Gets SecureString PSKey
        If ($PSKeyPrompt -and (-not $PSKey)) {
            $PSKey = Get-OpsPasswordSecureString
        }
        Write-OpsLog "Deploying Sandbox Properties" -Header
        # Support for standalone vs cluster.
        Switch ($PSCmdlet.ParameterSetName) {
            "Standalone" {
                $DBPlatformHost = (Get-OpsHostEntry -ComputerName $ComputerName)
                $DBTenantHost = (Get-OpsHostEntry -ComputerName $ComputerName)
                $SASHost = (Get-OpsHostEntry -ComputerName $ComputerName)
                $AppHost = (Get-OpsHostEntry -ComputerName $ComputerName)
            }
            "Cluster" {
                $DBPlatformHost = (Get-OpsHostEntry -ComputerName $DBPlatformHost)
                $DBTenantHost = (Get-OpsHostEntry -ComputerName $DBTenantHost)
                $SASHost = (Get-OpsHostEntry -ComputerName $SASHost)
                $AppHost = (Get-OpsHostEntry -ComputerName $AppHost)
            }
        }
        # Fetch G3 Properties.
        $G3Properties = Get-G3Properties `
            -Environment $Environment `
            -Overrides $Overrides `
            -PSKey $PSKey
        # Deploy sandbox extract(s).
        Deploy-G3SandboxGlobalSeed `
                -Environment $Environment `
                -PSKey $PSKey `
                -DBPlatformHost $DBPlatformHost `
                -DBTenantHost $DBTenantHost `
                -SASHost $SASHost `
                -AppHost $AppHost `
                -SkipGlobalSeed:$SkipGlobalSeed `
                -SkipRolesUpdate:$SkipRolesUpdate

        # Deploy sandbox extract(s).
        If (-Not($SkipExtracts)) {
            Deploy-G3SandboxExtract `
                -Environment $Environment `
                -PSKey $PSKey `
                -ComputerName $AppHost
        }
        # ForEach sandbox property.
        $SandboxProperty | ForEach-Object {
            # Fetch sandbox property meta.
            $G3SandboxProps = (Get-Content -Path $G3ModuleProperties\G3-Sandbox.json | ConvertFrom-Json).$_
            # print $G3Properties.OpsProperties.dbHostOSPlatform variable
            Write-OpsLog "DBHost OS Platform is : $($G3Properties.OpsProperties.dbHostOSPlatform)" -SubHeader
            Write-OpsLog "Sanbox property Version is : $($G3SandboxProps.dbVersion)" -SubHeader

            If ($G3SandboxProps.dbVersion) {
                Write-OpsLog "[$($_.toUpper())] Deploying DB" -SubHeader
                # Convert to a windows path.
                $DBSandboxPath = ($G3Properties.OpsProperties.sandboxDBDir | ConvertTo-OpsWindowsPath)
                if ( $G3Properties.OpsProperties.dbHostOSPlatform -eq "Windows" ) {
                    Remove-G3Database `
                        -DBHost $DBTenantHost `
                        -Credential $G3Properties.OpsCredentials.SQL `
                        -DBName  $G3SandboxProps.dbname
                    Download-G3Artifact `
                        -ComputerName $DBTenantHost `
                        -Credential  $G3Properties.OpsCredentials.Computer `
                        -Repo "ops-install-virtual" `
                        -Group "com.ideas.g3.sandbox.db" `
                        -Artifact $G3SandboxProps.propertyId `
                        -Type "zip" `
                        -Version  $G3SandboxProps.dbVersion `
                        -UnpackPath $DBSandboxPath `
                        -ForceUnpack
                    Attach-G3Database `
                        -DBHost $DBTenantHost `
                        -DBName $G3SandboxProps.dbname `
                        -Credential $G3Properties.OpsCredentials.SQL `
                        -DBDataFile "$($DBSandboxPath)\$($G3SandboxProps.dbname).mdf" `
                        -DBLogFile "$($DBSandboxPath)\$($G3SandboxProps.dbname)_log.ldf"
                    
                }
                Deploy-G3DatabaseTenant -ComputerName $DBPlatformHost -DBName $($G3SandboxProps.dbname) -PSKey $PSKey -G3Version $G3Version -BaselineVersion 1

            If ($G3SandboxProps.sasVersion) {
                Write-OpsLog "[$($_.toUpper())] Deploying SAS" -SubHeader
                $DataSet = $G3Properties.OpsProperties.sasPropDir + '\' + $G3SandboxProps.propertyId | ConvertTo-OpsWindowsPath
                Remove-OpsDirectories `
                    -ComputerNames $SASHost `
                    -Credential  $G3Properties.OpsCredentials.Computer `
                    -Directories "$($DataSet)"
                Download-G3Artifact `
                    -ComputerName $SASHost `
                    -Credential  $G3Properties.OpsCredentials.Computer `
                    -Repo "ops-install-virtual" `
                    -Group "com.ideas.g3.sandbox.sas" `
                    -Artifact $G3SandboxProps.propertyId `
                    -Type "zip" `
                    -Version  $G3SandboxProps.sasVersion `
                    -UnpackPath ($G3Properties.OpsProperties.sasPropDir | ConvertTo-OpsWindowsPath) `
                    -ForceUnpack
                Deploy-G3SASData `
                    -Environment $Environment `
                    -PSKey $PSKey `
                    -ComputerName $SASHost `
                    -Properties $G3SandboxProps.propertyId `
                    -G3Version $G3Version
            }
            If ($G3SandboxProps.ratchetVersion) {
                Write-OpsLog "[$($_.toUpper())] Deploying Ratchet" -SubHeader
                $RatchetDataSet = $G3Properties.OpsProperties.sasRatchetDir + '\Properties\Sandbox\' + $G3SandboxProps.propertyCode | ConvertTo-OpsWindowsPath
                Remove-OpsDirectories `
                    -ComputerNames $SASHost `
                    -Credential  $G3Properties.OpsCredentials.Computer `
                    -Directories "$($RatchetDataSet)"
                Download-G3Artifact `
                    -ComputerName $SASHost `
                    -Credential  $G3Properties.OpsCredentials.Computer `
                    -Repo "ops-install-virtual" `
                    -Group "com.ideas.g3.sandbox.ratchet" `
                    -Artifact $G3SandboxProps.propertyId `
                    -Type "zip" `
                    -Version  $G3SandboxProps.ratchetVersion `
                    -UnpackPath "$($G3Properties.OpsProperties.sasRatchetDir | ConvertTo-OpsWindowsPath)\Properties\Sandbox" `
                    -ForceUnpack
                Deploy-G3SASRatchetData `
                    -Environment $Environment `
                    -PSKey $PSKey `
                    -ComputerName $SASHost `
                    -Properties $G3SandboxProps.propertyCode `
                    -G3Version $G3Version
            }
            Write-OpsLog "[$($_.toUpper())] Completed" -SubHeader
        }
        Write-OpsLog "Completed" -Invocation $MyInvocation
    }
    Catch {
        Throw
    }
    Finally {
    }
}
