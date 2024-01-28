import os
import sys
from pathlib import Path
path = Path(os.getcwd())
sys.path.append(str(path))
sys.path.append(str(path.parent))


from dotenv import load_dotenv
import uvicorn
from fastapi import FastAPI
import routers.webuiapi_endpoint as webuiapi_endpoint
import routers.mongodb_endpoint as mongodb_endpoint
from fastapi.middleware.cors import CORSMiddleware

load_dotenv()
aws_access_key = os.getenv("LOCAL_AWS_S3_ACCESS_KEY")
aws_secret_key = os.getenv("LOCAL_AWS_S3_SECRET_KEY")
aws_bucket_name = os.getenv("LOCAL_AWS_S3_BUCKET")
aws_region = os.getenv("LOCAL_AWS_S3_REGION")

app = FastAPI()

# CORS 미들웨어 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 특정 도메인만 허용하려면 도메인 리스트를 명시합니다.
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


if __name__ == "__main__":
    app.include_router(mongodb_endpoint.router)
    app.include_router(webuiapi_endpoint.router)
    uvicorn.run(app, host="0.0.0.0", port=8000)
