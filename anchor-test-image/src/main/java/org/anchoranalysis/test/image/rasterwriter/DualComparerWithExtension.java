package org.anchoranalysis.test.image.rasterwriter;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.test.image.DualComparer;

@AllArgsConstructor
@Value
public class DualComparerWithExtension {

    private DualComparer comparer;
    private String extension;
}
