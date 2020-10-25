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

package org.anchoranalysis.image.io.bean.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.color.RGBColorBean;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.bean.spatial.SizeXY;
import org.anchoranalysis.image.core.bufferedimage.CreateStackFromBufferedImage;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.image.io.stack.StackWriteOptionsFactory;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * The size and style of text as it should appear in an image.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class TextStyle extends AnchorBean<TextStyle> {

    // START BEAN PROPERTIES
    /** Explicit size of the image the string is draw on */
    @BeanField @OptionalBean @Getter @Setter private SizeXY size;

    /** Font-size of drawn text */
    @BeanField @Getter @Setter private int fontSize = 12;

    /** Font-name of drawn text */
    @BeanField @Getter @Setter private String fontName = "SansSerif";

    /** Font-color of drawn text */
    @BeanField @Getter @Setter
    private RGBColorBean fontColor = new RGBColorBean(new RGBColor(Color.WHITE));

    /** Whether to bold the drawn text */
    @BeanField @Getter @Setter private boolean bold = false;

    /**
     * Padding added around the text in both dimensions if a default size is inferred (ignored if an
     * explicit size is specified )
     */
    @BeanField @Getter @Setter private double padding = 0;
    // END BEAN PROPERTIES

    public TextStyle(double padding) {
        this.padding = padding;
    }

    /** Creates a generator, which produces a drawn string on an image when generated */
    public RasterGeneratorSelectFormat<String> createGenerator() {
        return new RasterizedTextGenerator();
    }

    /**
     * Creates an image with text matching the style/size specified in this bean.
     *
     * @author Owen Feehan
     */
    private class RasterizedTextGenerator extends RasterGeneratorSelectFormat<String> {

        @Override
        public Stack transform(String element) throws OutputWriteFailedException {

            SizeXY resolvedSize =
                    Optional.ofNullable(size).orElseGet(() -> alternativeSizeFromDefault(element));

            assert (resolvedSize.asExtent().volumeXY() > 0);

            BufferedImage bufferedImage =
                    new BufferedImage(
                            resolvedSize.getWidth(),
                            resolvedSize.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = createGraphicsFromBufferedImage(bufferedImage);

            drawCenteredString(element, resolvedSize, graphics);

            try {
                return CreateStackFromBufferedImage.create(bufferedImage);
            } catch (OperationFailedException e) {
                throw new OutputWriteFailedException(e);
            }
        }

        @Override
        public Optional<ManifestDescription> createManifestDescription() {
            return Optional.of(new ManifestDescription("raster", "text"));
        }

        @Override
        public StackWriteOptions guaranteedImageAttributes() {
            return StackWriteOptionsFactory.rgbMaybe3D();
        }

        private Graphics2D createGraphicsFromBufferedImage(BufferedImage bufferedImage) {

            Graphics2D graphics = bufferedImage.createGraphics();

            Font font = new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize);

            graphics.setColor(fontColor.toAWTColor());
            graphics.setFont(font);
            return graphics;
        }

        private void drawCenteredString(String stringToDraw, SizeXY size, Graphics g) {
            FontMetrics fm = g.getFontMetrics();
            int x = (size.getWidth() - fm.stringWidth(stringToDraw)) / 2;
            int y = (fm.getAscent() + (size.getHeight() - (fm.getAscent() + fm.getDescent())) / 2);
            g.drawString(stringToDraw, x, y);
        }

        private SizeXY alternativeSizeFromDefault(String element) {

            Graphics2D graphics =
                    createGraphicsFromBufferedImage(
                            new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB));
            FontMetrics fm = graphics.getFontMetrics();
            Rectangle2D defaultSize = fm.getStringBounds(element, graphics);
            return new SizeXY(
                    addPadding(defaultSize.getWidth()), addPadding(defaultSize.getHeight()));
        }

        private int addPadding(double value) {
            return (int) Math.ceil(value + (padding * 2));
        }
    }
}
