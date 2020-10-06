package org.anchoranalysis.test.image.rasterwriter;

import org.anchoranalysis.test.image.DualComparer;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor @Value
public class DualComparerWithExtension {

    private DualComparer comparer;
    private String extension;
}
