package org.anchoranalysis.image.stack;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;

@FunctionalInterface
public interface NamedStacksSupplier {

    NamedProvider<Stack> get(ProgressReporter progressReporter) throws OperationFailedException;
    
    public static NamedStacksSupplier cache(NamedStacksSupplier supplier) {
        return CachedProgressingSupplier.cache(supplier::get)::get;
    }
}
