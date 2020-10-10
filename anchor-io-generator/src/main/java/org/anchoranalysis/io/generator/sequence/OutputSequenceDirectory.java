package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Directory where the output-sequence is written to, along with associated rules for writing.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class OutputSequenceDirectory {

    private static final int DEFAULT_NUMBER_DIGITS = 6;
    
    /** The name of a sub-directory in the current context to write to, or if empty, files are written into the current directory context. */
    @Getter private final Optional<String> subdirectoryName;
    
    @Getter private final IndexableOutputNameStyle outputNameStyle;
    
    /** Wheter the output should be checked to see if it is allowed or not. */
    @Getter private final boolean selective;
        
    /** A manifest-description for the folder, or if not defined, one will be automatically created. */
    private final Optional<ManifestDescription> folderManifestDescription;
    
    
    /**
     * Create for a particular sub-directory and number of digits.
     * 
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     * 
     * @param subdirectoryName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     */
    public OutputSequenceDirectory(String subdirectoryName) {
        this(subdirectoryName, DEFAULT_NUMBER_DIGITS);
    }
    
    /**
     * Create for a particular sub-directory and prefix.
     * 
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     * 
     * @param subdirectoryName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     */
    public OutputSequenceDirectory(String subdirectoryName, String prefix) {
        this(subdirectoryName, prefix, DEFAULT_NUMBER_DIGITS, false, Optional.empty());
    }
    
    /**
     * Create for a particular sub-directory and number of digits.
     * 
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     * 
     * @param subdirectoryName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     * @param numberDigits the number of digits in the numeric part of the output-name.
     */
    public OutputSequenceDirectory(String subdirectoryName, int numberDigits) {
        this(subdirectoryName, numberDigits, false, Optional.empty());
    }
        
    /**
     * Create for a particular sub-directory and number of digits and subdirectory manifest-description.
     * 
     * <p>The full-name has an increment number appended e.g. <code>$prefix_000000.tif</code> etc.
     * 
     * @param subdirectoryName name of sub-directory to place sequence in (which is also used as the outputName and prefix)
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective
     * @param folderManifestDescription
     */
    public OutputSequenceDirectory(String subdirectoryName, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        this(subdirectoryName, subdirectoryName, numberDigits, selective, folderManifestDescription);
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
    public OutputSequenceDirectory(String subdirectoryName, String prefix, int numberDigits, boolean selective, Optional<ManifestDescription> folderManifestDescription) {
        this.subdirectoryName = Optional.of(subdirectoryName);
        this.outputNameStyle = new IntegerSuffixOutputNameStyle(subdirectoryName, prefix, numberDigits);
        this.selective = selective;
        this.folderManifestDescription = folderManifestDescription;
    }
        
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
