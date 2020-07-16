/* (C)2020 */
package org.anchoranalysis.image.io.bean.seed;

import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.io.generator.raster.obj.ObjWithBoundingBoxGenerator;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.io.generator.collection.IterableGeneratorWriter;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class ObjectOutputter extends SeedCollectionOutputter {

    // START BEAN PROPERTIES

    // END BEAN PROPERTIES

    public void output(
            SeedCollection seeds,
            ImageResolution res,
            BoundOutputManagerRouteErrors outputManager) {

        IterableGeneratorWriter.writeSubfolder(
                outputManager,
                "seeds",
                "seeds",
                () -> new ObjWithBoundingBoxGenerator(res),
                seeds.createMasks().asList(),
                true);
    }
}
