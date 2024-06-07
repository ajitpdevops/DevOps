import subprocess
import installRequirements as ir
import runDockerContainer as rdc

# print("Installing requirements...")
# try:
#     ir.install_requirements()
#     print("Requirements installation completed.")
# except subprocess.CalledProcessError as e:
#     print(f"Error: {e}")
#     raise SystemExit(e.returncode)


print("calling run docker container function")
try:
    rdc.run_docker_image()
    print("Docker container run completed.")
except subprocess.CalledProcessError as e:
    print(f"Error: {e}")
    raise SystemExit(e.returncode)



