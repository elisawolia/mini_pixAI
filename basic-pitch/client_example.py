import json
import time
import requests
import base64
import soundfile as sf
import numpy as np
import librosa


gt = { 'krokodil': ['A', 'A#', 'A', 'A', 'A#', 'A',  'A', 'A#', 'A', 'D', 'E', 'F','D',
                  'A', 'A#', 'A',  'E', 'F', 'G', 'E', 'A', 'A#', 'A',
                  'E', 'F', 'G', 'A', 'A#', 'A', 'D', 'D#', 'D', 'A', 'A#',
                  'C','A', 'D', 'D#', 'D', 'G', 'A', 'A#', 'D', 'C', 'A#',
                  'D', 'A', 'F', 'E', 'G', 'F', 'D'],
'polka': ['D', 'G', 'A#', 'D', 'D', 'D#','D', 'B', 'C', 'D', 'D', 'D', 'D', 'D#', 'D', 
          'A', 'B', 'D', 'D', 'G','G','G','G','G','F','D#','D','C','D#','D','D','D','D','D','C','A#',
'A','G','D', 'G', 'A#', 'D', 'D', 'D#','D', 'B', 'C', 'D', 'D', 'D', 'D', 
'D#', 'D', 'A','B', 'D', 'D', 'G','G','G','G','G','F','D#','D','C','D#','D','C#','D','C#','D','D#','E','F#','G']
}





if __name__ == '__main__':
    audio_path = "/root/examples/kid_polka.wav"
    audio, sr = sf.read(audio_path) #kid_polka.wav #/krokodil_right.mpeg
    if audio.ndim > 1:
        audio = np.array(audio[:, 0])
    # audio = librosa.resample(audio, orig_sr=sr, target_sr=22050)
    gt_pitches = gt['polka']
    start = time.time()
    resp = requests.post("http://95.163.229.127:9890/predict", 
                         json = {"audio_bytes": base64.b64encode(audio).decode(),
                                "ground_truth": " ".join(gt_pitches), 
                                "sample_rate": sr})
    data = json.loads(resp.content)
    
    print(f"Needed time = {time.time() - start}")
    print(data)

