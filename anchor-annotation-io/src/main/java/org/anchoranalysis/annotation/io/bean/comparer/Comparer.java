/* (C)2020 */
package org.anchoranalysis.annotation.io.bean.comparer;

import java.nio.file.Path;
import org.anchoranalysis.annotation.io.wholeimage.findable.Findable;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;

public abstract class Comparer extends AnchorBean<Comparer> {

    public abstract Findable<ObjectCollection> createObjects(
            Path filePathSource, ImageDimensions dimensions, boolean debugMode)
            throws CreateException;
}
