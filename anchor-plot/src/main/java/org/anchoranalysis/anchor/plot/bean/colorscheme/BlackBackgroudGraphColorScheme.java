/* (C)2020 */
package org.anchoranalysis.anchor.plot.bean.colorscheme;

import org.anchoranalysis.io.bean.color.RGBColorBean;

public class BlackBackgroudGraphColorScheme extends GraphColorScheme {

    public BlackBackgroudGraphColorScheme() {

        setBackgroundColor(new RGBColorBean(0, 0, 0));
        setPlotBackgroundColor(new RGBColorBean(0, 0, 0));
        setAxisColor(new RGBColorBean(255, 255, 255));
    }
}
