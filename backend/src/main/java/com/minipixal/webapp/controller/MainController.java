package com.minipixal.webapp.controller;

import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.domain.SongResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class MainController {

    public static final String GET_SONGS = "/songs_list";

    public static final String CHECK = "/check";

    @GetMapping(MainController.GET_SONGS)
    @CrossOrigin(origins = "http://localhost:3000")
    public SongResponse getSongs() {
        return new SongResponse(Arrays.asList(new Song("Song 1", "Asdfsdfasdf"),
                new Song("Song 2", "Aodfojdsjfkds")));
    }

    @PostMapping(MainController.CHECK)
    public ResponseEntity<Object> check() {
        return ResponseEntity.ok().build();
    }

}
