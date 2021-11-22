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

package org.anchoranalysis.image.core.stack.named;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.anchoranalysis.bean.primitive.StringSet;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * A set of image-stacks each with a name.
 *
 * @author Owen Feehan
 */
public class NamedStacks implements NamedProviderStore<Stack> {

    /**
     * A map of identifiers to a supplier of the {@link Stack}.
     *
     * <p>This allows for delayed (lazy-evaluation) of the stacks, as some stacks may never be used.
     */
    private HashMap<String, StoreSupplier<Stack>> map = new HashMap<>();

    /**
     * Add a stack.
     *
     * @param identifier a unique name for the stack.
     * @param stack the stack to add.
     */
    public void add(String identifier, Stack stack) {
        map.put(identifier, () -> stack);
    }

    @Override
    public void add(String identifier, StoreSupplier<Stack> supplier) {
        map.put(identifier, supplier);
    }

    @Override
    public Optional<Stack> getOptional(String identifier) throws NamedProviderGetException {
        Optional<StoreSupplier<Stack>> supplier = Optional.ofNullable(map.get(identifier));
        try {
            return OptionalUtilities.map(supplier, StoreSupplier::get);
        } catch (OperationFailedException e) {
            throw new NamedProviderGetException(identifier, e);
        }
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    /**
     * Applies an operation on each {@link Stack} in the collection and returns a new derived
     * collection.
     *
     * @param operation the operation to apply to each stack.
     * @param dimensions if set, a check occurs that all {@link Stack}s have identical dimensions to
     *     this.
     * @return a new {@link NamedStacks} with identical identifiers, but with each identifier
     *     mapping to the result of applying {@code operation} on the existing stack.
     * @throws OperationFailedException if any of the dimensions do not match {@code dimensions} (if
     *     set) or if an error occurs when retrieving a stack.
     */
    public NamedStacks applyOperation(
            UnaryOperator<Stack> operation, Optional<Dimensions> dimensions)
            throws OperationFailedException {

        NamedStacks out = new NamedStacks();

        try {
            for (String key : keys()) {
                Stack stack = getException(key);

                if (dimensions.isPresent() && !stack.dimensions().equals(dimensions.get())) {
                    throw new OperationFailedException(
                            String.format(
                                    "The image-dimensions of %s (%s) does not match what is expected (%s)",
                                    key, stack.dimensions(), dimensions.get()));
                }

                out.add(key, () -> operation.apply(stack));
            }
            return out;

        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    /**
     * Adds a {@link Stack} from a {@link NamedProvider}.
     *
     * @param source where to retrieve the stack and associated unique name.
     */
    public void addFrom(NamedProvider<Stack> source) {
        addFromWithPrefix(source, "");
    }

    /**
     * Like {@link #addFrom(NamedProvider)} but additionally adds a prefix to the name when adding.
     *
     * @param source where to retrieve the stack and associated unique name.
     * @param prefix the prefix to place before the name.
     */
    public void addFromWithPrefix(NamedProvider<Stack> source, String prefix) {

        for (String name : source.keys()) {
            add(
                    prefix + name,
                    () -> {
                        try {
                            return source.getException(name);
                        } catch (NamedProviderGetException e) {
                            throw new OperationFailedException(e.summarize());
                        }
                    });
        }
    }

    /**
     * Creates a new collection containing only items whose keys exist in a particular set.
     *
     * @param identifiersToInclude only stacks whose identifiers exist in this set are included in
     *     the new {@link NamedStacks}.
     * @return the new {@link NamedStacks}.
     */
    public NamedStacks subset(StringSet identifiersToInclude) {
        NamedStacks out = new NamedStacks();

        for (Entry<String, StoreSupplier<Stack>> entry : map.entrySet()) {
            if (identifiersToInclude.contains(entry.getKey())) {
                out.map.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    /**
     * Iterates over each entry in the map.
     *
     * @param <E> an exception that may be called by {@code consumer}.
     * @param consumer this consumer is called the name and stack-supplier for each entry in the
     *     map.
     * @throws E if the consumer throws the exception.
     */
    public <E extends Exception> void forEach(
            CheckedBiConsumer<String, StoreSupplier<Stack>, E> consumer) throws E {
        FunctionalIterate.iterateMap(map, consumer);
    }

    /**
     * Number of stacks.
     *
     * @return the number of stacks
     */
    public int size() {
        return map.size();
    }
}
