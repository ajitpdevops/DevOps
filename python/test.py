import argparse
import docker

def run_con(image_name, name=None, protocol='tcp', ports, volumes, environment):
    # Define default parameters
    default_params = {
        'name': name,
        'protocol': 'tcp',
        'ports': None,
        'volumes': None,
        'environment': None 
    }

    print(f"image_name: {image_name}")
    print(f'ports: {ports}')
    print(f'volumes: {volumes}')
    print(f'environment: {environment}')
    
    # Merge default parameters with user-provided parameters
    params = default_params.copy()
    params['ports'] = ports
    params['volumes'] = volumes
    protocol = params.pop('protocol')
    
    for port in ports:
        ports_dict = {}
        
        for port in params['ports']:
            # print(f'port: {port}')
            print(params['ports'])
            port_split = port.split(':')
            print(f'port_split: {port_split}')
            host_port = port_split[0]
            print(f'host_port: {host_port}')
            container_port = port_split[1]
            print(f'container_port: {container_port}')
            ports_dict[f"{container_port}/{protocol}"] = host_port
            print(f'ports_dict: {ports_dict}')
        params['ports'] = ports_dict
    
    print(params['ports'])
    

    # # Convert user-friendly parameter format to Docker SDK format
    # if 'ports' in params:
    #     ports_dict = {}
    #     for port in params['ports']:
    #         ports = port.split(':')
    #         host_ports = ports[0]
    #         container_ports = ports[1]
    #         ports_dict[f"{container_ports}/{protocol}"] = host_ports

    #     params['ports'] = ports_dict

    # if 'volumes' in params:
    #     volumes_dict = {}

    #     for volume in params['volumes']:
    #         parts = volume.split(':')
    #         host_path = parts[0]
    #         container_path = parts[1]
    #         volumes_dict[host_path] = {'bind': container_path, 'mode': 'rw'}

    #     params['volumes'] = volumes_dict
        
    # if 'environment' in params:
    #     environment_dict = {}
    #     for env in params['environment']:
    #         parts = env.split('=')
    #         var_name = parts[0]
    #         var_value = parts[1]
    #         environment_dict[var_name] = var_value
            
    #     params['environment'] = environment_dict                 

    # Run container with merged parameters
    # client = docker.from_env()
    # container = client.containers.run(image_name, detach=True, **params)
    # if container:
    #     print(f"{container.short_id} is running now")

    # for param in params:
    #     print(f'{param} : {params[param]}')

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
    
    run_con(args.image_name, name=args.name, protocol=args.protocol, ports=args.port, volumes=args.volume, environment=args.environment)

    
