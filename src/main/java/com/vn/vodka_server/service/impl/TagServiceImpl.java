package com.vn.vodka_server.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.repository.TagRepository;
import com.vn.vodka_server.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc().stream()
                .map(tag -> TagResponse.builder()
                        .id(String.valueOf(tag.getId()))
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .toList();
    }
}
