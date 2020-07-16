/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.file.FileWrite;

public class FileWriteOutputName implements Match<FileWrite> {

    private String outputName;

    public FileWriteOutputName(String outputName) {
        super();
        this.outputName = outputName;
    }

    @Override
    public boolean matches(FileWrite obj) {
        return obj.getOutputName().equals(outputName);
    }
}
