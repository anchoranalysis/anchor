package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * How the output of the sequence looks on the filesystem and in the manifest.
 * 
 * <p>This involves several aspects:
 * 
 * <ul>
 * <li>An optional sub-directory where the output-sequence is written to, or else the current output-directory (both relative to the input-output-context).
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
public class OutputPattern {
    
    /** The name of a sub-directory in the current context to write to, or if empty, files are written into the current directory context. */
    @Getter private final Optional<String> subdirectoryName;
    
    @Getter private final IndexableOutputNameStyle outputNameStyle;
    
    /** Wheter the output should be checked to see if it is allowed or not. */
    @Getter private final boolean selective;
        
    /** A manifest-description for the folder, or if not defined, one will be automatically created. */
    private final Optional<ManifestDescription> folderManifestDescription;
        
    // Requires the generator to be in a valid state
    public ManifestDescription createFolderDescription(FileType[] fileTypes) {
        return folderManifestDescription.orElseGet( () -> guessFolderDescriptionFromFiles(fileTypes) );
    }

    // Attempts to come up with a description of the folder from the underlying file template
    //  If there is only one filetype, we use it
    //  If there is more than one, we look for bits which are the same
    private static ManifestDescription guessFolderDescriptionFromFiles(FileType[] fileTypes) {

        assert (fileTypes.length > 0);

        if (fileTypes.length == 1) {
            return fileTypes[0].getManifestDescription();
        }

        String function = "";
        String type = "";
        boolean first = true;
        for (FileType fileType : fileTypes) {
            String functionItr = fileType.getManifestDescription().getFunction();
            String typeItr = fileType.getManifestDescription().getType();

            if (first) {
                // FIRST ITERATION
                function = functionItr;
                type = typeItr;
                first = false;
            } else {
                // SUBSEQUENT ITERATIONS
                if (!functionItr.equals(function)) {
                    function = "combined";
                }

                if (!typeItr.equals(type)) {
                    type = "combined";
                }
            }
        }

        return new ManifestDescription(type, function);
    }
}
