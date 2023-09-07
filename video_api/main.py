import datetime
import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import config
from src.api import router as main_router

print('-' * 10 + 'Service started. {}'.format(datetime.datetime.now().strftime("%Y-%m-%d %H:%M")) + '-' * 10)

app = FastAPI(title='Image Synthesis API',
              description='Image Synthesis API for project Mini PixAI',
              version='0.1')

app.include_router(
    main_router,
    tags=["image_synthesis"],
)

origins = ["*"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

if __name__ == '__main__':
    uvicorn.run(app, host=config.host, port=config.port)


