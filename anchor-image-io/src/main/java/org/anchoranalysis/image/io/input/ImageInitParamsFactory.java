/* (C)2020 */
package org.anchoranalysis.image.io.input;

import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.io.output.bound.BoundIOContext;

public class ImageInitParamsFactory {

    private ImageInitParamsFactory() {}

    public static ImageInitParams create(BoundIOContext context) {
        SharedObjects so = new SharedObjects(context.common());
        return new ImageInitParams(so);
    }
}
