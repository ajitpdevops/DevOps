import boto3

s3 = boto3.client('s3')
response = s3.list_buckets()
print("Avaialble buckets are: ")

for bucket in response['Buckets']:
    print(bucket['Name'])

bucket_name = input("Enter the bucket name: ")

# List objects in a selcted bucket

response = s3.list_objects(Bucket=bucket_name)
print("Objects in the bucket are: ")

if 'Contents' in response:
    for obj in response['Contents']:
        print(obj['Key'])
else:
    print("No objects found in the bucket")
