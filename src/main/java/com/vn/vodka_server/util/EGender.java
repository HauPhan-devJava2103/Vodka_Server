package com.vn.vodka_server.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EGender {
    @JsonProperty("male")
    MALE,
    @JsonProperty("female")
    FEMALE,
    @JsonProperty("other")
    OTHER;
}
