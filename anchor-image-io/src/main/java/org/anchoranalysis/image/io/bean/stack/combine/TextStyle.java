/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.io.bean.stack.combine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.color.RGBColorBean;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.spatial.box.Extent;

/**
 * The size and style of text as it should appear in an image.
 *
 * @author Owen Feehan
 */
public class TextStyle extends AnchorBean<TextStyle> {

    // START BEAN PROPERTIES
    /** Font-size of drawn text */
    @BeanField @Getter @Setter private int fontSize = 12;

    /** Font-name of drawn text */
    @BeanField @Getter @Setter private String fontName = "SansSerif";

    /** Font-color of drawn text */
    @BeanField @Getter @Setter
    private RGBColorBean fontColor = new RGBColorBean(new RGBColor(Color.WHITE));

    /** Whether to bold the drawn text */
    @BeanField @Getter @Setter private boolean bold = false;

    // END BEAN PROPERTIES

    /**
     * Draws text on a {@link BufferedImage}.
     *
     * @param textToDraw the text to draw.
     * @param image the image.
     * @param extent the size of the {@code image}.
     */
    public void drawText(String textToDraw, BufferedImage image, Extent extent) {
        Graphics2D graphics = createGraphicsFromBufferedImage(image);

        graphics.fill(new Rectangle(0, 0, extent.x(), extent.y()));

        drawCenteredString(textToDraw, extent, graphics);
    }

    private Graphics2D createGraphicsFromBufferedImage(BufferedImage bufferedImage) {
        Graphics2D graphics = bufferedImage.createGraphics();

        Font font = new Font(fontName, bold ? Font.BOLD : Font.PLAIN, fontSize);

        graphics.setColor(fontColor.toAWTColor());
        graphics.setFont(font);
        return graphics;
    }

    private static void drawCenteredString(String stringToDraw, Extent extent, Graphics graphics) {
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (extent.x() - metrics.stringWidth(stringToDraw)) / 2;
        int y =
                (metrics.getAscent()
                        + (extent.y() - (metrics.getAscent() + metrics.getDescent())) / 2);
        graphics.drawString(stringToDraw, x, y);
    }
}
