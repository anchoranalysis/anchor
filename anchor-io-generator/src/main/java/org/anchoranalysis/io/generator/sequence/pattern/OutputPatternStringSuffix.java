package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.namestyle.StringSuffixOutputNameStyle;

/**
 * A {@link OutputPattern} that outputs a file-name with a trailing string index.
 * 
 * @author Owen Feehan
 *
 */
public class OutputPatternStringSuffix extends OutputPattern {

    /**
     * Creates <i>without</i> a prefix.
     * 
     * @param outputName the output-name to use, which also determines the subdirectory name if it is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the outputs occur in the parent directory.
     */
    public OutputPatternStringSuffix(String outputName, boolean suppressSubdirectory) {
        this(outputName, suppressSubdirectory, Optional.empty());
    }

    /**
     * Creates <i>with</i> a prefix.
     * 
     * @param outputName the output-name to use, which also determines the subdirectory name if it is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the outputs occur in the parent directory.
     * @param prefix a string that appears before the index in each outputted filename
     */
    public OutputPatternStringSuffix(String outputName, boolean suppressSubdirectory, String prefix) {
        this(outputName, suppressSubdirectory, Optional.of(prefix));
    }
    
    private OutputPatternStringSuffix(String outputName, boolean suppressSubdirectory, Optional<String> prefix) {
        super(
           OutputPatternUtilities.maybeSubdirectory(outputName, suppressSubdirectory),
           new StringSuffixOutputNameStyle(outputName, prefix),
           true,
           Optional.empty()
        );
    }
}
