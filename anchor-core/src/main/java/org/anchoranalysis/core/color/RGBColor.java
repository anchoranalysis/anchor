/* (C)2020 */
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

public class RGBColor {

    private java.awt.Color delegate;

    public RGBColor() {
        this.delegate = new java.awt.Color(0, 0, 0);
    }

    public RGBColor(java.awt.Color delegate) {
        super();
        this.delegate = delegate;
    }

    public RGBColor(int rgb) {
        super();
        this.delegate = new java.awt.Color(rgb);
    }

    public RGBColor(int red, int green, int blue) {
        super();
        this.delegate = new java.awt.Color(red, green, blue);
    }

    public int getBlue() {
        return delegate.getBlue();
    }

    public int getGreen() {
        return delegate.getGreen();
    }

    public int getRGB() {
        return delegate.getRGB();
    }

    public int getRed() {
        return delegate.getRed();
    }

    public void setRed(int value) {
        delegate = new Color(value, delegate.getGreen(), delegate.getBlue());
    }

    public void setGreen(int value) {
        delegate = new Color(delegate.getRed(), value, delegate.getBlue());
    }

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

    public String hexString() {
        return Integer.toHexString(getRGB() & 0x00ffffff);
    }

    // from
    // http://stackoverflow.com/questions/946544/good-text-foreground-color-for-a-given-background-color
    public RGBColor textColor() {
        double grayDbl = 0.299 * getRed() + 0.587 * getGreen() + 0.114 * getBlue();

        if (grayDbl < 186) {
            return new RGBColor(255, 255, 255);
        } else {
            return new RGBColor(0, 0, 0);
        }
    }

    public java.awt.Color toAWTColor() {
        return delegate;
    }

    public RGBColor duplicate() {
        RGBColor out = new RGBColor();
        out.delegate = new Color(getRed(), getGreen(), getBlue());
        return out;
    }
}
