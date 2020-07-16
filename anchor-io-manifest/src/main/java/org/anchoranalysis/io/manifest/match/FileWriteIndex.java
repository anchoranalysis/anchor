/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.file.FileWrite;

public class FileWriteIndex implements Match<FileWrite> {

    private String index;

    public FileWriteIndex(String index) {
        super();
        this.index = index;
    }

    @Override
    public boolean matches(FileWrite obj) {

        if (obj == null) {
            return false;
        }

        return obj.getIndex().equals(this.index);
    }
}
