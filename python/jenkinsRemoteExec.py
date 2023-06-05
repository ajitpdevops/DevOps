import com.jcraft.jsch.*
import groovy.json.JsonOutput

def uploadFileToRemote(remoteHost, remotePort, remoteUser, remotePassword, localFilePath, remoteFilePath) {
    def jsch = new JSch()
    def session = jsch.getSession(remoteUser, remoteHost, remotePort)
    session.setPassword(remotePassword)
    session.setConfig("StrictHostKeyChecking", "no")
    session.connect()

    def channel = session.openChannel("sftp")
    channel.connect()
    def sftpChannel = (ChannelSftp) channel

    sftpChannel.put(localFilePath, remoteFilePath)

    sftpChannel.exit()
    session.disconnect()
}

def executeRemoteScript(remoteHost, remotePort, remoteUser, remotePassword, remoteScriptPath, scriptParameters) {
    def command = "python $remoteScriptPath ${JsonOutput.toJson(scriptParameters)}"
    def jsch = new JSch()
    def session = jsch.getSession(remoteUser, remoteHost, remotePort)
    session.setPassword(remotePassword)
    session.setConfig("StrictHostKeyChecking", "no")
    session.connect()

    def channel = session.openChannel("exec")
    def outputStream = new ByteArrayOutputStream()
    channel.setOutputStream(outputStream)

    def commandWithOutput = "$command 2>&1"
    channel.setCommand(commandWithOutput)

    channel.connect()

    while (!channel.isClosed()) {
        Thread.sleep(1000)
    }

    def exitCode = channel.getExitStatus()

    def output = outputStream.toString()
    def errorOutput = output.trim()

    channel.disconnect()
    session.disconnect()

    [exitCode: exitCode, output: output, errorOutput: errorOutput]
}

def pipeline = {
    stage("Python") {
        def remoteHost = "your_remote_host"
        def remotePort = 22
        def remoteUser = "your_remote_user"
        def remotePassword = "your_remote_password"
        def localScriptPath = "/path/to/local_script.py"
        def remoteScriptPath = "/path/to/remote_script.py"
        def scriptParameters = [param1: "value1", param2: "value2"]

        uploadFileToRemote(remoteHost, remotePort, remoteUser, remotePassword, localScriptPath, remoteScriptPath)
        def executionResult = executeRemoteScript(remoteHost, remotePort, remoteUser, remotePassword, remoteScriptPath, scriptParameters)

        if (executionResult.exitCode != 0) {
            throw new Exception("Python script execution failed with exit code: ${executionResult.exitCode}\nError output:\n${executionResult.errorOutput}")
        }

        println("Python script execution completed. Output:\n${executionResult.output}")
    }

    stage("PowerShell") {
        powershell "Get-ChildItem"
    }
}

pipeline()
