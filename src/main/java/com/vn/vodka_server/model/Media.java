package com.vn.vodka_server.model;

import com.vn.vodka_server.util.EMediaType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Media")
@Table(name = "media")
public class Media extends AbstractEntity {

    // id lưu trên cloudinary
    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "secure_url", nullable = false)
    private String secureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private EMediaType resourceType;

    @Column(name = "format")
    private String format;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "bytes")
    private Long bytes;

    @Column(name = "duration")
    private Double duration;

}
