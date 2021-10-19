package org.schors.merch.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Child {
    private String username;
    private String inGameName;
    private Integer power;
    private Gender gender;
}
