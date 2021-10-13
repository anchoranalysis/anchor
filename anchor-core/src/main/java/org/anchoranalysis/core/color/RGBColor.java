package org.anchoranalysis.core.color;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.awt.Color;

/**
 * A color encoded in <a href="https://en.wikipedia.org/wiki/RGB_color_space">RGB color space</a>.
 *
 * @author Owen Feehan
 */
public class RGBColor {

    private java.awt.Color delegate;

    /** Create with black color. */
    public RGBColor() {
        this.delegate = new java.awt.Color(0, 0, 0);
    }

    /**
     * Create from a {@link java.awt.Color}.
     *
     * @param color the color to create from.
     */
    public RGBColor(java.awt.Color color) {
        this.delegate = color;
    }

    /**
     * Creates with an RGB value encoded as an int, as in the constructor for {@link
     * java.awt.Color}.
     *
     * @param rgb an integer with the red component in bits 16-23, the green component in bits 8-15,
     *     and the blue component in bits 0-7.
     */
    public RGBColor(int rgb) {
        this.delegate = new java.awt.Color(rgb);
    }

    /**
     * Creates with specific values for the red, green and blue components.
     *
     * @param red value for the <i>red</i> component.
     * @param green value for the <i>green</i> component.
     * @param blue value for the <i>blue</i> component.
     */
    public RGBColor(int red, int green, int blue) {
        this.delegate = new java.awt.Color(red, green, blue);
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
     * Returns the RGB value encoded as an int as in {@link java.awt.Color#getRGB()}.
     *
     * @return the rgb value encoded as an int.
     */
    public int getRGB() {
        return delegate.getRGB();
    }

    /**
     * Sets the <i>red</i> component value.
     *
     * @param value the value to assign
     */
    public void setRed(int value) {
        delegate = new Color(value, delegate.getGreen(), delegate.getBlue());
    }

    /**
     * Sets the <i>green</i> component value.
     *
     * @param value the value to assign
     */
    public void setGreen(int value) {
        delegate = new Color(delegate.getRed(), value, delegate.getBlue());
    }

    /**
     * Sets the <i>blue</i> component value.
     *
     * @param value the value to assign
     */
    public void setBlue(int value) {
        delegate = new Color(delegate.getRed(), delegate.getGreen(), value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RGBColor) {
            RGBColor objCast = (RGBColor) obj;
            return delegate.equals(objCast.delegate);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * Converts the red-blue-green values as a hex-string.
     *
     * @return a hex representation of the color values.
     */
    public String hexString() {
        return Integer.toHexString(getRGB() & 0x00ffffff);
    }

    /**
     * Converts to a {@link java.awt.Color} representation.
     *
     * @return the color, as used internally.
     */
    public java.awt.Color toAWTColor() {
        return delegate;
    }

    /**
     * Creates a deep-copy of the current object.
     *
     * @return a newly-created deep copy.
     */
    public RGBColor duplicate() {
        RGBColor out = new RGBColor();
        out.delegate = new Color(getRed(), getGreen(), getBlue());
        return out;
    }
}
