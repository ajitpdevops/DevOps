import importlib
import docker


def func_args(*args, **kwargs):
    # Define default parameters
    default_params = {
        'name': None,
        'protocol': 'tcp',
        'ports': [],
        'volumes': [],
    }

    # Merge default parameters with user-provided parameters
    params = default_params.copy()
    params.update(kwargs)

    protocol = params.pop('protocol')
    
    # Convert user-friendly parameter format to Docker SDK format
    if 'ports' in params:
        ports_dict = {}
        for port in params['ports']:
            ports = port.split(':')
            container_ports = ports[0]
            host_ports = ports[1]
            ports_dict[f"{container_ports}/tcp"] = host_ports
            
        params['ports'] = ports_dict

    if 'volumes' in params:
        volumes_dict = {}
        
        for volume in params['volumes']:
            parts = volume.split(':')
            host_path = parts[0]
            container_path = parts[1]
            volumes_dict[host_path] = {'bind': container_path, 'mode': 'rw'}

        params['volumes'] = volumes_dict

    # Run container with merged parameters
    client = docker.from_env()
    container = client.containers.run(args[0], detach=True, **params)
    if container:
        print(f"{container.short_id} is running now")
    
    for param in params:
        print(f'{param} : {params[param]}')


# Example usage
func_args('devopsandai/g3_db', name='g3_db', ports=['1433:1434','1500:1500'], volumes=['/opt/data:/var/opt/mssql/data', '/opt/sandbox:/var/opt/mssql/sandbox'])