package org.arlian.site.start.model.picture;

public interface ThumbnailPicture {

    long getId();

    PictureGroup getPictureGroup();

    byte[] getThumbnail();
}
