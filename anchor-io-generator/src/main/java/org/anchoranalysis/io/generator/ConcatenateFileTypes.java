package org.anchoranalysis.io.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.manifest.file.FileType;
import io.vavr.control.Either;

/**
 * Concatenates the arrays of {@link FileType} that may be returned from write operations.
 * 
 * @author Owen Feehan
 *
 */
public class ConcatenateFileTypes {
    
    // Tracks the file-types added with a list, but only if there's more than one generator
    // Otherwise it remembers the last added result.
    private Either<List<FileType>,Optional<FileType[]>> tracking;
    
    /**
     * Creates to always use a list for tracking.
     */
    public ConcatenateFileTypes() {
        tracking = Either.left(new ArrayList<>());
    }
    
    /**
     * Create to use a list for tracking if a flag is true, otherwise to just remember the last result of each call to {@link #add}.
     * 
     * @param supportMultipleCallsToAdd if true, a list is used for tracking (can handle multiple calls to {#add}), otherwise a list is not used, and the last call to {@link #add} is simply remembered.
     */
    public ConcatenateFileTypes(boolean supportMultipleCallsToAdd) {
        // Only create if the list has more than one item, otherwise leave as bull
        tracking = supportMultipleCallsToAdd ? Either.left(new ArrayList<>()) : Either.right( Optional.empty() );
    }
    
    public void add(Optional<FileType[]> fileTypes) {
        if (tracking.isLeft()) {
            // If we're using the list, then add any file-types to it
            if (fileTypes.isPresent()) {
                Arrays.stream(fileTypes.get()).forEach(tracking.getLeft()::add);
            }
        } else {
            // If we're not using the list, just remember the last array added
            tracking = Either.right(fileTypes);
        }
    }
    
    public Optional<FileType[]> allFileTypes() {
        if (tracking.isLeft()) {
            // Convert to an array if the list exists and there's more than 1 item
            // If it exists with 0 items, then Optional.empty() is returned
            return OptionalUtilities.createFromFlag( !tracking.getLeft().isEmpty(), () -> tracking.getLeft().toArray( new FileType[0] ) );
        } else {
            return tracking.get();
        }
    }
}