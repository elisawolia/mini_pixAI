package com.minipixal.webapp.controller;

import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.domain.SongResponse;
import com.minipixal.webapp.service.MLService;
import com.minipixal.webapp.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
public class MainController {

    public static final String GET_SONGS = "/songs_list";

    public static final String CHECK = "/check";

    private final MLService service;

    private final SongService songService;

    @Autowired
    public MainController(MLService MLService, SongService songService) {
        this.service = MLService;
        this.songService = songService;
    }

    /**
     * Контроллер для получения списка песен
     */
    @GetMapping(MainController.GET_SONGS)
    @CrossOrigin(origins = "http://localhost:3000")
    public SongResponse getSongs() {
        System.out.println("MainController::getSongs");
        return songService.readSongs();
    }

    /**
     * Контроллер для проверки качества сыгранной песни
     * @param songId идентификатор песни
     * @param animal выбранное животное
     * @param color цвет животного
     * @param file аудио файл
     * @return сгенерированное видео
     */
    @PostMapping(value = MainController.CHECK, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<byte[]> check(@RequestParam(value="songId") Integer songId,
                                        @RequestParam(value="animal") String animal,
                                        @RequestParam(value="color") String color,
                                        @RequestPart("file") MultipartFile file) throws IOException {
        System.out.println("MainController::check");
        Song song = songService.getSongById(songId);
        return service.processAudio(file, song, animal, color);
    }

}
