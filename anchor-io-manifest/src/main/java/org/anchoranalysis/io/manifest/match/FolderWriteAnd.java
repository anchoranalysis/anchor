/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class FolderWriteAnd extends MatchAnd<FolderWrite> {

    public FolderWriteAnd() {
        super();
    }

    public FolderWriteAnd(Match<FolderWrite> condition1, Match<FolderWrite> condition2) {
        super(condition1, condition2);
    }
}
