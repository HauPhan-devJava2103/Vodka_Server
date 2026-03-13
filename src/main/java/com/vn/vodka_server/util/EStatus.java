package com.vn.vodka_server.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EStatus {
    @JsonProperty("active")
    ACTIVE,
    @JsonProperty("inactive")
    INACTIVE;
}
