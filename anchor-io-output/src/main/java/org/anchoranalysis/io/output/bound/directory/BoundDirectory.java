package org.anchoranalysis.io.output.bound.directory;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BoundDirectory {

    private final LazyDirectoryCreatorCache directoryCreator;
    
    /** Parent directory creator to be executed before any derived sub-directories */
    @Getter private final Optional<WriterExecuteBeforeEveryOperation> parentDirectoryCreator;

    public BoundDirectory(Path directoryPath, boolean deleteExistingFolder) {
        this.directoryCreator = new LazyDirectoryCreatorCache(directoryPath, deleteExistingFolder);
        this.parentDirectoryCreator = Optional.empty();
    }
    
    /**
     * Creates a new {@link BoundDirectory} that is a sub-directory of the existing directory.
     *  
     * @param directoryPath subdirectory of existing {@link BoundDirectory} for which a new {@link BoundDirectory} will be created.
     * @return a newly created {@link BoundDirectory} bound to {@code directoryPath}.
     * @throws BindFailedException
     */
    public BoundDirectory bindToDirectory(Path directoryPath) throws BindFailedException {
        Preconditions.checkArgument( directoryPath.getParent().normalize().equals(directoryCreator.getRootDirectory()) );
        try {
            return new BoundDirectory(directoryCreator, Optional.of( directoryCreator.getOrCreate(directoryPath, parentDirectoryCreator)) );
        } catch (GetOperationFailedException e) {
            throw new BindFailedException(e);
        }        
    }
}
