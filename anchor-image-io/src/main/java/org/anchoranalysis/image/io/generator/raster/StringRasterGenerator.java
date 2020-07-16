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

package org.anchoranalysis.image.io.generator.raster;

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
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.bufferedimage.CreateStackFromBufferedImage;
import org.anchoranalysis.io.bean.color.RGBColorBean;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@NoArgsConstructor
public class StringRasterGenerator extends AnchorBean<StringRasterGenerator> {

    public StringRasterGenerator(String text) {
        super();
        this.text = text;
    }

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String text = "text";

    @BeanField @Getter @Setter private int width = -1;

    @BeanField @Getter @Setter private int height = -1;

    @BeanField @Getter @Setter private int fontSize = 12;

    @BeanField @Getter @Setter private String fontName = "SansSerif";

    @BeanField @Getter @Setter
    private RGBColorBean fontColor = new RGBColorBean(new RGBColor(Color.WHITE));

    @BeanField @Getter @Setter private boolean bold = false;

    @BeanField @Getter @Setter private double padding = 0;
    // END BEAN PROPERTIES

    // A generator associated with this bean
    private class Generator extends RasterGenerator
            implements IterableObjectGenerator<String, Stack> {

        @Override
        public String getIterableElement() {
            return text;
        }

        @Override
        public void setIterableElement(String element) {
            StringRasterGenerator.this.text = element;
        }

        @Override
        public Stack generate() throws OutputWriteFailedException {

            Rectangle2D defaultSize = calcDefaultSize();

            if (width == -1) {
                width = (int) Math.ceil(defaultSize.getWidth() + (padding * 2));
            }

            if (height == -1) {
                height = (int) Math.ceil(defaultSize.getHeight() + (padding * 2));
            }

            BufferedImage bufferedImage =
                    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = createGraphicsFromBufferedImage(bufferedImage);

            drawCenteredString(text, width, height, graphics);

            try {
                return CreateStackFromBufferedImage.create(bufferedImage);
            } catch (OperationFailedException e) {
                throw new OutputWriteFailedException(e);
            }
        }

        @Override
        public boolean isRGB() {
            return true;
        }

        @Override
        public Optional<ManifestDescription> createManifestDescription() {
            return Optional.of(new ManifestDescription("raster", "text"));
        }

        @Override
        public ObjectGenerator<Stack> getGenerator() {
            return this;
        }

        private Rectangle2D calcDefaultSize() {

            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = createGraphicsFromBufferedImage(bufferedImage);
            FontMetrics fm = graphics.getFontMetrics();
            return fm.getStringBounds(text, graphics);
        }

        private Graphics2D createGraphicsFromBufferedImage(BufferedImage bufferedImage) {

            Graphics2D graphics = bufferedImage.createGraphics();

            Font font = new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize);

            graphics.setColor(fontColor.toAWTColor());
            graphics.setFont(font);
            return graphics;
        }

        private void drawCenteredString(String s, int w, int h, Graphics g) {
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(s)) / 2;
            int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
            g.drawString(s, x, y);
        }
    }

    // Create an iterable generator, which produces Stack for different Strings
    public IterableObjectGenerator<String, Stack> createGenerator() {
        return new Generator();
    }

    // Creates a stack with this string
    public Stack generateStack() throws OutputWriteFailedException {
        return new Generator().generate();
    }
}
