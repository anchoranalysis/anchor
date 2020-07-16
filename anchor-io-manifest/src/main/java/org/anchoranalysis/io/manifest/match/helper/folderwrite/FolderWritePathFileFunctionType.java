/* (C)2020 */
package org.anchoranalysis.io.manifest.match.helper.folderwrite;

import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;
import org.anchoranalysis.io.manifest.match.Match;

public class FolderWritePathFileFunctionType implements Match<FolderWrite> {

    private Match<FolderWrite> delegate;

    public FolderWritePathFileFunctionType(String function, String type) {
        super();

        delegate =
                new FolderWriteManifestMatch(
                        new ManifestDescriptionMatchAnd(
                                new ManifestDescriptionFunctionMatch(function),
                                new ManifestDescriptionTypeMatch(type)));
    }

    @Override
    public boolean matches(FolderWrite obj) {
        return delegate.matches(obj);
    }
}
