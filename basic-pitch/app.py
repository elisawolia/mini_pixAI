import base64
import io
from io import BufferedReader, BytesIO
import numpy as np
import soundfile as sf
import tensorflow as tf
from flask import Flask, jsonify, request
import tensorflow as tf
import librosa 
import numpy as np
import io
import pandas as pd
from multiprocessing import Pool
import uuid
import pretty_midi

from basic_pitch.inference import predict as pitch_predict
from basic_pitch import ICASSP_2022_MODEL_PATH
from utils import diez_map, diez_map_invert, needleman_wunsch




def read_audio(audio_bytes):
    data = base64.b64decode(audio_bytes.encode())
    print(len(data))
    print(f"{type(data)=}")
    audio, samplerate = sf.read(io.BytesIO(data))
    print(f"{samplerate=}")

    # audio = np.frombuffer(data, dtype=np.float64)
    # print(audio.shape)
    #print(audio)
    #print(len(audio))
    # audio = (audio * 2**15).astype(np.int16)
    # if audio.ndim == 2:
    #     audio = audio[:, 0]
    print(f"{audio.ndim=}")
    return audio, samplerate



if __name__ == "__main__":
    app = Flask(__name__)

    basic_pitch_model = tf.saved_model.load(str(ICASSP_2022_MODEL_PATH))
    SAMPLE_RATE = 22050

    @app.route("/predict", methods=["GET", "POST"])
    def predict():
        if request.method == "POST":
            data = request.json["audio_bytes"]
            gt_pitches = request.json["ground_truth"].split()
            #sample_rate = request.json["sample_rate"]
            for i, pitch in enumerate(gt_pitches):
                if pitch in diez_map:
                    gt_pitches[i] = diez_map[pitch]
                    
            y, samplerate = read_audio(data)
            y  = librosa.resample(y, orig_sr=samplerate, target_sr=SAMPLE_RATE)
            filename =  f"/tmp/{str(uuid.uuid4())}.wav" 
            sf.write(filename, y, SAMPLE_RATE)
            model_output, midi_data, note_events = pitch_predict(filename, basic_pitch_model)
            print(f"{len(note_events)=}")
            sorted_notes = sorted(note_events, key=lambda x: x[0])
            predicted_pitches = [(start_time, end_time, pretty_midi.note_number_to_name(pitch_num)[:-1]) 
                            for start_time, end_time, pitch_num, _, _ in sorted_notes]            
            unique_pitches = predicted_pitches

            predicted_pitch = [p[-1] for p in unique_pitches]
            for i, pitch in enumerate(predicted_pitch):
                if pitch in diez_map:
                    predicted_pitch[i] = diez_map[pitch]
            alignment = needleman_wunsch("".join(gt_pitches), "".join(predicted_pitch))

            print(diez_map_invert)
            for i, pitch in enumerate(predicted_pitch):
                if pitch in diez_map_invert:
                    predicted_pitch[i] = diez_map_invert[pitch]
                    
            print(alignment)
            print(f"{predicted_pitch=}")
            print(f'{len("".join(gt_pitches))=}')
            print(f'{len("".join(predicted_pitch))=}')
            
            errors = [] 
            left_time = 0
            for i, (left_index, right_index) in enumerate(alignment):
                if left_index is None and right_index is not None:
                    predicted_note = unique_pitches[right_index]
                    start_time, end_time, note_name = predicted_note
                    left_time = end_time
                    errors.append({"timestamp":  (start_time, end_time),
                            "note": note_name,
                                  "is_played": True})
                elif left_index is not None and right_index is None and left_index < len(gt_pitches):
                    if i + 1 >= len(alignment):
                        right_time = len(y) / SAMPLE_RATE
                    else:
                        right_time = len(y) / SAMPLE_RATE
                        for j in range(i + 1, len(alignment)):
                            next_note_index = alignment[j][1]
                            if next_note_index is not None:
                                right_time = unique_pitches[next_note_index][0]
                                break
                        # right_time =  unique_pitches[alignment[i + 1][1]][0]
                    print(f"{left_index=}, {len(gt_pitches)=}")
                    errors.append({"timestamp":  (left_time, right_time),
                            "note": gt_pitches[left_index],
                                   "is_played": False})
               
            return errors
    #app.run( host="0.0.0.0", port=9890, debug=True)
    from waitress import serve
    serve(app, host="0.0.0.0", port=9890)


