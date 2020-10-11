package org.anchoranalysis.io.bean.files.provider;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.exception.FilesProviderException;
import org.anchoranalysis.io.params.InputContextParams;
import lombok.Getter;
import lombok.Setter;

/**
 * Like {@link FilesProviderWithDirectory} but employs a unary operator on a call to an existing {@link FilesProviderWithDirectory}.
 * 
 * <p>It is assumed that the associated directory is not altered.
 * 
 * @author Owen Feehan
 *
 */
public abstract class FilesProviderWithDirectoryUnary extends FilesProviderWithDirectory {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FilesProviderWithDirectory filesProvider;
    // END BEAN PROPERTIES

    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params)
            throws FilesProviderException {
        return transform( filesProvider.matchingFilesForDirectory(directory, params) );
    }

    @Override
    public Path getDirectoryAsPath(InputContextParams inputContext) {
        return filesProvider.getDirectoryAsPath(inputContext);
    }
    
    /**
     * Transform an existing collection of files.
     * 
     * <p>Note that the incoming collection of files may be modified, and can no
     * longer be used in its original form after this method call.
     * 
     * @param source the incoming files (which may be consumed and modified).
     * @return the transformed (outgoing) files.
     */
    protected abstract Collection<File> transform(Collection<File> source) throws FilesProviderException;
}
