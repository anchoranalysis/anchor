package org.anchoranalysis.io.output.outputter;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.filepath.prefixer.NamedPath;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.filepath.prefixer.PathDifferenceFromBase;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.ExperimentFileFolder;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrefixForInput {

    private static final ManifestFolderDescription MANIFEST_FOLDER_ROOT =
            new ManifestFolderDescription("root", "experiment", new SetSequenceType());
    
    private final FilePathPrefixer prefixer;
    
    private final FilePathPrefixerParams params;
    
    /**
     * The prefix to use for outputs pertaining to a particular file.
     * 
     * @param path the path from which a prefix is derived
     * @param experimentIdentifier
     * @param manifestRecorder
     * @param experimentalManifestRecorder
     * @return
     * @throws FilePathPrefixerException
     */
    public FilePathPrefix prefixForFile(
            NamedPath path,
            String experimentIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder
            )
            throws FilePathPrefixerException {

        // Calculate a prefix from the incoming file, and create a file path generator
        FilePathPrefix prefix = prefixer.outFilePrefix(path, experimentIdentifier, params);

        PathDifferenceFromBase difference =
                differenceFromPrefixer(experimentIdentifier, prefix.getCombinedPrefix());

        experimentalManifestRecorder.ifPresent(recorder -> writeRootFolderInManifest(recorder, difference.combined()));

        manifestRecorder.ifPresent(recorder -> recorder.init(prefix.getFolderPath()));

        return prefix;
    }
    
    private PathDifferenceFromBase differenceFromPrefixer(String expIdentifier, Path combinedPrefix)
            throws FilePathPrefixerException {
        try {
            return PathDifferenceFromBase.differenceFrom(
                    prefixer.rootFolderPrefix(expIdentifier, params)
                            .getCombinedPrefix(),
                    combinedPrefix);
        } catch (AnchorIOException e) {
            throw new FilePathPrefixerException(e);
        }
    }
    
    private static void writeRootFolderInManifest(
            ManifestRecorder manifestRecorderExperiment, Path rootPath) {
        manifestRecorderExperiment
                .getRootFolder()
                .writeFolder(rootPath, MANIFEST_FOLDER_ROOT, new ExperimentFileFolder());
    }
}
