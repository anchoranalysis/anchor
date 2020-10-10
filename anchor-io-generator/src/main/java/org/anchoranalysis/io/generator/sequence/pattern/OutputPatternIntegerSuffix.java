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
     * Create for a particular sub-directory and number of digits.
     * 
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     * 
     * @param outputName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     * @param selective whether to check output-names against the rules or not. If not, all outputs will occur permissively.
     */
    public OutputPatternIntegerSuffix(String outputName, boolean selective) {
        this(outputName, DEFAULT_NUMBER_DIGITS, selective, Optional.empty());
    }
    
    /**
     * Create for a particular sub-directory and prefix.
     * 
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     * 
     * @param outputName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     */
    public OutputPatternIntegerSuffix(String outputName, String prefix) {
        this(outputName, Optional.of(outputName), prefix, DEFAULT_NUMBER_DIGITS, true, Optional.empty());
    }
    
    /**
     * Create for a particular sub-directory and number of digits and subdirectory manifest-description.
     * 
     * <p>The full-name has an increment number appended e.g. <code>$prefix_000000.tif</code> etc.
     * 
     * @param outputName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs will occur permissively.
     * @param folderManifestDescription
     */
    public OutputPatternIntegerSuffix(String outputName, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        this(outputName, Optional.of(outputName), outputName, numberDigits, selective, folderManifestDescription);
    }
    
    /**
     * 
     * <p>The full-name has an increment number appended e.g. <code>$prefix_000000.tif</code> etc.
     * 
     * @param subdirectoryName
     * @param prefix prefix on each file-name.
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective
     * @param folderManifestDescription
     */
    public OutputPatternIntegerSuffix(String outputName, Optional<String> subdirectoryName, String prefix, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        super( subdirectoryName,
                new IntegerSuffixOutputNameStyle(outputName, prefix, numberDigits),
                selective,
                folderManifestDescription );
    }
}
