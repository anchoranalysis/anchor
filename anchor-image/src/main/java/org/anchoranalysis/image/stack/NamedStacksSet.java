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
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.image.extent.ImageDimensions;

/**
 * A set of image-stacks each with a name
 *
 * @author Owen Feehan
 */
public class NamedStacksSet implements NamedProviderStore<Stack> {

    private HashMap<String, StoreSupplier<Stack>> map = new HashMap<>();

    public Optional<StoreSupplier<Stack>> getAsSupplier(String identifier) {
        return Optional.ofNullable(map.get(identifier));
    }

    @Override
    public Optional<Stack> getOptional(String identifier) throws NamedProviderGetException {
        Optional<StoreSupplier<Stack>> ret = getAsSupplier(identifier);
        try {
            return OptionalUtilities.map(ret, StoreSupplier::get);
        } catch (OperationFailedException e) {
            throw NamedProviderGetException.wrap(identifier, e);
        }
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    public void addImageStack(String identifier, Stack inputImage) {
        map.put(identifier, () -> inputImage);
    }

    /** TODO remove in favour of add */
    public void addImageStack(
            String identifier,
            StoreSupplier<Stack> inputImage) {
        map.put(identifier, inputImage);
    }

    @Override
    public void add(String name, StoreSupplier<Stack> supplier) {
        map.put(name, supplier);
    }

    public NamedStacksSet maxIntensityProj() throws OperationFailedException {

        NamedStacksSet out = new NamedStacksSet();

        for (Entry<String, StoreSupplier<Stack>> entry :
                map.entrySet()) {
            Stack projection =
                    entry.getValue().get().maximumIntensityProjection();
            out.addImageStack(entry.getKey(), projection);
        }

        return out;
    }

    /** Applies an operation on each stack in the collection and returns a new derived collection */
    public NamedStacksSet applyOperation(
            ImageDimensions dimensions, UnaryOperator<Stack> stackOperation)
            throws OperationFailedException {

        NamedStacksSet out = new NamedStacksSet();

        try {
            for (String key : keys()) {
                Stack stack = getException(key);

                if (!stack.getDimensions().equals(dimensions)) {
                    throw new OperationFailedException(
                            String.format(
                                    "The image-dimensions of %s (%s) does not match what is expected (%s)",
                                    key, stack.getDimensions(), dimensions));
                }

                out.add(key, () -> stackOperation.apply(stack) );
            }
            return out;

        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    public void addFrom(NamedProvider<Stack> source) {
        addFromWithPrefix(source,"");
    }

    public void addFromWithPrefix(NamedProvider<Stack> source, String prefix) {

        for (String name : source.keys()) {
            addImageStack(prefix + name, () -> {
                try {
                    return source.getException(name);
                } catch (NamedProviderGetException e) {
                    throw new OperationFailedException(e.summarize());
                }
            });
        }
    }

    /**
     * Creates a new collection containing only items whose keys exist in a particular set
     *
     * @return the new collection
     */
    public NamedStacksSet subset(StringSet keyMustBeIn) {
        NamedStacksSet out = new NamedStacksSet();

        for (Entry<String, StoreSupplier<Stack>> entry :
                map.entrySet()) {
            if (keyMustBeIn.contains(entry.getKey())) {
                out.map.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }
}