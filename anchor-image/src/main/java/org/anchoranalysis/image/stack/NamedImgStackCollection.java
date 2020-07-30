/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.stack;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.extent.ImageDimensions;

// A collection of Image Stacks each with a name
public class NamedImgStackCollection implements NamedProviderStore<Stack> {

    private HashMap<String, OperationWithProgressReporter<Stack, OperationFailedException>> map;

    public NamedImgStackCollection() {
        map = new HashMap<>();
    }

    public Optional<OperationWithProgressReporter<Stack, OperationFailedException>> getAsOperation(
            String identifier) {
        return Optional.ofNullable(map.get(identifier));
    }

    @Override
    public Optional<Stack> getOptional(String identifier) throws NamedProviderGetException {
        Optional<OperationWithProgressReporter<Stack, OperationFailedException>> ret =
                getAsOperation(identifier);
        try {
            return OptionalUtilities.map(ret, op -> op.doOperation(ProgressReporterNull.get()));
        } catch (OperationFailedException e) {
            throw NamedProviderGetException.wrap(identifier, e);
        }
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    public void addImageStack(String identifier, Stack inputImage) {
        map.put(identifier, new IdentityOperationWithProgressReporter<>(inputImage));
    }

    public void addImageStack(
            String identifier,
            OperationWithProgressReporter<Stack, OperationFailedException> inputImage) {
        map.put(identifier, inputImage);
    }

    @Override
    public void add(String name, Operation<Stack, OperationFailedException> getter)
            throws OperationFailedException {
        map.put(name, progressReporter -> getter.doOperation());
    }

    public NamedImgStackCollection maxIntensityProj() throws OperationFailedException {

        NamedImgStackCollection out = new NamedImgStackCollection();

        for (Entry<String, OperationWithProgressReporter<Stack, OperationFailedException>> entry :
                map.entrySet()) {
            Stack projection =
                    entry.getValue().doOperation(ProgressReporterNull.get()).maximumIntensityProjection();
            out.addImageStack(entry.getKey(), projection);
        }

        return out;
    }

    /** Applies an operation on each stack in the collection and returns a new derived collection */
    public NamedImgStackCollection applyOperation(
            ImageDimensions dimensions, UnaryOperator<Stack> stackOperation)
            throws OperationFailedException {

        NamedImgStackCollection out = new NamedImgStackCollection();

        try {
            for (String key : keys()) {
                Stack img = getException(key);

                if (!img.getDimensions().equals(dimensions)) {
                    throw new OperationFailedException(
                            String.format(
                                    "The image-dimensions of %s (%s) does not match what is expected (%s)",
                                    key, img.getDimensions(), dimensions));
                }

                out.add(key, new IdentityOperation<>(stackOperation.apply(img)));
            }
            return out;

        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    public void addFrom(NamedProvider<Stack> src) {

        for (String name : src.keys()) {
            addImageStack(name, new OperationStack(src, name));
        }
    }

    public void addFromWithPrefix(final NamedProvider<Stack> src, String prefix) {

        for (final String name : src.keys()) {
            addImageStack(prefix + name, new OperationStack(src, name));
        }
    }

    private static class OperationStack
            implements OperationWithProgressReporter<Stack, OperationFailedException> {

        private NamedProvider<Stack> src;
        private String name;

        public OperationStack(NamedProvider<Stack> src, String name) {
            super();
            this.src = src;
            this.name = name;
        }

        @Override
        public Stack doOperation(ProgressReporter progressReporter)
                throws OperationFailedException {
            try {
                return src.getException(name);
            } catch (NamedProviderGetException e) {
                throw new OperationFailedException(e.summarize());
            }
        }
    }
}
