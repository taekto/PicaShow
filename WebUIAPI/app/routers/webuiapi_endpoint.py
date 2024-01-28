import sys, os

sys.path.append(os.path.dirname(os.path.abspath(__file__)))
import webuiapi
from io import BytesIO
import app.s3.s3Service as s3
import app.models.image_prompt as ImagePrompt
import app.main as main
import os
import logging
import openai
from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse
from urllib import request

router = APIRouter()

main.load_dotenv()
openai.organization = "org-cdrFOsSb24KAY7vi5zmNpYRv"
openai.api_key = os.getenv("OPENAI_API_KEY")

# create API client with custom host, port
WEB_UI_API = webuiapi.WebUIApi(host='127.0.0.1', port=7860)

logging.basicConfig(level=logging.DEBUG)

def get_value_from_key(theme_list, key):
    for dictionary in theme_list:
        if key in dictionary:
            return dictionary[key]
    return ""


# 배경화면 생성 API
@router.post("/image")
def sendAPI(requestBody: ImagePrompt.ImagePrompt):
    if len(requestBody.input_text) <= 0:
        raise HTTPException(status_code=422, detail="input length supposed to be greater than 0")
    theme_list = [
        {"realistic": "<lora:PrgXL_V1:1>"},
        {"fantasy": "<lora:FaeTastic2:1> Faetastic"},
        {"ghibli": "<lora:ghibli_last:1> ghibli"},
        {"animation": "<lora:sdxl-1.0_makoto-shinkai:1> by Makoto Shinkai"},
        {"van gogh": "<lora:v0ng44g, p14nt1ng:1> v0ng44g, p14nt1ng"},
        {"picasso": "<lora:p1c4ss0_003-step00028000:1> p1c4ss0"},
        {"europe": "<lora:edgRenaissanceXL:1> painted in renaissance style"}
    ]

    input = "'" + requestBody.input_text + "' 바탕으로 이미지 프롬프트를 영어로 만들어줘. 다른 말은 하지마"

    theme = get_value_from_key(theme_list, requestBody.user_theme)
    if theme == "":
        theme = "<lora:PrgXL_V1:1>"

    # main.logger.info("test")
    # api_key = os.getenv("OPENAI_API_KEY")

    response = openai.chat.completions.create(
        model="gpt-4-1106-preview",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": input}
        ],

    )

    # response = openai.ChatCompletion.create(
    #     model="gpt-4-1106-preview",  # 사용할 모델 (현재 최신 모델)
    #     messages=[
    #         {"role": "system", "content": "You are a helpful assistant."},
    #         {"role": "user", "content": input}
    #     ],
    # )



    print(response.choices[0].message.content)
    ai_image = WEB_UI_API.txt2img(prompt=theme + response.choices[0].message.content,
                                  negative_prompt="(worst quality, low quality, normal quality, lowres, low details, oversaturated, undersaturated, overexposed, underexposed, grayscale, bw, bad photo, bad photography, bad art:1.4), (watermark, signature, text font, username, error, logo, words, letters, digits, autograph, trademark, name:1.2), (blur, blurry, grainy), morbid, ugly, asymmetrical, mutated malformed, mutilated, poorly lit, bad shadow, draft, cropped, out of frame, cut off, censored, jpeg artifacts, out of focus, glitch, duplicate, (airbrushed, cartoon, anime, semi-realistic, cgi, render, blender, digital art, manga, amateur:1.3), (3D ,3D Game, 3D Game Scene, 3D Character:1.1), (bad hands, bad anatomy, bad body, bad face, bad teeth, bad arms, bad legs, deformities:1.3), NSFW",
                                  seed=-1,
                                  cfg_scale=7,
                                  override_settings={
                                      "sd_model_checkpoint": "sd_xl_base_1.0.safetensors [31e35c80fc]"
                                  },
                                  sampler_index="DDIM",
                                  # sampler_index="DPM++ SDM",
                                  width=512,
                                  height=1024
                                  )
    image = ai_image.images[0]
    # with Image.open("./00014-99100504.png") as image:
    image_bytes = BytesIO()
    image.save(image_bytes, format='png')

    image_url = s3.upload(image_bytes, s3.connection(), theme)

    return image_url


@router.post("/image/dalle")
def sendAPItoDallE3(requestBody: ImagePrompt.ImagePrompt):
    if len(requestBody.input_text) <= 0:
        raise HTTPException(status_code=422, detail="input length supposed to be greater than 0")

    client = openai.OpenAI()
    theme = requestBody.user_theme

    input = requestBody.input_text + "라는 주제를 " + theme + " 느낌으로 생성해줘"

    logging.info(input)

    try:
        response = client.images.generate(
            model="dall-e-3",
            prompt=input,
            size="1024x1792",
            quality="standard",
            n=1,
        )

        res = request.urlopen(response.data[0].url).read()
        image_bytes = BytesIO(res)
        image_url = s3.upload(image_bytes, s3.connection(), theme)

        return JSONResponse(content=image_url, status_code=200)
    except Exception as e:
        logging.warning(e)
        raise HTTPException(status_code=503, detail="Failed to generate image")