import boto3
from botocore.exceptions import NoCredentialsError
import os

from pymongo import MongoClient

import app.main as main
from io import BytesIO
main.load_dotenv()

def connection():

    try:
        s3 = boto3.client(service_name="s3"
                          , aws_access_key_id=main.aws_access_key
                          , aws_secret_access_key=main.aws_secret_key
                          , region_name=main.aws_region)
    except Exception as e:
        print(e)
    else:
        return s3

def upload(image_bytes, s3, theme):
    image_buffer = BytesIO(image_bytes.getvalue())
    image_buffer.seek(0)

    image_hash = hash(image_bytes.getvalue())
    s3_object_key = f'images/{image_hash}.png'

    try:
        s3.upload_fileobj(image_buffer, main.aws_bucket_name, s3_object_key, ExtraArgs={'ContentType': 'image/png'})
        mongodb_URI = os.getenv("MONGODB_URI")
        client = MongoClient(mongodb_URI)
        collection = client.final.wallpaper
        collection.insert_one(
            {
                "url": f"https://{main.aws_bucket_name}.s3.{main.aws_region}.amazonaws.com/{s3_object_key}",
                "phone_number": [],
                "theme": f"{theme}"
            }
        )

    except NoCredentialsError:
        print("AWS 계정 정보 없음")
    except Exception as e:
        print("오류발생", e)

    return f"https://{main.aws_bucket_name}.s3.{main.aws_region}.amazonaws.com/{s3_object_key}"