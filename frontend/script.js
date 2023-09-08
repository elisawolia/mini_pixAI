document.addEventListener("DOMContentLoaded", function () {
    const audioContainer = document.createElement('div');
    document.body.appendChild(audioContainer);

    let mediaRecorder;
    let audioChunks = [];

    // Функция, которая начинает запись аудио
    function startRecording() {
        navigator.mediaDevices
            .getUserMedia({ audio: true })
            .then(function (stream) {
                mediaRecorder = new MediaRecorder(stream);

                mediaRecorder.ondataavailable = function (event) {
                    if (event.data.size > 0) {
                        audioChunks.push(event.data);
                    }
                };

                mediaRecorder.onstop = function () {
                    const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });

                    // Отправляем аудиозапись на сервер
                    sendAudioToServer(audioBlob);
                };

                mediaRecorder.start();
            })
            .catch(function (error) {
                console.error('Error accessing microphone:', error);
            });
    }

    // Функция для отправки аудиозаписи на сервер
    function sendAudioToServer(audioBlob) {
        const formData = new FormData();
        formData.append('audio', audioBlob, 'audio.wav'); // 'audio' - это имя поля на сервере, 'audio.wav' - имя файла

        // Отправляем аудиозапись на сервер с использованием fetch
        fetch('http://95.163.229.127/upload-audio', {
            method: 'POST',
            body: formData
        })
        .then(function(response) {
            if (response.ok) {
                // Если аудиофайл успешно отправлен, обработайте ответ сервера, если необходимо
                return response.json();
            } else {
                console.error('Ошибка при отправке аудиозаписи на сервер.');
            }
        })
        .then(function(data) {
            // Обработка ответа от сервера, если необходимо
            console.log('Сервер ответил:', data);
        })
        .catch(function(err) {
            console.error('Ошибка при отправке запроса на сервер: ' + err);
        });
    }

    // Получаем элементы DOM
    const startGameButton = document.getElementById('startGameButton');

    // Обработчик клика по кнопке "готов играть"
    startGameButton.addEventListener('click', function () {
        startRecording();
        startGameButton.disabled = true;
    });
});

