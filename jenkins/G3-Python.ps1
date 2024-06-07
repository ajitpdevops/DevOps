function Invoke-PythonScript {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$Username,
        
        [Parameter(Mandatory = $true)]
        [string]$Password,
        
        [Parameter(Mandatory = $true)]
        [string]$LinuxServerName,
        
        [Parameter(Mandatory = $true)]
        [string]$PythonScriptPath
    )
    
    try {
        # Load the Posh-SSH module
        Import-Module Posh-SSH -DisableNameChecking -ErrorAction Stop
        
        # Set up the SSH session
        $sshSession = New-SSHSession -ComputerName $LinuxServerName -Credential (New-Object System.Management.Automation.PSCredential ($Username, (ConvertTo-SecureString $Password -AsPlainText -Force)))
        
        # Copy the Python script to the Linux machine
        $scriptName = (Split-Path $PythonScriptPath -Leaf)
        Write-Output "Copying $scriptName to the Linux machine..."
        Copy-SCPFile -SessionId $sshSession.SessionId -RemotePath "/tmp/$scriptName" -LocalFile $PythonScriptPath -ErrorAction Stop
        
        # Execute the Python script on the Linux machine
        Write-Output "Executing $scriptName on the Linux machine..."
        $command = "python /tmp/$scriptName"
        $result = Invoke-SSHCommand -SessionId $sshSession.SessionId -Command $command -ErrorAction Stop
        
        # Print the output of the Python script
        Write-Output "Python script output:"
        $result.Output | ForEach-Object { Write-Output $_ }
        
        # Remove the script from the Linux machine
        Write-Output "Removing $scriptName from the Linux machine..."
        Invoke-SSHCommand -SessionId $sshSession.SessionId -Command "rm /tmp/$scriptName" -ErrorAction Stop
        
    }
    catch {
        Write-Error $_.Exception.Message
    }
    finally {
        # Close the SSH session
        if ($sshSession) {
            Write-Output "Closing SSH session..."
            $sshSession.Dispose()
        }
    }
}
