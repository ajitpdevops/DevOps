import boto3

class S3Bucket:
    def __init__(self):
        self.s3 = boto3.client('s3')
        
    def list_buckets(self):
        response = self.s3.list_buckets()
        print("Available buckets are: ")
        for bucket in response['Buckets']:
            print(bucket['Name'])
    
    def list_objects(self, bucket_name):
        response = self.s3.list_objects(Bucket=bucket_name)
        print("Objects in the bucket are: ")
        if 'Contents' in response:
            for obj in response['Contents']:
                print(obj['Key'])
        else:
            print("No objects found in the bucket")

if __name__ == "__main__":
    s3Bucket = S3Bucket()
    s3Bucket.list_buckets()
    bucket_name = input("Enter the bucket name: ")
    s3Bucket.list_objects(bucket_name)