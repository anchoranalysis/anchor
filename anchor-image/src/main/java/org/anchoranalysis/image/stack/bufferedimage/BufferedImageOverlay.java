/* (C)2020 */
package org.anchoranalysis.image.stack.bufferedimage;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class BufferedImageOverlay {

    private BufferedImageOverlay() {}

    public static void overlayBufferedImage(
            BufferedImage imageSrc, BufferedImage imageInsert, int x, int y) {
        Graphics graphics = imageSrc.getGraphics();
        graphics.drawImage(
                imageInsert, x, y, imageInsert.getWidth(), imageInsert.getHeight(), null);
    }
}
