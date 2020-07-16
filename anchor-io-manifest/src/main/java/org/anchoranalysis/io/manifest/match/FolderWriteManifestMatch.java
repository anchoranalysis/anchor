/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class FolderWriteManifestMatch implements Match<FolderWrite> {

    private Match<ManifestDescription> manifestDescriptionMatch;
    private Optional<Match<SequenceType>> sequenceTypeMatch;

    // If match is null, we match everything
    public FolderWriteManifestMatch(Match<ManifestDescription> manifestDescriptionMatch) {
        super();
        this.manifestDescriptionMatch = manifestDescriptionMatch;
        this.sequenceTypeMatch = Optional.empty();
    }

    @Override
    public boolean matches(FolderWrite obj) {

        if (obj.getManifestFolderDescription() == null) {
            return false;
        }

        if (!manifestDescriptionMatch.matches(
                obj.getManifestFolderDescription().getFileDescription())) {
            return false;
        }

        return !(sequenceTypeMatch.isPresent()
                && !sequenceTypeMatch
                        .get()
                        .matches(obj.getManifestFolderDescription().getSequenceType()));
    }
}
