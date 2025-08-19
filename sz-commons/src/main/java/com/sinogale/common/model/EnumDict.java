package com.sinogale.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class EnumDict {

    private  String name;

    private  Integer code;

    private  String text;


    public EnumDict(String name, Integer code, String text) {
        this.name = name;
        this.code = code;
        this.text = text;
    }

}
