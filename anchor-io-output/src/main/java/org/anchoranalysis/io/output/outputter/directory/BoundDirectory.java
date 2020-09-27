package org.anchoranalysis.io.output.outputter.directory;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A particular directory that is used as a binding-path by {@link org.anchoranalysis.io.output.outputter.OutputterChecked}.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class BoundDirectory {

    /**
     * A pool with a unique {@link LazyDirectoryCreator} for every directory.
     */
    private final LazyDirectoryCreatorPool directoryCreator;
    
    /** Parent directory creator to be executed before any derived sub-directories */
    @Getter private final Optional<WriterExecuteBeforeEveryOperation> parentDirectoryCreator;

    /**
     * Creates for a particular directory.
     * 
     * @param directoryPath the path to the directory
     * @param deleteExistingDirectory if true, any existing directory at the intended path for creation, is first deleted. If false, an exception is thrown in this circumstance.
     */
    public BoundDirectory(Path directoryPath, boolean deleteExistingDirectory) {
        this.directoryCreator = new LazyDirectoryCreatorPool(directoryPath, deleteExistingDirectory);
        this.parentDirectoryCreator = Optional.empty();
    }
    
    /**
     * Creates a new {@link BoundDirectory} that is a sub-directory of the existing directory.
     *  
     * @param directoryPath sub-directory of existing {@link BoundDirectory} for which a new {@link BoundDirectory} will be created.
     * @return a newly created {@link BoundDirectory} bound to {@code directoryPath}.
     * @throws BindFailedException
     */
    public BoundDirectory bindToDirectory(Path directoryPath) throws BindFailedException {
        Preconditions.checkArgument( rootDirectoryContains(directoryPath) );
        try {
            return new BoundDirectory(directoryCreator, Optional.of( directoryCreator.getOrCreate(directoryPath, parentDirectoryCreator)) );
        } catch (GetOperationFailedException e) {
            throw new BindFailedException(e);
        }        
    }
    
    private boolean rootDirectoryContains(Path path) {
        return path.normalize().toString().contains(directoryCreator.getRootDirectory().toString());
    }
}
