package org.anchoranalysis.io.input;

import java.nio.file.Path;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.io.error.AnchorIOException;

@FunctionalInterface
public interface PathSupplier {

    Path get() throws AnchorIOException;
    
    public static PathSupplier cache(PathSupplier supplier) {
        return CachedSupplier.cache(supplier::get)::get;
    }
}
