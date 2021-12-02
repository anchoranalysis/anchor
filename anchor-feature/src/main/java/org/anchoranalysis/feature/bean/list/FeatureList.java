/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.bean.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * A list of features with the same input-type.
 *
 * @see FeatureListFactory for the preferred means of creating instances.
 * @author Owen Feehan
 * @param <T> input type of features contained in the list
 */
public class FeatureList<T extends FeatureInput> extends AnchorBean<FeatureList<T>>
        implements Iterable<Feature<T>> {

    // START BEAN PARAMETERS
    /** The list of features. */
    @BeanField @Getter @Setter private List<Feature<T>> list;
    // END BEAN PARAMETERS

    /** Creates with an empty list. */
    public FeatureList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a list from a stream.
     *
     * @param stream the stream of features.
     */
    public FeatureList(Stream<Feature<T>> stream) {
        this.list = stream.collect(Collectors.toList());
    }

    /**
     * Wraps an existing list.
     *
     * @param list the list to wrap.
     */
    public FeatureList(List<Feature<T>> list) {
        this.list = list;
    }

    /**
     * Initializes all features in the list, and recursively any features they contain.
     *
     * <p>A feature must be <i>initialized</i> before being used for calculation.
     *
     * @param initialization the initialization parameters.
     * @param logger the logger.
     * @throws InitializeException if any feature cannot be successfully initialized.
     */
    public void initializeRecursive(FeatureInitialization initialization, Logger logger)
            throws InitializeException {
        for (Feature<T> feature : list) {
            feature.initializeRecursive(initialization, logger);
        }
    }

    /**
     * Creates a new feature-list where each feature is the result of applying a map-function to an
     * existing feature.
     *
     * @param <S> input-type of feature to be created as result of mapping
     * @param <E> exception that can be thrown during mapping
     * @param mapFunc function to perform the mapping of each item.
     * @return a newly created feature-list (with the same number of items) containing the mapped
     *     features.
     * @throws E if the mapping-function throws this exception.
     */
    public <S extends FeatureInput, E extends Exception> FeatureList<S> map(
            CheckedFunction<Feature<T>, Feature<S>, E> mapFunc) throws E {
        FeatureList<S> out = new FeatureList<>();
        for (Feature<T> feature : list) {
            out.add(mapFunc.apply(feature));
        }
        return out;
    }

    /**
     * Filters inputs and then performs a {@link #map}.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param <S> input-type of feature to be created as result of mapping
     * @param <E> exception that can be thrown during mapping
     * @param mappingFunction the function that performs the mapping.
     * @param predicate iff true object is included, otherwise excluded.
     * @return a newly created feature-list, a filtered version of all features, then mapped.
     * @throws E if the mapping-function throws this exception.
     */
    public <S extends FeatureInput, E extends Exception> FeatureList<S> filterAndMap(
            Predicate<Feature<T>> predicate,
            CheckedFunction<Feature<T>, Feature<S>, E> mappingFunction)
            throws E {
        FeatureList<S> out = new FeatureList<>();
        for (Feature<T> feature : list) {
            if (predicate.test(feature)) {
                out.add(mappingFunction.apply(feature));
            }
        }
        return out;
    }

    /**
     * Appends the features one or more (optional) feature-lists to the existing list.
     *
     * <p>This is an <b>immutable</b> operation and the existing list is not altered.
     *
     * @param featureList the optional feature-lists to append.
     * @return a newly-created list with all the existing features, as well as any optional
     *     additional features.
     */
    public FeatureList<T> append(Optional<FeatureList<T>> featureList) {
        FeatureList<T> out = new FeatureList<>();
        out.addAll(this);
        featureList.ifPresent(out::addAll);
        return out;
    }

    /**
     * Derives the names of the features.
     *
     * @return the names.
     */
    public FeatureNameList deriveNames() {
        return new FeatureNameList(list.stream().map(Feature::getFriendlyName));
    }

    /**
     * Creates a new feature-list sorted in a particular order.
     *
     * @param comparator used to determine the order of elements when sorting.
     * @return a newly-created list with the same elements in sorted order.
     */
    public FeatureList<T> sort(Comparator<Feature<T>> comparator) {
        // Creates a duplicate list, and sorts the items in place.
        FeatureList<T> out = shallowDuplicate();
        Collections.sort(out.asList(), comparator);
        return out;
    }

    /**
     * Creates a new feature-list which contains identical elements.
     *
     * @return a newly-created list with the same elements in the same order.
     */
    public FeatureList<T> shallowDuplicate() {
        return new FeatureList<>(this.list.stream());
    }

    /**
     * Adds a feature to the current list.
     *
     * @param feature the feature to add.
     */
    @SuppressWarnings("unchecked")
    public void add(Feature<? extends T> feature) {
        list.add((Feature<T>) feature);
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<Feature<T>> iterator() {
        return list.iterator();
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list.
     */
    public int size() {
        return list.size();
    }

    /**
     * Exposes the underlying list of features in this data-structure.
     *
     * <p>Changing this list, will change the current instance.
     *
     * @return the internal list used to store features.
     */
    public List<Feature<T>> asList() {
        return list;
    }

    @Override
    public String describeBean() {
        return String.format("%s with %d items", super.describeBean(), list.size());
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the index.
     * @return the feature at position {@code index} in the list.
     */
    public Feature<T> get(int index) {
        return list.get(index);
    }

    /**
     * Removes all of the elements from this list.
     *
     * <p>The list will be empty after this call returns.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Add all the features in {@code other} to the current list.
     *
     * @param toAdd the features to add.
     */
    public void addAll(FeatureList<? extends T> toAdd) {
        for (Feature<? extends T> feature : toAdd) {
            add(feature);
        }
    }
}
