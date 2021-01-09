package org.arlian.site.start.service;

import org.arlian.site.start.model.picture.Orientation;
import org.arlian.site.start.model.picture.Picture;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageService {

    public void addImageToPicture(Picture picture, byte[] originalImage){

        // get the image & characteristics
        BufferedImage bufferedImage = createImageFromBytes(originalImage);
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        // set the orientation
        if (height > width)
            picture.setOrientation(Orientation.VERTICAL);
        else
            picture.setOrientation(Orientation.HORIZONTAL);

        // create thumbnail
        BufferedImage thumbnailImage = createScaledImage(bufferedImage, 200.0, 300.0);
        picture.setThumbnail(getBytes(thumbnailImage));

        // add original image
        picture.setImage(originalImage);
    }

    public BufferedImage createScaledImage(BufferedImage bufferedImage, double maxHeight, double maxWidth){

        // determine width and height
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        // determine necessary scaling for thumbnail
        double heightReductionFactor = height / maxHeight;
        double widthReductionFactor = width / maxWidth;
        double reductionFactor = Math.max(heightReductionFactor, widthReductionFactor);

        // create scaled image
        Image scaledImage = bufferedImage.getScaledInstance(
                (int) Math.floor(width / reductionFactor),
                (int) Math.floor(height / reductionFactor),
                Image.SCALE_SMOOTH);
        BufferedImage scaledBufferedImage = toBufferedImage(scaledImage);

        // return
        return scaledBufferedImage;
    }

    public byte[] getBytes(BufferedImage bufferedImage){
        try {
            return toByteArray(bufferedImage, "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;

    }
}
