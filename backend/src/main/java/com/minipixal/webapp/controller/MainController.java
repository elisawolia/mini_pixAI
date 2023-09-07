package com.minipixal.webapp.controller;

import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.domain.SongResponse;
import com.minipixal.webapp.service.AudioService;
import com.minipixal.webapp.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@RestController
@Slf4j
public class MainController {

    public static final String GET_SONGS = "/songs_list";

    public static final String CHECK = "/check";

    private final AudioService service;

    private final SongService songService;

    @Autowired
    public MainController(AudioService audioService, SongService songService) {
        this.service = audioService;
        this.songService = songService;
    }

    @GetMapping(MainController.GET_SONGS)
    @CrossOrigin(origins = "http://localhost:3000")
    public SongResponse getSongs() {
        return songService.readSongs();
    }

    @PostMapping(value = MainController.CHECK, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> check(@RequestParam(value="songId", required=true) Integer songId,
                                        @RequestParam(value="animal", required=true) String animal,
                                        @RequestParam(value="color", required=false) String color,
                                        @RequestPart("file") MultipartFile file) throws IOException {
        System.out.println("Animal is = " + animal);
        System.out.println("SongId is = " + songId.toString());
        System.out.println("Color is = " + color);
        Song song = songService.getSongById(songId);
        return service.processAudio(file, song, animal, color);
    }

}
