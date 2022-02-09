/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.output.outputter;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options that influence how an output directory is created.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class DirectoryCreationParameters {

    /**
     * When true, this will delete any existing directory with the same path, and then create it
     * anew.
     *
     * <p>When false, an exception is thrown if an existing directory with the same path already
     * exists.
     */
    private final boolean deleteExistingDirectory;

    /**
     * When defined, this {@code consumer} is called when the directory is first created, as it is
     * created lazily only when first needed.
     *
     * <p>It is called with the path of the directory as an argument.
     */
    private final Optional<Consumer<Path>> callUponDirectoryCreation;

    /**
     * Creates to <i>not</i> delete directories, and with no consumer called upon directory
     * creation.
     */
    public DirectoryCreationParameters() {
        this.deleteExistingDirectory = false;
        this.callUponDirectoryCreation = Optional.empty();
    }
}
