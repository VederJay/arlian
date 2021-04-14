package org.arlian.site.start.model.picture;

public interface ReducedImagePicture {

    long getId();

    PictureGroup getPictureGroup();

    byte[] getReducedImage();
}
