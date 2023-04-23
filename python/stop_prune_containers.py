import importlib

docker = importlib.import_module('docker')
client = docker.from_env()


# Stop running containers
runningContainers = client.containers.list(filters={"status": "running", "ancestor": 'devopsandai/g3_db'})

if (runningContainers):
    print('Container is already running.')
    print('Stopping all containers.')
    for container in runningContainers:
        container.stop()
        
# Prune containers 
client.containers.prune()        
