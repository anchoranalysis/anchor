package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * How the output of the sequence looks on the filesystem and in the manifest.
 * 
 * <p>This involves several aspects:
 * 
 * <ul>
 * <li>An optional subdirectory where the output-sequence is written to, or else the current output-directory (both relative to the input-output-context).
 * <li>A naming style (possibly including an index in the filename, and a prefix.suffix) for each file created in that directory.
 * <li>An associated output-name that is used as an identifer in forming rules on what outputs are enabled or not.
 * <li>Whether to enforce these rules or not, on what outputs are enabled.
 * <li>A description to associate with the subdirectory (if it is created) in the manifest.
 * </ul> 
 * along with associated rules for writing.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public abstract class OutputPattern {
    
    /** Uses when a mixed set of manifest-functions or types appear in the same folder. */
    private static final String COMBINED_IDENTIFIER = "combined";
    
    /** The name of a subdirectory in the current context to write to, or if empty, files are written into the current directory context. */
    @Getter private final Optional<String> subdirectoryName;
    
    @Getter private final IndexableOutputNameStyle outputNameStyle;
    
    /** Whether the output should be checked to see if it is allowed or not. */
    @Getter private final boolean selective;
        
    /** A manifest-description for the folder, or if not defined, one will be automatically created. */
    private final Optional<ManifestDescription> folderManifestDescription;
    
    /**
     * Derives a manifest-folder description from the file-types used.
     * <p>Requires the generator to be in a valid state.
     * 
     * @param fileTypes
     * @return
     */
    public ManifestDescription createFolderDescription(FileType[] fileTypes) {
        return folderManifestDescription.orElseGet( () -> guessFolderDescriptionFromFiles(fileTypes) );
    }
    
    /**
     * Attempts to come up with a description of the folder from the underlying file template.
     *
     * <p>If there is only one filetype, we use it.
     * 
     * <p>If there is more than one, we look for bits which are the same.
     * 
     * @param fileTypes
     * @return
     */
    private static ManifestDescription guessFolderDescriptionFromFiles(FileType[] fileTypes) {
        Preconditions.checkArgument(fileTypes.length > 0);

        if (fileTypes.length == 1) {
            return fileTypes[0].getManifestDescription();
        }

        ManifestDescription description = null;
        
        for (FileType fileType : fileTypes) {

            if (description==null) {
                // First iteration
                description = fileType.getManifestDescription();
            } else {
                // Subsequent iteration
                if (!fileType.getManifestDescription().getFunction().equals(description.getFunction())) {
                    description = description.assignFunction(COMBINED_IDENTIFIER);
                }

                if (!fileType.getManifestDescription().getType().equals(description.getType())) {
                    description = description.assignType(COMBINED_IDENTIFIER);
                }
            }
        }

        return description;
    }
}
