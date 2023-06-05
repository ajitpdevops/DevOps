import os 

x = os.system("ls -l")

os.getcwd()
os.chdir("/home/") 
os.listdir() # List all files in current directory
os.listdir("/home/") # List all files in a directory
os.mknod("test.txt")  # Create a file
os.mkdir("test") # Create a directory
os.makedirs("test1/test2") # Create a directory and all parent directories
os.environ # Get environment variables
os.environ.get("HOME") # Get a specific environment variable
os.getenv("HOME") # Get a specific environment variable
os.getuid() # Get the current user ID
os.getgid() # Get the current group ID
os.rmdir("test1") # Remove a directory
os.remove("test.txt") # Remove a file
os.removedirs("test1/test2") # Remove a directory and all parent directories
os.rename("test.txt", "test2.txt") # Rename a file

# os.path() module
os.path.join("/home/", "test.txt") # Join two paths
os.path.exists("/home/") # Check if a file or directory exists
os.path.isfile("/home/") # Check if a file exists
os.path.isdir("/home/") # Check if a directory exists
os.path.islink("/etc/") # Check if a link exists
os.path.basename("/etc/hosts") # Get the base name of a path
os.path.dirname("/etc/hosts") # Get the directory name of a path
os.path.join("/etc/", "hosts") # Join two paths

# os.walk() module
os.walk("/etc/default")
list(os.walk("/etc/default"))
# Output => [('/etc/default', ['grub.d'], ['locale', 'rsync', 'ssh', 'cron', 'dbus', 'apport', 'cacerts', 'networkd-dispatcher', 'docker', 'motd-news', 'pollinate', 'ufw', 'useradd', 'open-iscsi', 'nss', 'mdadm', 'irqbalance', 'console-setup', 'bsdmainutils', 'keyboard', 'cryptdisks']), ('/etc/default/grub.d', [], ['50-cloudimg-settings.cfg'])]


for dirname, dirpath, files in os.walk("/etc/default"):
    print(dirname)
    for file in files:
        print(os.path.join(dirname, file))
    print(dirpath)