/* (C)2020 */
package org.anchoranalysis.io.manifest.match.helper.filewrite;

import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteAnd;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;
import org.anchoranalysis.io.manifest.match.Match;

public class FileWriteFileFunctionTypeOutputName implements Match<FileWrite> {

    private Match<FileWrite> delegate;

    public FileWriteFileFunctionTypeOutputName(String function, String type, String outputName) {
        super();

        delegate =
                new FileWriteAnd(
                        new FileWriteOutputName(outputName),
                        new FileWriteManifestMatch(
                                new ManifestDescriptionMatchAnd(
                                        new ManifestDescriptionFunctionMatch(function),
                                        new ManifestDescriptionTypeMatch(type))));
    }

    @Override
    public boolean matches(FileWrite obj) {
        return delegate.matches(obj);
    }
}
