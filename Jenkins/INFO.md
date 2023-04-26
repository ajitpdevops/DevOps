# Python from powershell function 

## Install POSH-SSH module
1. Install-Module -Name Posh-SSH
2. https://github.com/darkoperator/Posh-SSH/blob/master/docs/Remove-SSHSession.md
3. Issue observered with Win 2012 - https://github.com/darkoperator/Posh-SSH/issues/437
4. Import-Module -Name Posh-SSH
5. $x = Find-Module -Name Posh-SSH
6. $x.AdditionalMetadata.Functions -split ' '
7. $x.AdditionalMetadata.Cmdlets -split ' '
8. Get-Command -Module Posh-SSH -Name *scp* | ForEach-Object {"$($_.Name): $((Get-Help -Name $_).Synopsis)"}

## Test File Copying through powershell - 
PS C:\Users\idnajp\Documents\01_TechOps\all-Info\PSTests> $username = 'idnajp'
PS C:\Users\idnajp\Documents\01_TechOps\all-Info\PSTests> $password = '<my_password>'
PS C:\Users\idnajp\Documents\01_TechOps\all-Info\PSTests> $creds = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $Username, (ConvertTo-SecureString -String $Password -AsPlainText -Force)
PS C:\Users\idnajp\Documents\01_TechOps\all-Info\PSTests> $linuxmachine = 'mn4dbldcisl004'
PS C:\Users\idnajp\Documents\01_TechOps\all-Info\PSTests> Set-SCPItem -ComputerName $linuxmachine -Credential $creds -Path .\G3\pyFunctions\runDockerContainer.py -Destination /tmp -Verbose
VERBOSE: Using SSH Username and Password authentication for connection.
VERBOSE: ssh-ed25519 Fingerprint for mn4dbldcisl004: 36:92:3c:63:22:ee:98:46:74:15:1c:b1:f1:db:81:2a
VERBOSE: Fingerprint matched trusted ssh-ed25519 fingerprint for host mn4dbldcisl004
VERBOSE: Connection successful
VERBOSE: Uploading: C:\Users\idnajp\Documents\01_TechOps\all-Info\PSTests\test.py
VERBOSE: Destination: /tmp/test.py