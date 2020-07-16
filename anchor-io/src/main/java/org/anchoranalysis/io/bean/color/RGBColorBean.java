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
