import sys, os

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from pymongo import MongoClient
import app.models.download_img as DownloadImg

import app.main as main

main.load_dotenv()
from fastapi import APIRouter, Query, Path, HTTPException, status
import math

router = APIRouter()

mongodb_URI = os.getenv("MONGODB_URI")
client = MongoClient(mongodb_URI)


# 배경화면 다운로드 시 유저 등록 API
@router.post("/download")
def registUser(download_img: DownloadImg.DownloadImg):
    if len(download_img.url) <= 0:
        raise HTTPException(status_code=422, detail="img length supposed to be greater than 0")
    if len(download_img.phone_number) <= 0:
        raise HTTPException(status_code=422, detail="user number length supposed to be greater than 0")

    collection = client.final.wallpaper

    if collection.count_documents({"url": download_img.url}) <= 0:
        raise HTTPException(status_code=404, detail="img not found")

    img_url = download_img.url
    user_phone_number = download_img.phone_number
    try:
        collection.update_one({"url": img_url}, {"$addToSet": {"phone_number": user_phone_number}})
    except Exception as e:
        print(e)
        return HTTPException(status_code=500, detail="Internal server error")
    return status.HTTP_200_OK


# 배경화면 조회 API
@router.get("/list")
def getList(page: int = Query(default=1)):
    # mongodb_URI = "localhost:27017"
    collection = client.final.wallpaper
    if page <= 0:
        raise HTTPException(status_code=404, detail="page not found")

    page_number = page
    limit = 15

    image_url_list = (collection.aggregate([
        {"$addFields": {"arrayLength": {"$size": "$phone_number"}}},
        {"$sort": {"arrayLength": -1, "_id": -1}},
        {"$skip": (page_number - 1) * limit},
        {"$limit": limit},
        {"$project": {"arrayLength": 0}}
    ]))
    # .skip((page_number - 1) * limit)
    # .limit(limit))
    # .find({}, {'_id': False})

    total_pages = math.ceil(collection.count_documents({}) / limit)

    last_page_num = total_pages

    # sorted_list = sorted(image_url_list, key=lambda x: len(x['phone_number']), reverse=True)

    list = []

    # for i in sorted_list:
    #     data = {'url': i['url']}
    #     list.append(data)

    for i in image_url_list:
        data = {'url': i['url']}
        list.append(data)

    return {'list': list,
            'limit': limit,
            'page_number': page_number,
            'last_page_num': last_page_num}


@router.get("/list/{theme}")
def getThemeList(theme: str = Path(), page: int = Query(default=1)):
    if page <= 0:
        raise HTTPException(status_code=404, detail="page not found")

    collection = client.final.wallpaper

    page_number = page
    limit = 15

    image_url_list = (collection.aggregate([
        {"$addFields": {"arrayLength": {"$size": "$phone_number"}}},
        {"$match": {"theme": theme}},
        {"$sort": {"arrayLength": -1, "_id": -1}},
        {"$skip": (page_number - 1) * limit},
        {"$limit": limit},
        {"$project": {"arrayLength": 0}}
    ]))

    total_pages = math.ceil(collection.count_documents({}) / limit)

    last_page_num = math.ceil(total_pages / limit)
    # sorted_list = sorted(image_url_list, key=lambda x: len(x['phone_number']), reverse=True)

    list = []

    for i in image_url_list:
        data = {'url': i['url']}
        list.append(data)
    return {'list': list,
            'limit': limit,
            'page_number': page_number,
            'last_page_num': last_page_num}
