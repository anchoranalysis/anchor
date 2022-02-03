package org.anchoranalysis.io.output.outputter;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options that influence how an output directory is created.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class DirectoryCreationParameters {

    /**
     * When true, this will delete any existing directory with the same path, and then create it
     * anew.
     *
     * <p>When false, an exception is thrown if an existing directory with the same path already
     * exists.
     */
    private final boolean deleteExistingDirectory;

    /**
     * When defined, this {@code consumer} is called when the directory is first created, as it is
     * created lazily only when first needed.
     *
     * <p>It is called with the path of the directory as an argument.
     */
    private final Optional<Consumer<Path>> callUponDirectoryCreation;

    /**
     * Creates to <i>not</i> delete directories, and with no consumer called upon directory
     * creation.
     */
    public DirectoryCreationParameters() {
        this.deleteExistingDirectory = false;
        this.callUponDirectoryCreation = Optional.empty();
    }
}
