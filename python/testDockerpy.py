# https://github.com/docker/docker-py/issues/2338

import docker 
import importlib
import subprocess

docker = importlib.import_module('docker')
client = docker.from_env()
# cont = client.containers.get('devopsandai/g3_db')
container=client.containers.list(filters={"status":"running"})

if (container):
    print("Container is running")
    print("Stopping all containers.")
else:
    print("No container is runnign atm.")    
        
print("Initiating a new container.")    
mssqlContainer=client.container.run('devopsandai/g3_db', detach=True, ports={'1433/tcp': 1433}, name='g3_db', volumes={'/opt/data': {'bind': '/var/opt/mssql', 'mode': 'rw'}})
mssqlContainer.logs()