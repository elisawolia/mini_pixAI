import os

import cv2
import moviepy.editor as mpe
import numpy as np


def put_mistake(image, note, flag):
    image = image.copy()
    text = f'Ты ошибся, тут нота - {note}' if flag else f'Ты забыл про ноту - {note}'
    coordinates = (35, 45)
    font = cv2.FONT_HERSHEY_COMPLEX
    fontScale = 1
    thickness = 2

    text_size, _ = cv2.getTextSize(text, font, fontScale, thickness)
    text_w, text_h = text_size
    cv2.rectangle(image, (coordinates[0], coordinates[1] - text_h), (coordinates[0]+text_w, coordinates[1]), (255, 255, 255), -1)
    image = cv2.putText(image, text, coordinates, font, fontScale, (0, 0, 0), thickness, cv2.LINE_AA)

    return image


def parse_mistakes(mistakes, mistake_duration):
    final_mistakes = {}
    for mistake in mistakes:
        mistake_timestamp = int(10 * mistake['timestamp'][0])
        final_mistakes = final_mistakes | {
                                            i: (mistake['note'], mistake['is_played'])
                                            for i in range(mistake_timestamp, mistake_timestamp + mistake_duration)
                                        }

    return final_mistakes


def create_images_list(images, mistakes, base_duration, audio_duration, fps=10):
    if not mistakes:
        images_list = list()

        for image in images[:-1]:
            if image['type']:
                images_list += [_process_images(image['image'])] * base_duration

    else:
        images_list = list()
        for i in range(int(audio_duration * fps)):
            if i in mistakes.keys():
                images_list.append(put_mistake(_process_images(images[-1]['image']), mistakes[i][0], mistakes[i][1]))
            else:
                images_list.append(_process_images(images[i//base_duration]['image']))

    return images_list


def create_video(images, mistakes, audio_duration, media_path, false_duration=10, fps=10):
    image_duration = int(audio_duration * 10 // (len(images) - 1))
    height, width, _ = _process_images(images[0]['image']).shape

    mistakes = parse_mistakes(mistakes, false_duration)

    frames = create_images_list(images, mistakes, image_duration, audio_duration)

    video_path = os.path.join(media_path, 'output.avi')

    out = cv2.VideoWriter(video_path, cv2.VideoWriter_fourcc(*'DIVX'), fps, (width, height))
    [out.write(f) for f in frames]
    out.release()

    return video_path


def add_audio(media_path, audio_path, video_path):
    videoclip = mpe.VideoFileClip(video_path)
    audioclip = mpe.AudioFileClip(audio_path)

    final_audio = mpe.CompositeAudioClip([audioclip])
    final_clip = videoclip.set_audio(final_audio)

    final_path = os.path.join(media_path, "final_output.mp4")
    final_clip.write_videofile(
                             final_path,
                             codec='libx264',
                             audio_codec='aac',
                             temp_audiofile='temp-audio.m4a',
                             remove_temp=True
    )

    return final_path


def _process_images(image):
    image = np.array(image)

    return image[:, :, ::-1]


