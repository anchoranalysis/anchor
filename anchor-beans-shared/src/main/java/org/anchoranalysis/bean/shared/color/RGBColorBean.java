/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.bean.shared.color;

import java.awt.Color;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.color.RGBColor;

/**
 * A bean describing a color in the <a href="https://en.wikipedia.org/wiki/RGB_color_space">RGB
 * color space</a>.
 *
 * @author Owen Feehan
 */
public class RGBColorBean extends AnchorBean<RGBColorBean> {

    private RGBColor delegate;

    /**
     * Creates with a completely black color.
     *
     * <p>i.e. Red, green, blue values are assigned <code>0</code>
     */
    public RGBColorBean() {
        delegate = new RGBColor();
    }

    /**
     * Create from a {@link RGBColor}.
     *
     * @param color the color, which continues to be used internally after the constructor call.
     */
    public RGBColorBean(RGBColor color) {
        this.delegate = color;
    }

    /**
     * Create from a {@link java.awt.Color}.
     *
     * @param color the color, which is no longer used internally after the constructor call.
     */
    public RGBColorBean(java.awt.Color color) {
        this.delegate = new RGBColor(color);
    }

    /**
     * Create from values for red, green and blue.
     *
     * @param red the value for the <b>red</b> color component.
     * @param green the value for the <b>green</b> color component.
     * @param blue the value for the <b>blue</b> color component.
     */
    public RGBColorBean(int red, int green, int blue) {
        this.delegate = new RGBColor(red, green, blue);
    }

    /**
     * The <i>red</i> component value of the RGB color.
     *
     * @return a value between 0 and 255 (inclusive)
     */
    public int getRed() {
        return delegate.getRed();
    }

    /**
     * The <i>green</i> component value of the RGB color.
     *
     * @return a value between 0 and 255 (inclusive)
     */
    public int getGreen() {
        return delegate.getGreen();
    }

    /**
     * The <i>blue</i> component value of the RGB color.
     *
     * @return a value between 0 and 255 (inclusive)
     */
    public int getBlue() {
        return delegate.getBlue();
    }

    /**
     * Sets the <i>red</i> component value.
     *
     * @param value the value to assign
     */
    public void setRed(int value) {
        delegate.setRed(value);
    }

    /**
     * Sets the <i>green</i> component value.
     *
     * @param value the value to assign
     */
    public void setGreen(int value) {
        delegate.setGreen(value);
    }

    /**
     * Sets the <i>blue</i> component value.
     *
     * @param value the value to assign
     */
    public void setBlue(int value) {
        delegate.setBlue(value);
    }

    /**
     * Returns the RGB value encoded as an int as in {@link java.awt.Color#getRGB()}.
     *
     * @return the rgb value encoded as an int.
     */
    public int getRGB() {
        return delegate.getRGB();
    }

    /**
     * The {@link RGBColor} associated with the bean.
     *
     * @return the color, as used internally.
     */
    public RGBColor toRGBColor() {
        return delegate;
    }

    /**
     * Converts to a {@link java.awt.Color} representation.
     *
     * @return the color, as used internally.
     */
    public Color toAWTColor() {
        return delegate.toAWTColor();
    }

    @Override
    public RGBColorBean duplicateBean() {
        RGBColorBean bean = super.duplicateBean();
        bean.delegate = delegate.duplicate();
        return bean;
    }
}
