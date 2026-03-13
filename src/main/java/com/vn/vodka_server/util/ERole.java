package com.vn.vodka_server.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ERole {
    @JsonProperty("admin")
    ADMIN,
    @JsonProperty("user")
    USER;
}
