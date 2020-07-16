/* (C)2020 */
package org.anchoranalysis.io.manifest.match.helper.filewrite;

import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;
import org.anchoranalysis.io.manifest.match.Match;

public class FileWriteFileFunctionType implements Match<FileWrite> {

    private Match<FileWrite> delegate;

    public FileWriteFileFunctionType(String function, String type) {
        super();

        delegate =
                new FileWriteManifestMatch(
                        new ManifestDescriptionMatchAnd(
                                new ManifestDescriptionFunctionMatch(function),
                                new ManifestDescriptionTypeMatch(type)));
    }

    @Override
    public boolean matches(FileWrite obj) {
        return delegate.matches(obj);
    }
}
