from pydantic import BaseModel

# 이미지 생성 요청 포맷
class DownloadImg(BaseModel):
    url: str
    phone_number: str