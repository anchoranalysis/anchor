/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.finder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;

public abstract class FinderSingleDirectory implements Finder {

    private Optional<MutableDirectory> foundDirectory = Optional.empty();

    @Override
    public final boolean doFind(Manifest manifestRecorder) {

        foundDirectory = findDirectory(manifestRecorder);

        return exists();
    }

    @Override
    public final boolean exists() {
        return foundDirectory.isPresent();
    }

    protected abstract Predicate<MutableDirectory> matchDirectories();

    protected MutableDirectory getFoundDirectory() {
        return foundDirectory.get(); // NOSONAR
    }

    private final Optional<MutableDirectory> findDirectory(Manifest manifestRecorder) {
        List<MutableDirectory> list =
                FinderUtilities.findListDirectory(manifestRecorder, matchDirectories());
        return OptionalUtilities.createFromFlag(!list.isEmpty(), () -> list.get(0));
    }
}
