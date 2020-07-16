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
/* (C)2020 */
package org.anchoranalysis.io.bean.color;

import java.awt.Color;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.color.RGBColor;

public class RGBColorBean extends AnchorBean<RGBColorBean> {

    private RGBColor delegate;

    public RGBColorBean() {
        delegate = new RGBColor();
    }

    public RGBColorBean(RGBColor color) {
        this.delegate = color;
    }

    public RGBColorBean(java.awt.Color delegate) {
        this.delegate = new RGBColor(delegate);
    }

    public RGBColorBean(int red, int green, int blue) {
        this.delegate = new RGBColor(red, green, blue);
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
        delegate.setRed(value);
    }

    public void setGreen(int value) {
        delegate.setGreen(value);
    }

    public void setBlue(int value) {
        delegate.setBlue(value);
    }

    public RGBColor rgbColor() {
        return delegate;
    }

    public RGBColor textColor() {
        return delegate.textColor();
    }

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
