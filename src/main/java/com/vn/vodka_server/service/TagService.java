package com.vn.vodka_server.service;

import java.util.List;

import com.vn.vodka_server.dto.response.TagResponse;

public interface TagService {
    List<TagResponse> getAllTags();
}