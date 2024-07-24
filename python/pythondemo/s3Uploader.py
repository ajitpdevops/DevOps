import boto3
import os
from boto3.s3.transfer import TransferConfig

class S3Uploader:
    def __init__(self):
        self.s3 = boto3.client('s3')
        self.config = TransferConfig(multipart_threshold=5*1024**3, max_concurrency=10, multipart_chunksize=5*1024**3, use_threads=True)

    def list_buckets(self):
        response = self.s3.list_buckets()
        print("Available buckets are:")
        for bucket in response['Buckets']:
            print(bucket['Name'])

    def upload_folder(self, local_path, bucket_name, prefix=""):
        if not os.path.exists(local_path):
            raise ValueError(f"{local_path} does not exist locally.")
            
        for root, dirs, files in os.walk(local_path):
            for file in files:
                full_path = os.path.join(root, file)
                relative_path = os.path.relpath(full_path, local_path)
                s3_path = os.path.join(prefix, relative_path)
                response = self.s3.list_objects(Bucket=bucket_name, Prefix=s3_path)
                if 'Contents' in response:
                    raise ValueError(f"{s3_path} already exists in {self.bucket_name}.")
                
                try:
                    self.s3.upload_file(full_path, bucket_name, s3_path, Config=self.config)
                    print(f"{full_path} has been successfully uploaded to {bucket_name}.")
                except Exception as e:
                    raise ValueError(f"Failed to upload {full_path} to {bucket_name}. Error: {str(e)}")

    def upload_file(self, local_path, bucket_name):
        file_name = os.path.basename(local_path)

        object_exists = False
        response = self.s3.list_objects(Bucket=bucket_name)

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
                return

        print(f"Uploading {file_name} to {bucket_name}...")
        self.s3.upload_file(local_path, bucket_name, file_name, Config=self.config)
        print(f"{file_name} has been uploaded to {bucket_name}.")

if __name__ == "__main__":
    uploader = S3Uploader()
    uploader.list_buckets()
    bucket_name = input("Enter the bucket name: ")
    user_response = input("Do you want to upload a folder(s)? (y/n): ")
    if user_response.lower() == 'y':
        property_parent_path = './dirsToUpload'
        # user_list = input("please enter the list of properties to upload, e.g. test,test1,test2: ")
        # property_list = user_list.split(',')
        property_list = ['property1', 'property2', 'property3', 'property4']
        print(f'List of folders specified: {property_list}')
        for property in property_list:
            folder_path = os.path.join(property_parent_path, property)
            print(f"Uploading {property} to {bucket_name}...")
            # I want to throw an expection if folder is not found or there is an error while uploading
            uploader.upload_folder(folder_path, bucket_name, property)
            print(f"{property} has been successfully uploaded to {bucket_name}.")
    else:
        local_path = input("Enter the local file path, e.g. /home/apatil20/repos/python/test.txt : ")
        uploader.upload_file(local_path, bucket_name)