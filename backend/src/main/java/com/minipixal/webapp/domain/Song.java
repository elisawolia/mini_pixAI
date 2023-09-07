package com.minipixal.webapp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс с описанием песни
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Song {

    /**
     * Уникальный идеентификатор песни
     */
    private Integer id;

    /**
     * Наименование песни
     */
    private String name;

    /**
     * Эталонные ноты песни
     */
    private String notes;

}
