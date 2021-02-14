package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Outputs an element to the file-system.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class ElementOutputter {

    /** The outputter for writing the element. */
    @Getter private OutputterChecked outputter;
    
    /** Supplies a logger for information messages when outputting. */
    private Supplier<Optional<Logger>> logger;

    /**
     * Derives a bound-output-manager for a (possibly newly created) subdirectory of the existing
     * manager
     *
     * @param subdirectoryName the subdirectory-name
     * @param manifestDescription manifest-description for the directory if it exists
     * @param manifestDirectory manifest-folder
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return a bound-output-manager for the subdirectory
     */
    public OutputterChecked deriveSubdirectory(String subdirectoryName,
            ManifestDirectoryDescription manifestDescription,
            Optional<SubdirectoryBase> manifestDirectory, boolean inheritOutputRulesAndRecording) {
        return outputter.deriveSubdirectory(subdirectoryName, manifestDescription,
                manifestDirectory, inheritOutputRulesAndRecording);
    }

    /**
     * Creates a full absolute path that completes the part of the path present in the outputter
     * with an additional suffix.
     *
     * @param suffixWithoutExtension the suffix for the path (without any extension).
     * @param fallbackSuffix if neither a {@code prefix} is defined nor a {@code suffix}, then this
     *     provides a suffix to use so a file isn't only an extension.
     * @return a newly created absolute path, combining directory, prefix (if it exists), suffix and
     *     extension.
     */
    public Path makeOutputPath(Optional<String> suffixWithoutExtension, String extension,
            String fallbackSuffix) {
        return outputter.makeOutputPath(suffixWithoutExtension, extension, fallbackSuffix);
    }

    /**
     * Writes a file-entry to the operation recorder
     *
     * @param outputName output-name
     * @param pathSuffix the final part of the path of the file
     * @param manifestDescription the manifest description
     * @param index an index associated with this item (there may be other items with the same name,
     *     but not the same index)
     */
    public void writeFileToOperationRecorder(String outputName, Path pathSuffix,
            ManifestDescription manifestDescription, String index) {
        outputter.writeFileToOperationRecorder(outputName, pathSuffix, manifestDescription, index);
    }

    /**
     * General settings for outputting.
     * 
     * @return the settings.
     */
    public OutputWriteSettings getSettings() {
        return outputter.getSettings();
    }
    
    public Optional<ImageFileFormat> getSuggestedFormatToWrite() {
        return outputter.getContext().getSuggestedFormatToWrite();
    }

    /** Which outputs are enabled or not enabled. */
    public MultiLevelOutputEnabled getOutputsEnabled() {
        return outputter.getOutputsEnabled();
    }
    
    public Optional<Logger> logger() {
        return logger.get();
    }
}
