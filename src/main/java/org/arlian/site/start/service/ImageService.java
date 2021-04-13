package org.arlian.site.start.service;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.picture.Orientation;
import org.arlian.site.start.model.picture.Picture;
import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {

    private final ResourceLoader resourceLoader;

    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void addImageToPicture(Picture picture, byte[] originalImageBytes) throws IOException, ImageReadException, ImageWriteException {

        // add original image
        picture.setImage(originalImageBytes);

        // get the image & characteristics
        BufferedImage bufferedImage = getBufferedImageFromBytes(originalImageBytes);
        assert bufferedImage != null;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        // set the orientation
        if (height > width)
            picture.setOrientation(Orientation.VERTICAL);
        else
            picture.setOrientation(Orientation.HORIZONTAL);

        // create and set thumbnail
        byte[] thumbnailImageBytes = getThumbnailImageBytes(originalImageBytes, 200, 300);
        picture.setThumbnail(thumbnailImageBytes);

    }

    public void addImageToLink(Link link, byte[] originalImageBytes) throws IOException, ImageWriteException, ImageReadException {

        // add original image
        link.setImage(originalImageBytes);

        // add thumbnail image
        byte[] thumbnailImageBytes = getThumbnailImageBytes(originalImageBytes, 128, 128);
        link.setThumbnail(thumbnailImageBytes);
    }

    private byte[] getThumbnailImageBytes(byte[] originalImageBytes, int maxWidth, int maxHeight)
            throws IOException, ImageReadException, ImageWriteException {
        BufferedImage bufferedImage = getBufferedImageFromBytes(originalImageBytes);
        assert bufferedImage != null;

        // create thumbnail if picture larger than required for thumbnail
        if (bufferedImage.getWidth() > maxWidth || bufferedImage.getHeight() > maxHeight) {
            BufferedImage thumbnailImage = createScaledImage(bufferedImage, maxHeight, maxWidth);
            return Imaging.writeImageToBytes(thumbnailImage, ImageFormats.PNG, null);
        }

        // use picture itself as thumbnail if it's smaller
        else
            return originalImageBytes;

    }

    private BufferedImage getBufferedImageFromBytes(byte[] originalImageBytes) throws IOException, ImageReadException {
        BufferedImage bufferedImage;

        // Read the given bytes as an image
        try {
            bufferedImage = Imaging.getBufferedImage(originalImageBytes);
        }

        // If the bytes can't be read as an image (they're svg or a non-image file type), then use the default
        catch (ImageReadException imageReadException){
            Resource resource = resourceLoader.getResource("classpath:static/img/no-image-available-icon.jpg");
            InputStream is = resource.getInputStream();
            byte[] imageNotAvailableBytes = is.readAllBytes();
            is.close();
            bufferedImage = Imaging.getBufferedImage(imageNotAvailableBytes);
        }

        return bufferedImage;
    }

    private BufferedImage createScaledImage(BufferedImage bufferedImage, int maxHeight, int maxWidth){
        // create scaled image
        return Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC,
                maxWidth, maxHeight);
    }
}
