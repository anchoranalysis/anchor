package org.anchoranalysis.io.input.files;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.input.InputFromManager;
import com.google.common.base.Preconditions;

/**
 * A base class for inputs that refer to a single file.
 * 
 * @author Owen Feehan
 *
 */
public abstract class SingleFileInputBase implements InputFromManager {

    private NamedFile file;

    protected SingleFileInputBase(NamedFile file) {
        this.file = file;
        Preconditions.checkArgument(!file.getName().isEmpty());
    }
    
    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return Optional.of(file.getPath());
    }

    @Override
    public String toString() {
        return name();
    }

    public File getFile() {
        return file.getFile();
    }
}
