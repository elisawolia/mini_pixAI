package com.minipixal.webapp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AudioPitchRequest {

    private String audio_bytes;

    private String ground_truth;

}
