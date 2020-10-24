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
package org.anchoranalysis.io.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.io.manifest.file.FileType;
import io.vavr.control.Either;

/**
 * Concatenates the arrays of {@link FileType} that may be returned from write operations.
 * 
 * @author Owen Feehan
 *
 */
public class ConcatenateFileTypes {
    
    // Tracks the file-types added with a list, but only if there's more than one generator
    // Otherwise it remembers the last added result.
    private Either<List<FileType>,FileType[]> tracking;
    
    /**
     * Creates to always use a list for tracking.
     */
    public ConcatenateFileTypes() {
        tracking = Either.left(new ArrayList<>());
    }
    
    /**
     * Create to use a list for tracking if a flag is true, otherwise to just remember the last result of each call to {@link #add}.
     * 
     * @param supportMultipleCallsToAdd if true, a list is used for tracking (can handle multiple calls to {#add}), otherwise a list is not used, and the last call to {@link #add} is simply remembered.
     */
    public ConcatenateFileTypes(boolean supportMultipleCallsToAdd) {
        // Only create if the list has more than one item, otherwise leave as bull
        tracking = supportMultipleCallsToAdd ? Either.left(new ArrayList<>()) : Either.right(null);
    }
    
    public void add(FileType[] fileTypes) {
        if (tracking.isLeft()) {
            // If we're using the list, then add any file-types to it
            Arrays.stream(fileTypes).forEach(tracking.getLeft()::add);
        } else {
            // If we're not using the list, just remember the last array added
            tracking = Either.right(fileTypes);
        }
    }
    
    public FileType[] allFileTypes() {
        if (tracking.isLeft()) {
            // Convert to an array if the list exists and there's more than 1 item
            // If it exists with 0 items, then Optional.empty() is returned
            return tracking.getLeft().toArray( new FileType[0] );
        } else {
            return tracking.get();
        }
    }
}
