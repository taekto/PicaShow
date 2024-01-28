from pydantic import BaseModel

# 이미지 생성 요청 포맷
class ImagePrompt(BaseModel):
    input_text: str
    user_theme: str