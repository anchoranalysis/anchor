/* (C)2020 */
package org.anchoranalysis.io.manifest.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.Match;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FinderUtilities {

    public static List<FileWrite> findListFile(
            ManifestRecorder manifestRecorder, Match<FileWrite> match) {

        ArrayList<FileWrite> foundList = new ArrayList<>();
        manifestRecorder.getRootFolder().findFile(foundList, match, false);
        return foundList;
    }

    public static List<FolderWrite> findListFolder(
            ManifestRecorder manifestRecorder, Match<FolderWrite> match) {

        ArrayList<FolderWrite> foundList = new ArrayList<>();
        manifestRecorder.getRootFolder().findFolder(foundList, match);
        return foundList;
    }

    public static Optional<FileWrite> findSingleItem(
            ManifestRecorder manifestRecorder, Match<FileWrite> match)
            throws MultipleFilesException {

        List<FileWrite> files = findListFile(manifestRecorder, match);
        if (files.isEmpty()) {
            return Optional.empty();
        }
        if (files.size() > 1) {
            throw new MultipleFilesException(
                    "More than one matching object was found in the manifest");
        }

        return Optional.of(files.get(0));
    }
}
