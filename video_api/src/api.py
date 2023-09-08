import os
import io
from typing import List, Union
from urllib.parse import unquote

import librosa
import soundfile as sf
from fastapi import status, APIRouter, File
from starlette.responses import StreamingResponse

import config
from src.inference import generate_images
from src.utils import add_audio, create_video, generate_images_demo

router = APIRouter()


@router.post("/generate", description='Метод для генерации изображений.')
async def synthesize(
        audio: bytes = File(...),
        sample_rate: int = 16000,
        animal: str = 'dog',
        color: str = 'black',
        mistakes: Union[List, None] = None,
) -> StreamingResponse:
    parameters = {
        'animal': animal,
        'color': color
    }

    audio = librosa.load(io.BytesIO(audio), sr=sample_rate)[0]
    duration = len(audio) / sample_rate
    audio_path = os.path.join(config.media_path, 'audio.wav')

    sf.write(audio_path, audio.transpose(), sample_rate, 'PCM_16')
    images = generate_images(parameters)

    mistakes = eval(unquote(mistakes))
    if mistakes:
        mistakes = [mistake for mistake in mistakes if mistake]

    video_path = create_video(images, mistakes=mistakes, audio_duration=duration, media_path=config.media_path)
    final_file = add_audio(config.media_path, audio_path, video_path)

    response = StreamingResponse(iter(open(final_file, "rb")), status_code=status.HTTP_200_OK)
    response.headers["Content-Disposition"] = "attachment; filename={}".format('video.mp4')

    return response
