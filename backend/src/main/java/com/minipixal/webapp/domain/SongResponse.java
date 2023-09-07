package com.minipixal.webapp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс для ответа на запрос получения списка песен
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SongResponse {

    /**
     * Список песен
     */
    private List<Song> songsList;

}
