import importlib
import subprocess
import argparse
import ast


def run_docker_container(image_name, cleandir, container_args):
    """_summary_: This function runs a docker image with the given arguments

    Args:
        image_name (str): image name to run
        cleandir (bool): If you want to clean the /opt/data directory before running the container
        docker_args (dict): arguments to pass to the docker container E.g. {'ports': {'1433/tcp': 1433}, 'name': 'g3_db', 'volumes': {'/opt/data': {'bind': '/var/opt/mssql', 'mode': 'rw'}}}
    """
    
    # Check if docker service is running
    try:
        print('Checking docker service...')
        subprocess.check_call(['systemctl', 'is-active', '--quiet', 'docker'])
        print('Docker service is running.')
    except subprocess.CalledProcessError as e:
        print('Docker service is not running. Starting docker service...')
        try:
            subprocess.check_call(['systemctl', 'start', 'docker'])
            print('Docker service started successfully.')
        except subprocess.CalledProcessError as e:
            print('Error starting docker service:', e)
            return
    
    # checking if docker module is installed
    try:
        docker = importlib.import_module('docker')
    except ImportError:
        print('docker module not found, installing...')
        try:
            subprocess.check_call(['pip', 'install', 'docker'])
        except subprocess.CalledProcessError as e:
            print('Error installing docker:', e)
            return
        print('docker module installed successfully.')

    client = docker.from_env()
    
    # Verify if container is already running
    try:
        print('Checking if container is already running...')
        runningContainers = client.containers.list(filters={"status":"running", "ancestor": image_name})
        if (runningContainers):
            print('Container is already running.')
            print('Stopping all containers.')
            for container in runningContainers:
                container.stop()
            
            # prune docker container
            client.containers.prune()
            print('All containers stopped and pruned all stopped containers.')
            
    except docker.errors.NotFound:
        print('Container is not running.')
    except docker.errors.APIError as e:
        print('Error:', e)
        return
    
    # if a arg clean=True then check if /opt/data dir exists and clean it
    if cleandir:
            try:
                print('Checking if /opt/data directory exists...')
                subprocess.check_call(['test', '-d', '/opt/data'])
                print('/opt/data directory cleaned successfully.')
            except subprocess.CalledProcessError as e:
                print('Error:', e)
                return
            
    # Pulling the docker image
    try:
        print('Pulling image...')
        client.images.pull(image_name)
        print('Image pulled successfully.')
    except docker.errors.APIError as e:
        print('Error pulling image:', e)
        return
    
    # Convert container_args string to dictionary
    try:
        container_args_dict = ast.literal_eval(container_args)
    except (ValueError, SyntaxError):
        print('Error parsing container_args string.')
        return
    
    try:
        print('Running container with given docker args...')
        print(f"Image name: {image_name}")
        print(f"Container args: {container_args_dict}")
        container = client.containers.run(image_name, detach=True, **container_args_dict)
        if container:
            print(f"{container.short_id} : is running now")
        return
    except docker.errors.APIError as e:
        print('Error running container:', e)
    


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run a Docker container.')
    parser.add_argument('--image_name', required=True, help='The name of the Docker image to run.')
    parser.add_argument('--cleandir', action='store_true', help='If specified, the container will run with a clean directory.')
    parser.add_argument('--container_args', required=True, help='Docker container arguments in the form of dictionary')
    args = parser.parse_args()
    
    print ('value of variable image_name is:',args.image_name)
    print ('value of variable cleandir is:',args.cleandir)
    print ('value of variable container_args is:', args.container_args)
        
    run_docker_container(args.image_name, args.cleandir, args.container_args)
