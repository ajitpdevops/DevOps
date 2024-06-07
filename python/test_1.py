import argparse
import docker

def run_docker_container(image_name, name=None, protocol='tcp', ports=[], volumes=[], clean_host_volume_dirs=False, environment=[]):
    # Define default parameters
    default_params = {
        'name': name,
        'protocol': 'tcp',
        'ports': {},
        'volumes': {},
        'environment': []
    }
    
    # Merge default parameters with user-provided parameters
    params = default_params.copy()
    params['ports'] = {port.split(':')[1]: port.split(':')[0] for port in ports}
    params['volumes'] = {volume.split(':')[0]: {'bind': volume.split(':')[1], 'mode': 'rw'} for volume in volumes}
    protocol = params.pop('protocol')
    params['environment'] = environment
    
    print('ports: ', params['ports'])
    print('volumes: ', params['volumes'])
    print(image_name)
    # # Create Docker client
    # client = docker.from_env()

    # # Run container
    # container = client.containers.run(image_name, detach=True, remove=True, **params)

    # # Print container logs
    # for line in container.logs(stream=True):
    #     print(line.strip())

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run a Docker container with specified parameters')
    parser.add_argument('image_name', type=str, help='Docker image name')
    parser.add_argument('--name', type=str, help='Container name')
    parser.add_argument('--protocol', type=str, default='tcp', help='Port protocol (tcp or udp))')
    parser.add_argument('--port', type=str, nargs='+', help='Port mappings in the format "host_port:container_port"')
    parser.add_argument('--volume', type=str, nargs='+', help='Volume mappings in the format "host_path:container_path"')
    parser.add_argument('--environment', type=str, nargs='+', help='Environment variables in the format "var_name=var_value"')
    parser.add_argument('--clean_host_volume_dirs', type=bool, default=False, help='Clean host volume directories')
    args = parser.parse_args()

    # Convert unknown arguments to a dictionary and pass to run_container function
    # unknown_args_dict = {k: v for k, v in [arg.split('=') for arg in unknown_args]}
    run_docker_container(args.image_name, name=args.name, protocol=args.protocol, ports=args.port, volumes=args.volume, environment=args.environment)
