package com.minipixal.webapp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Описание ошибок
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Note {

    /**
     * Сыграна ли нота
     */
    private Boolean is_played;

    /**
     * Время ноты
     */
    private List<Float> timestamp;

    /**
     * Нота
     */
    private String note;

}
