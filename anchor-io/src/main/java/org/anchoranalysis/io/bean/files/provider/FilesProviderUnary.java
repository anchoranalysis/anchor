package org.anchoranalysis.io.bean.files.provider;

import java.io.File;
import java.util.Collection;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.exception.FilesProviderException;
import lombok.Getter;
import lombok.Setter;

/**
 * Like {@link FilesProvider} but employs a unary operator on a call to an existing {@link FilesProvider}.
 *  
 * @author Owen Feehan
 *
 */
public abstract class FilesProviderUnary extends FilesProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FilesProvider filesProvider;
    // END BEAN PROPERTIES
    
    @Override
    public Collection<File> create(InputManagerParams params) throws FilesProviderException {
        return transform(filesProvider.create(params), params.isDebugModeActivated());
    }
    
    /**
     * Transform an existing collection of files.
     * 
     * <p>Note that the incoming collection of files may be modified, and can no
     * longer be used in its original form after this method call.
     * 
     * @param source the incoming files (which may be consumed and modified).
     * @param debugMode whether we are executing in debug-mode or not
     * @return the transformed (outgoing) files.
     */
    protected abstract Collection<File> transform(Collection<File> source, boolean debugMode) throws FilesProviderException;
}
