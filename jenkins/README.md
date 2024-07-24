# Why use JSch over paramiko for python and Posh-SSH for powershell?

JSch, Paramiko, and Posh-SSH are all popular SSH libraries for Python, PowerShell, and Groovy respectively. They all have their own strengths and weaknesses, so the best choice for you will depend on your specific needs.

JSch is a Java library that has been around for a long time and is very well-established. It is relatively easy to use and has a large community of users and contributors. However, it can be slow and memory-intensive.

Paramiko is a Python library that is newer than JSch, but it is gaining popularity due to its speed and efficiency. It is also more flexible than JSch, as it supports a wider range of SSH features. However, it can be more difficult to use than JSch.

Posh-SSH is a PowerShell library that is designed to be easy to use and integrate with other PowerShell modules. It is also very fast and efficient. However, it is not as widely used as JSch or Paramiko, so there may be less documentation and support available.

If you are using Groovy to execute things from a Windows host to Linux, then I would recommend using Posh-SSH. It is easy to use and integrate with other Groovy modules, and it is also very fast and efficient. However, if you need to support a wider range of SSH features, then you may want to consider using Paramiko instead.

Here is a table that summarizes the strengths and weaknesses of each library:

Library	Strengths	Weaknesses
JSch	Well-established, large community, easy to use	Slow, memory-intensive
Paramiko	Fast, efficient, flexible	More difficult to use
Posh-SSH	Easy to use, integrates well with other PowerShell modules, fast and efficient	Less widely used, less documentation and support

