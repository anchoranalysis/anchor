/* (C)2020 */
package org.anchoranalysis.image.io.bean.seed;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// Selects a mechanism by which seeds are outputted to the file system
public abstract class SeedCollectionOutputter extends AnchorBean<SeedCollectionOutputter> {

    public abstract void output(
            SeedCollection seeds, ImageResolution res, BoundOutputManagerRouteErrors outputManager);
}
