import boto3
import botocore
import time
import logging
from botocore.exceptions import ClientError

AWS_SERVER_PUBLIC_KEY = "4f624375bfbd41f1b69a662ca323831a"
AWS_SERVER_SECRET_KEY = "f20b44c4aa8c23eaa6a146514dbffbd7"

client = boto3.client('s3',
                      endpoint_url="http://minio1:9000",
                      aws_access_key_id=AWS_SERVER_PUBLIC_KEY,
                      aws_secret_access_key=AWS_SERVER_SECRET_KEY)

while True:
    try:
        client.head_bucket(Bucket='test_bucket')
    except ClientError as e:
        for bucket in ['photos', 'filters']:
            try:
                client.head_bucket(Bucket=bucket)
            except ClientError:
                client.create_bucket(Bucket=bucket)
        break
    except botocore.vendored.requests.exceptions.ConnectionError as e:
        logging.error(e)
    else:
        break
    time.sleep(1)
