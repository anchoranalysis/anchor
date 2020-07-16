/* (C)2020 */
package org.anchoranalysis.io.input.descriptivename;

import java.io.File;
import java.nio.file.Path;

/**
 * A file with an associated descriptive-name
 *
 * @author Owen Feehan
 */
public class DescriptiveFile {

    private File file;
    private String descriptiveName;

    public DescriptiveFile(File file, String descriptiveName) {
        super();
        this.file = file;
        this.descriptiveName = descriptiveName;
    }

    public File getFile() {
        return file;
    }

    public Path getPath() {
        return file.toPath();
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }
}
