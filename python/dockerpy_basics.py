import importlib

docker = importlib.import_module('docker')
client = docker.from_env()

'''
# To pull an image from docker hub
image = client.images.pull('devopsandai/g3_db')
print(image)    

# To run a container from an image
client.containers.run('devopsandai/g3_db', detach=True)

# Stop running containers
runningContainers = client.containers.list(filters={"status": "running", "ancestor": 'devopsandai/g3_db'})

if (runningContainers):
    print('Container is already running.')
    print('Stopping all containers.')
    for container in runningContainers:
        container.stop()
        
# Prune containers 
client.containers.prune()        
'''

# To run a container from an image
# client.containers.run('devopsandai/g3_db', detach=True, name='g3_db', ports={'1433/tcp': 1455}, volumes=['/opt/data:/var/opt/mssql/data', '/opt/sandbox:/var/opt/mssql/sandbox'])

client.containers.run('devopsandai/g3_db', detach=True, name='g3_db', ports={'1433/tcp': '1434', '1500/tcp': '1500'}, volumes={'/opt/data': {'bind': '/var/opt/mssql/data', 'mode': 'rw'}, '/opt/sandbox': {'bind': '/var/opt/mssql/sandbox', 'mode': 'rw'}}, environment={'AWS_PROFILE': 'Default', 'AWS_REGION': 'use-east-2'})
