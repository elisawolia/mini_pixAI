package com.minipixal.webapp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Класс с описание запроса на проверку аудио
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AudioPitchRequest {

    /**
     * Сыгранная песня
     */
    private byte[] audio_bytes;

    /**
     * Эталонные ноты песни
     */
    private String ground_truth;

}
