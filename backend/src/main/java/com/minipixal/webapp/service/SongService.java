package com.minipixal.webapp.service;

import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.domain.SongResponse;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SongService {

    public static final String FILE_PATH = "src/main/resources/songs.txt";

    public SongResponse readSongs() {
        SongResponse response = new SongResponse();
        List<Song> songsList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(FILE_PATH);
            BufferedReader reader = new BufferedReader(fileReader);
            while (reader.ready()) {
                String line = reader.readLine();
                String[] fields = line.split("\t");
                if (fields.length > 1) {
                    Song song = new Song();
                    song.setId(Integer.valueOf(fields[0]));
                    song.setName(fields[1]);
                    song.setNotes(fields[2]);
                    songsList.add(song);
                }
            }
        } catch (IOException e) {
            System.out.println("SongService::readSongs fot exception = " + e.getMessage());
        }
        response.setSongsList(songsList);
        return response;
    }

    public Song getSongById(Integer id) {
        SongResponse songs = readSongs();
        return songs
                .getSongsList()
                .stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst().get();
    }


}
