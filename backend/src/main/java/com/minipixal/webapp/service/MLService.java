package com.minipixal.webapp.service;

import com.minipixal.webapp.domain.AudioPitchRequest;
import com.minipixal.webapp.domain.Note;
import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.util.UtilObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Сервис по взаимодействию с ML-сервисами
 */
@Service
public class MLService {

    private final RestTemplate audioPitchRest;

    private final RestTemplate generateVideoRest;

    private final UtilObjectMapper objectMapper = new UtilObjectMapper();

    public MLService(@Qualifier("AudioRestTemplate") RestTemplate audioPitchRest,
                     @Qualifier("VideoRestTemplate") RestTemplate generateVideoRest) {
        this.audioPitchRest = audioPitchRest;
        this.generateVideoRest = generateVideoRest;
    }

    public ResponseEntity<byte[]> processAudio(MultipartFile audio, Song song, String animal, String color) throws IOException {
        System.out.println("AudioService::processAudio got song = "
                + song.getId() + ", animal = "
                + animal + ", color = " + color);
        AudioPitchRequest audioRequest = new AudioPitchRequest();
        audioRequest.setAudio_bytes(audio.getBytes());
        audioRequest.setGround_truth(song.getNotes());
        Note[] audioPitch =
                audioPitchRest.postForObject("/predict", audioRequest, Note[].class);
        String mistakes = objectMapper.mapToString(audioPitch)
                .replaceAll("false", "False")
                .replaceAll("true", "True");
        System.out.println("AudioService::processAudio got mistakes = " + mistakes);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.add("Content-Transfer-Encoding", "binary");
        httpHeaders.add("Content-Description", "File Transfer");
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("color", color);
        form.add("animal", animal);
        form.add("mistakes", mistakes);
        LinkedMultiValueMap<String, String> audioHeaderMap = new LinkedMultiValueMap<>();
        audioHeaderMap.add("Content-disposition", "form-data; name=audio; filename=" + audio.getOriginalFilename());
        audioHeaderMap.add("Content-type", "application/octet-stream");
        HttpEntity<byte[]> audioBytes = new HttpEntity<>(audio.getBytes(), audioHeaderMap);
        form.add("audio", audioBytes);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, httpHeaders);
        ResponseEntity<byte[]> generateResponse =
                generateVideoRest.postForEntity("/demo", requestEntity, byte[].class);
        byte[] video = generateResponse.getBody();
        System.out.println("AudioService::processAudio ended");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(video.length))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header("Content-Transfer-Encoding", "binary")
                .header("Content-Description", "File Transfer")
                .header(HttpHeaders.CACHE_CONTROL, "must-revalidate")
                .body(video);
    }

}
