import importlib
import docker


def func_args(*args, **kwargs):
    # Define default parameters
    default_params = {
        'name': None,
        'protocol': 'tcp',
        'ports': {'80/tcp': 80},
        'volumes': {},
    }

    # Merge default parameters with user-provided parameters
    params = default_params.copy()
    params.update(kwargs)
    protocol = params.pop('protocol')
    
    # Convert user-friendly parameter format to Docker SDK format
    
    if 'ports' in params:
        params['ports'] = {f"{k}/{protocol}": v for k,
                           v in params['ports'].items()}

    if 'volumes' in params:
        volumes_dict = {}
        
        for volume in params['volumes']:
            parts = volume.split(':')
            host_path = parts[0]
            container_path = parts[1]
            volumes_dict[host_path] = {'bind': container_path, 'mode': 'rw'}
            
        params['volumes'] = volumes_dict
    
    print(f'{params}')
    
    # # Run container with merged parameters
    # client = docker.from_env()
    
    # container = client.containers.run(args[0], detach=True, **params)
    # if container:
    #     print(f"{container.short_id} is running now")



func_args('devopsandai/g3_db', name='g3_db', ports={'1433': 1455}, volumes=['/opt/data:/var/opt/mssql/data', '/opt/sandbox:/var/opt/mssql/sandbox'])



