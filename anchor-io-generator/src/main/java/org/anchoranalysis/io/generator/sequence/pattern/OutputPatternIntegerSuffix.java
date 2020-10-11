package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;

/**
 * A {@link OutputPattern} that outputs a file-name with a trailing integer index.
 * 
 * @author Owen Feehan
 *
 */
public class OutputPatternIntegerSuffix extends OutputPattern {

    private static final int DEFAULT_NUMBER_DIGITS = 6;
    
    /**
     * Creates in a subdirectory, using the outputName as both the subdirectory name and the prefix.
     * 
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     * 
     * <p>The full-name has an increment number appended e.g. <code>$outputName/$outputName_000000.tif</code> etc.
     * 
     * @param outputName name of subdirectory to place sequence in (which is also used as the outputName and prefix)
     * @param selective whether to check output-names against the rules or not. If not, all outputs will occur permissively.
     */
    public OutputPatternIntegerSuffix(String outputName, boolean selective) {
        this(outputName, DEFAULT_NUMBER_DIGITS, selective, Optional.empty());
    }
    
    /**
     * Creates in a subdirectory, using the output-name as the subdirectory name, and a specific prefix.
     * 
     * <p>The full-name has an increment number appended e.g. <code>$outputName/$prefix_000000.tif</code> etc.
     * 
     * @param outputName name of subdirectory to place sequence in (which is also used as the outputName and prefix)
     * @param prefix a string that appears before the index in each outputted filename, with an underscore separating the prefix from the numeric index
     */
    public OutputPatternIntegerSuffix(String outputName, String prefix) {
        this(outputName, false, prefix, DEFAULT_NUMBER_DIGITS, true, Optional.empty());
    }
    
    /**
     * Like {@link #OutputPatternIntegerSuffix(String, boolean)} but with additional options on the manifest-folder description and whether to check outputs against rules. 
     * 
     * @param outputName name of subdirectory to place sequence in (which is also used as the outputName and prefix)
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs will occur permissively.
     * @param folderManifestDescription a description of the folder in the manifest
     */
    public OutputPatternIntegerSuffix(String outputName, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        this(outputName, false, outputName, numberDigits, selective, folderManifestDescription);
    }
    
    /**
     * Like {@link #OutputPatternIntegerSuffix(String, String)} but with additional options about the number of digits, the manifest-folder description and whether to check outputs against rules.
     * 
     * @param outputName name of subdirectory to place sequence in (which is also used as the outputName and prefix)
     * @param prefix a string that appears before the index in each outputted filename, with an underscore separating the prefix from the numeric index
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs will occur permissively.
     * @param folderManifestDescription a description of the folder in the manifest
     */
    public OutputPatternIntegerSuffix(String outputName, String prefix, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        super( Optional.of(outputName),
                new IntegerSuffixOutputNameStyle(outputName, prefix, numberDigits),
                selective,
                folderManifestDescription );
    }
    
    /**
     * Creates with a full set of flexibility about how the filename appears and whether a subdirectory is used.
     * 
     * @param outputName the output-name to use, which also determines the subdirectory name if it is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the outputs occur in the parent directory.
     * @param prefix a string that appears before the index in each outputted filename, with an underscore separating the prefix from the numeric index
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs will occur permissively.
     * @param folderManifestDescription a description of the folder in the manifest
     */
    public OutputPatternIntegerSuffix(String outputName, boolean suppressSubdirectory, String prefix, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        super( OutputPatternUtilities.maybeSubdirectory(outputName, suppressSubdirectory),
                new IntegerSuffixOutputNameStyle(outputName, prefix, numberDigits),
                selective,
                folderManifestDescription );
    }
}
