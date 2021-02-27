package org.anchoranalysis.bean.shared.path;

import java.nio.file.Path;
import org.anchoranalysis.bean.initializable.params.BeanInitialization;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import lombok.Getter;

/**
 * Initialization parameters for a named-set of file-paths.
 * 
 * @author Owen Feehan
 *
 */
public class FilePathInitialization implements BeanInitialization {

    /** Named-store of file-paths. */
    @Getter private final NamedProviderStore<Path> filePaths;
    
    public FilePathInitialization(SharedObjects sharedObjects) {
        filePaths = sharedObjects.getOrCreate(String.class);
    }
}
