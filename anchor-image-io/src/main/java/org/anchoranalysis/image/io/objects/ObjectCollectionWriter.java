/* (C)2020 */
package org.anchoranalysis.image.io.objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.IterableGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectCollectionWriter {

    public static final String MANIFEST_DESCRIPTION = "objects";

    public static IterableGenerator<ObjectCollection> generator() {
        return new GeneratorHDF5(true);
    }
}
