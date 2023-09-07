package com.minipixal.webapp.service;

import com.minipixal.webapp.domain.Song;
import com.minipixal.webapp.domain.SongResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для взаимодействия с хранилищем песен
 */
@Component
public class SongService {

    public static final String FILE_PATH = "src/main/resources/songs.txt";

    /**
     * Получение списка песен
     */
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
            System.out.println("SongService::readSongs got exception = " + e.getMessage());
        }
        response.setSongsList(songsList);
        return response;
    }

    /**
     * Получение информации о конкретной песне
     */
    public Song getSongById(Integer id) {
        SongResponse songs = readSongs();
        return songs
                .getSongsList()
                .stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst().get();
    }


}
