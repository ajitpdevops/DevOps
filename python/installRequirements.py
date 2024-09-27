import subprocess

def install_requirements():
    """Install packages listed in requirements.txt file."""
    try:
        subprocess.check_call(["pip", "install", "-r", "requirements.txt"])
    except subprocess.CalledProcessError as e:
        print(f"Error: {e}")
        raise SystemExit(e.returncode)
    


'''
def install_requirements():
    """_summary_: This function installs the requirements from the requirements.txt file
    """
    if not os.path.exists('requirements.txt'):
        print('requirements.txt file not found')
        return

    try:
        import pip
    except ImportError:
        print('pip not found. Installing pip...')
        os.system('python -m ensurepip --default-pip')
        print('pip installation completed.')
        import pip

    print('Installing requirements...')
    pip.main(['install', '-r', 'requirements.txt'])
    print('Requirements installation completed.')
'''