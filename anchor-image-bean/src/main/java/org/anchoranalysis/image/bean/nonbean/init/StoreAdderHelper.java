/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.image.bean.nonbean.init;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;

/**
 * Routines to add beans to a {@link NamedProviderStore}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class StoreAdderHelper {

    /**
     * Adds a single item to a {@link NamedProviderStore} (using a bridge) and explicitly specifying
     * a new name.
     *
     * @param <S> item-type as input
     * @param <T> item-type in the store
     * @param name how the item will be named in the store
     * @param item item to be added
     * @param store container the item is added to (destination)
     * @param bridge bridge applied to item so it matches the type of store
     * @throws OperationFailedException if the operation cannot be completed
     */
    public static <S extends AnchorBean<?>, T> void add(
            String name,
            S item,
            NamedProviderStore<T> store,
            CheckedFunction<S, T, OperationFailedException> bridge)
            throws OperationFailedException {
        store.add(name, () -> bridge.apply(item));
    }

    /**
     * Adds named-items to a {@link NamedProviderStore} (using a bridge) using identical names in
     * the store.
     *
     * <p>Note that the item is duplicated before being added.
     *
     * <p>Only the item is added to the store (not the named-item).
     *
     * @param <S> item-type as input
     * @param <T> item-type in the store
     * @param define source of many beans indexed by class
     * @param defineClass specifies which named-items form {@code define} to use as a source.
     * @param store container the item is added to (destination)
     * @param bridge bridge applied to item so it matches the type of store
     * @throws OperationFailedException if the operation cannot be completed
     */
    public static <S extends AnchorBean<?>, T> void addPreserveName(
            Define define,
            Class<?> defineClass,
            NamedProviderStore<T> store,
            CheckedFunction<S, T, OperationFailedException> bridge)
            throws OperationFailedException {
        addFromListPreserveName(define.listFor(defineClass), store, bridge);
    }

    /**
     * Like {@link #addPreserveName(Define, Class, NamedProviderStore, CheckedFunction)} but uses a
     * {@link List} as the source of beans.
     */
    private static <S extends AnchorBean<?>, T> void addFromListPreserveName(
            List<NamedBean<S>> beans,
            NamedProviderStore<T> store,
            CheckedFunction<S, T, OperationFailedException> bridge)
            throws OperationFailedException {

        for (NamedBean<S> namedBean : beans) {
            StoreAdderHelper.add(
                    namedBean.getName(), namedBean.duplicateBean().getValue(), store, bridge);
        }
    }
}
