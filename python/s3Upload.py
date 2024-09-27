import boto3
import os
from boto3.s3.transfer import TransferConfig

property_parent_path = '/R3/Data/Properties/'
# property_list = ['21041','21061','21081','21101','21121','21141','21161','21181','21002','21022','21042','21062','21082','21102','21122','21142','21162','21182','21003','21023','21043','21063','21083','21103','21123','21143','21163','21183','21004','21024','21044','21064','21084','21104','21124','21144','21164','21184','21005','21025','21045','21065','21085','21105','21125','21145','21165','21185']
property_list = property_list = ['21085','21105','21125','21145','21165','21185']


# Set up a connection to the S3 service
s3 = boto3.client('s3')

# List the buckets in the account
response = s3.list_buckets()
print("Avaialble buckets are: ")

# Lists the buckes with sequantial numbers so I can take the user input as the number
for bucket in response['Buckets']:
    print(bucket['Name'])

# Get the bucket name and the local file path from the user
bucket_name = input("Enter the bucket name: ")

# Set the desired multipart threshold value (5GB)
GB = 1024 ** 3
config = TransferConfig(multipart_threshold=5*GB, max_concurrency=10, multipart_chunksize=5*GB, use_threads=True)

# Ask if user wants to upload a folder
user_response = input("Do you want to upload a folder? (y/n): ")

# Function upload files from a folder recuresively to s3 bucket
def upload_dir(path, bucket, prefix=""):
    for root, dirs, files in os.walk(path):
        for file in files:
            full_path = os.path.join(root, file)
            relative_path = os.path.relpath(full_path, path)
            s3_path = os.path.join(prefix, relative_path)
            s3.upload_file(full_path, bucket, s3_path, Config=config)


if user_response == 'y':
    for property in property_list:
        #folder_name = os.path.basename(folder_path)
        folder_path = property_parent_path + property
        print(f"Uploading {property} to {bucket_name}...")
        upload_dir(folder_path, bucket_name, property)
        print(f"{property} has been successfully uploaded to {bucket_name}.")

    exit()

# Get file upload info from the user
local_file_path = input("Enter the local file path, e.g. /home/apatil20/repos/python/test.txt : ")
file_name = os.path.basename(local_file_path)


# Check if the file already exists in the bucket
object_exists = False
response = s3.list_objects(Bucket=bucket_name)

if 'Contents' in response:
    for obj in response['Contents']:
        if obj['Key'] == file_name:
            object_exists = True
            print(f"{file_name} already exists in {bucket_name}.")
            break

if object_exists:
    overwrite = input("Do you want to overwrite it? (y/n): ")
    if overwrite == 'n':
        print(f"{file_name} has not been uploaded to {bucket_name}.")
        exit()

print(f"Uploading {file_name} to {bucket_name}...")

s3.upload_file(local_file_path, bucket_name, file_name, Config=config)

print(f"{file_name} has been uploaded to {bucket_name}.")
