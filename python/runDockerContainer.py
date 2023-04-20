import importlib
import subprocess
import argparse
import ast


def run_docker_container(image_name, command=None, **kwargs):
    """_summary_: This function runs a docker image with the given arguments. If the container is already running, it will stop the container and prune all stopped containers. 
        For information on the all other arguments refere to the Docker SDK for Python Documentation - https://github.com/docker/docker-py


    Args:
        image_name (str): image name to run
        command (str): command to run
        ports (dict): Ports to bind inside the container.
            - The port number, as an integer. For example,
                  ``{'2222/tcp': 3333}`` will expose port 2222 inside the
        volumes (Dict or List): A dictionary mapping container volumes to host volumes. 
            E.g. ['/home/user1/:/mnt/vol2','/var/www:/mnt/vol1'] or {'/home/user1/': {'bind': '/mnt/vol2', 'mode': 'rw'}
        entrypoint (str or list): The entrypoint for the container.    
        environment (dict or list): Environment variables to set inside
                the container, as a dictionary or a list of strings in the
                format ``["SOMEVARIABLE=xxx"]``.            
        clean_host_volume_dirs (bool): If specified, the container will run with a clean volume directories from the Host.
        healthcheck (dict): Specify a test to perform to check that the
            container is healthy. The dict takes the following keys:
        volumes (dict or list): A dictionary to configure volumes mounted
                inside the container. The key is either the host path or a
                volume name, and the value is a dictionary with the keys:
                Dict Example: 
                    {'/home/user1/': {'bind': '/mnt/vol2', 'mode': 'rw'},
                     '/var/www': {'bind': '/mnt/vol1', 'mode': 'ro'}}

                List Example 
                    ['/home/user1/:/mnt/vol2','/var/www:/mnt/vol1']
            working_dir (str): Path to the working directory.                
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
        runningContainers = client.containers.list(
            filters={"status": "running", "ancestor": image_name})
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
        container_args_dict = ast.literal_eval()
    except (ValueError, SyntaxError):
        print('Error parsing container_args string.')
        return

    try:
        print('Running container with given docker args...')
        print(f"Image name: {image_name}")
        print(f"Container args: {container_args_dict}")
        container = client.containers.run(
            image_name, detach=True, **container_args_dict)
        if container:
            print(f"{container.short_id} : is running now")
        return
    except docker.errors.APIError as e:
        print('Error running container:', e)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run a Docker container.')
    parser.add_argument('--image_name', required=True,
                        help='The name of the Docker image to run.')
    parser.add_argument('--cleandir', action='store_true',
                        help='If specified, the container will run with a clean directory.')
    parser.add_argument('--container_args', required=True,
                        help='Docker container arguments in the form of dictionary')
    args = parser.parse_args()

    print('value of variable image_name is:', args.image_name)
    print('value of variable cleandir is:', args.cleandir)
    print('value of variable container_args is:', args.container_args)

    run_docker_container(args.image_name, args.cleandir, args.container_args)
