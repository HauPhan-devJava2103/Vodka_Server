package com.vn.vodka_server.dto.projection;

import java.util.Date;


public interface GenreAdminProjection {

    Long getId();

    String getName();

    String getSlug();

    Long getMovieCount();

    Long getViewCount();

    Date getCreatedAt();

    Date getUpdatedAt();

}
