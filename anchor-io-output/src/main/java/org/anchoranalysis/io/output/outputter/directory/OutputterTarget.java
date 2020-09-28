package org.anchoranalysis.io.output.outputter.directory;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

/**
 * The directory and prefix an outputter writes to.
 * 
 * <p>This class is <i>immutable</i>.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor(access=AccessLevel.PRIVATE) @Value
public class OutputterTarget {

    /** The directory to which the output-manager is bound to. */
    private BoundDirectory directory;
    
    @Getter private final FilePathPrefix prefix;
    
    public OutputterTarget(FilePathPrefix prefix, boolean deleteExistingDirectory) throws BindFailedException {
        this( 
                new BoundDirectory(prefix.getFolderPath(), deleteExistingDirectory),
                prefix );
    }
    
    /**
     * Creates a new {@link OutputterTarget} with a changed prefix.
     * 
     * <p>The directory-component of the prefix must be equal to or a sub-directory of the existing {@code directory}.
     * 
     * @param prefixToAssign the prefix to assign
     * @return a new shallow-copied {@link OutputterTarget} but instead with {@code prefixToAssign}.
     * @throws BindFailedException if the sub-directory cannot be outputted to
     */
    public OutputterTarget changePrefix(FilePathPrefix prefixToAssign) throws BindFailedException {
        return new OutputterTarget(directory.bindToSubdirectory(prefixToAssign.getFolderPath()), prefixToAssign);
    }

    public Optional<WriterExecuteBeforeEveryOperation> getParentDirectoryCreator() {
        return directory.getParentDirectoryCreator();
    }

    public Path getFolderPath() {
        return prefix.getFolderPath();
    }

    public Path outFilePath(String filePathRelative) {
        return prefix.outFilePath(filePathRelative);
    }

    public Path relativePath(Path fullPath) {
        return prefix.relativePath(fullPath);
    }
}
