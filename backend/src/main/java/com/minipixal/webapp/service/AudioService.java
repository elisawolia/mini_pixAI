package com.minipixal.webapp.service;

import com.minipixal.webapp.domain.AudioPitchRequest;
import com.minipixal.webapp.domain.AudioPitchResponse;
import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.util.UtilObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AudioService {

    private final RestTemplate audioPitchRest;

    private final RestTemplate generateVideoRest;

    private final UtilObjectMapper objectMapper = new UtilObjectMapper();

    public AudioService(@Qualifier("AudioRestTemplate") RestTemplate audioPitchRest,
                        @Qualifier("VideoRestTemplate") RestTemplate generateVideoRest) {
        this.audioPitchRest = audioPitchRest;
        this.generateVideoRest = generateVideoRest;
    }

    public ResponseEntity<byte[]> processAudio(MultipartFile audio, Song song, String animal, String color) throws IOException {
        System.out.println("AudioService::processAudio");
        AudioPitchRequest audioRequest = new AudioPitchRequest();
        audioRequest.setAudio_bytes(Base64.getEncoder().encodeToString(audio.getBytes()));
        audioRequest.setGround_truth(song.getNotes());
//        ResponseEntity<String> audioResponse =
//                audioPitchRest.postForEntity("/predict", audioRequest, String.class);
//        String audioPitch = audioResponse.getBody();
//        System.out.println("Audio service resp = " + audioPitch);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.add("Content-Transfer-Encoding", "binary");
        httpHeaders.add("Content-Description", "File Transfer");
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("color", color);
        form.add("animal", animal);
  //      form.add("mistakes", audioPitch);
        LinkedMultiValueMap<String, String> audioHeaderMap = new LinkedMultiValueMap<>();
        audioHeaderMap.add("Content-disposition", "form-data; name=audio; filename=" + audio.getOriginalFilename());
        audioHeaderMap.add("Content-type", "application/octet-stream");
        HttpEntity<byte[]> audioBytes = new HttpEntity<>(audio.getBytes(), audioHeaderMap);
        form.add("audio", audioBytes);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, httpHeaders);
        ResponseEntity<byte[]> generateResponse =
                generateVideoRest.postForEntity("/demo", requestEntity, byte[].class);
        byte[] video = generateResponse.getBody();
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
