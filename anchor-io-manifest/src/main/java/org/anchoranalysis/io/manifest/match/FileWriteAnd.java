/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.file.FileWrite;

public class FileWriteAnd extends MatchAnd<FileWrite> {

    public FileWriteAnd() {
        super();
    }

    public FileWriteAnd(Match<FileWrite> condition1, Match<FileWrite> condition2) {
        super(condition1, condition2);
    }
}
