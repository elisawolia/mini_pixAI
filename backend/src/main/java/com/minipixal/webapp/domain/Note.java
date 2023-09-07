package com.minipixal.webapp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Note {

    private Float timestamp;

    private String note;

}
