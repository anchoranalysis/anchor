/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;

public class FileWriteManifestMatch implements Match<FileWrite> {

    private Match<ManifestDescription> matchManifestDescription;

    public FileWriteManifestMatch(Match<ManifestDescription> matchManifestDescription) {
        super();
        this.matchManifestDescription = matchManifestDescription;
    }

    @Override
    public boolean matches(FileWrite obj) {
        ManifestDescription md = obj.getManifestDescription();
        if (md == null) {
            return false;
        }
        return matchManifestDescription.matches(md);
    }
}
