/*-
 * #%L
 * anchor-io-generator
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
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
    public ManifestDescription createDirectoryDescription(FileType[] fileTypes) {
        return folderManifestDescription.orElseGet( () -> guessDirectoryDescriptionFromFiles(fileTypes) );
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
    private static ManifestDescription guessDirectoryDescriptionFromFiles(FileType[] fileTypes) {
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
