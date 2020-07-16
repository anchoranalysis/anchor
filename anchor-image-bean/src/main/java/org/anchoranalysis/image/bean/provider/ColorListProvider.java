/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.CreateException;

public abstract class ColorListProvider extends NullParamsBean<ColorListProvider>
        implements Provider<ColorList> {

    public abstract ColorList create() throws CreateException;
}
