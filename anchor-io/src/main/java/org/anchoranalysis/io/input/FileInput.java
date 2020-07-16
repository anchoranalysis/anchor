/* (C)2020 */
package org.anchoranalysis.io.input;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.input.descriptivename.DescriptiveFile;

public class FileInput implements InputFromManager {

    private File file;
    private String descriptiveName;

    public FileInput(DescriptiveFile file) {
        super();
        this.file = file.getFile();
        this.descriptiveName = file.getDescriptiveName();
        assert (!descriptiveName.isEmpty());
    }

    @Override
    public String descriptiveName() {
        return descriptiveName;
    }

    @Override
    public Optional<Path> pathForBinding() {
        return Optional.of(file.toPath());
    }

    @Override
    public String toString() {
        return descriptiveName();
    }

    public File getFile() {
        return file;
    }
}
