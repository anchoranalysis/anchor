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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.functional.checked.CheckedIntFunction;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Factory for creating {@link FeatureList} in different ways.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureListFactory {

    /**
     * Creates an empty list of features
     *
     * @param <T> input-type of feature(s) in list
     * @return a newly created empty list
     */
    public static <T extends FeatureInput> FeatureList<T> empty() {
        return new FeatureList<>();
    }

    /**
     * Creates a list for one or more features
     *
     * @param <T> input-type of feature(s) in list
     * @param feature the feature(s)
     * @return a newly-created list with each feature included in identical order to the argument
     */
    @SafeVarargs
    public static <T extends FeatureInput> FeatureList<T> from(Feature<T>... feature) {
        return new FeatureList<>(Arrays.stream(feature));
    }

    /**
     * Creates a list for a single-feature created from a provider
     *
     * @param <T> input-type of feature(s) in list
     * @param featureProvider provides the single feature
     * @return a newly-created list
     * @throws ProvisionFailedException
     */
    public static <T extends FeatureInput> FeatureList<T> fromProvider(
            Provider<Feature<T>> featureProvider) throws ProvisionFailedException {
        return from(featureProvider.get());
    }

    /**
     * Creates a list of features from an iterable
     *
     * @param <T> input-type of feature(s) in list
     * @param iterable the iterable
     * @return a newly created list
     */
    public static <T extends FeatureInput> FeatureList<T> fromIterable(
            Iterable<Feature<T>> iterable) {
        return new FeatureList<>(StreamSupport.stream(iterable.spliterator(), false));
    }

    /**
     * Creates a list of features from a stream
     *
     * @param <T> input-type of feature(s) in list
     * @param stream the stream
     * @return a newly created list
     */
    public static <T extends FeatureInput> FeatureList<T> fromStream(Stream<Feature<T>> stream) {
        return new FeatureList<>(stream);
    }

    /**
     * Creates a list of features from a collection of feature-providers
     *
     * @param <T> input-type of feature(s) in list
     * @param providers the providers, each of which provides a single feature
     * @return a newly created list
     * @throws ProvisionFailedException if an exception is occurring creating from the provider
     */
    public static <T extends FeatureInput> FeatureList<T> fromProviders(
            Collection<FeatureProvider<T>> providers) throws ProvisionFailedException {
        return new FeatureList<>(
                CheckedStream.map(
                        providers.stream(), ProvisionFailedException.class, FeatureProvider::get));
    }

    /**
     * Wraps an existing list, reusing the list in the internals of the {@link FeatureList}
     *
     * @param <T> input-type of feature(s) in list
     * @param list the list to wrap
     * @return a newly created feature-list, using the list argument internally as its data
     *     container
     */
    public static <T extends FeatureInput> FeatureList<T> wrapReuse(List<Feature<T>> list) {
        return new FeatureList<>(list);
    }

    /**
     * Wraps an existing list, WITHOUT reusing the list in the internals of the {@link FeatureList}
     * i.e. create a new list internally
     *
     * @param <T> input-type of feature(s) in list
     * @param list the list to wrap
     * @return a newly-created feature-list, using the same features as the {@code list} argument
     *     but not the same list data structure.
     */
    public static <T extends FeatureInput> FeatureList<T> wrapDuplicate(List<Feature<T>> list) {
        return new FeatureList<>(list.stream());
    }

    /**
     * Creates a new feature-list by mapping an {@link Iterable} to {@link Feature}
     *
     * @param  <S> type that will be mapped to {@link Feature}
     * @param  <T> feature input-type for the result of the mapping
     * @param  <E> exception-type that can be thrown during mapping
     * @param iterable source of entities to be mapped
     * @param mapFunc function for mapping
     * @return a newly created feature-list, with the result (in order) of the mapping of each iterm
     *     in {@link Iterable}
     * @throws E exception if it occurs during mapping
     */
    public static <S, T extends FeatureInput, E extends Exception> FeatureList<T> mapFrom(
            Iterable<S> iterable, CheckedFunction<S, Feature<T>, E> mapFunc) throws E {
        FeatureList<T> out = new FeatureList<>();
        for (S item : iterable) {
            out.add(mapFunc.apply(item));
        }
        return out;
    }

    /**
     * Creates a new feature-list by filtering an iterable and then mapping it to a feature
     *
     * @param  <S> type that will be filtered and then mapped to a feature
     * @param  <T> feature input-type for the result of the mapping
     * @param  <E> exception-type that can be thrown during mapping
     * @param iterable source of entities to be mapped
     * @param predicate only items in {@code iterable} that fulfill this condition will be mapped
     * @param mapFunc function for mapping
     * @return a newly created feature-list, with the result (in order) of the mapping of each item
     *     in {@code iterable}
     * @throws E exception if it occurs during mapping
     */
    public static <S, T extends FeatureInput, E extends Exception> FeatureList<T> mapFromFiltered(
            Iterable<S> iterable, Predicate<S> predicate, CheckedFunction<S, Feature<T>, E> mapFunc)
            throws E {
        FeatureList<T> out = new FeatureList<>();
        for (S item : iterable) {
            if (predicate.test(item)) {
                out.add(mapFunc.apply(item));
            }
        }
        return out;
    }

    /**
     * Creates a new feature-list by flat-mapping an iterable to an optional feature
     *
     * <p>Any {@link Optional#empty} results are not included.
     *
     * @param  <S> type that will be mapped to an optional feature.
     * @param  <T> feature input-type for the result of the mapping
     * @param  <E> exception-type that can be thrown during mapping
     * @param iterable source of entities to be mapped
     * @param flatMapFunc function for mapping
     * @return a newly created feature-list, with the result (in order) of the mapping of each item
     *     in {@code iterable}
     * @throws E exception if it occurs during mapping
     */
    public static <S, T extends FeatureInput, E extends Exception>
            FeatureList<T> flatMapFromOptional(
                    Iterable<S> iterable,
                    CheckedFunction<S, Optional<FeatureList<T>>, E> flatMapFunc)
                    throws E {
        FeatureList<T> out = new FeatureList<>();
        for (S item : iterable) {
            flatMapFunc.apply(item).ifPresent(out::addAll);
        }
        return out;
    }

    /**
     * Creates a new feature-list by mapping integers (from a range) each to a feature
     *
     * @param  <T> feature input-type for the result of the mapping
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
     * @param mapFunc function for mapping
     * @return a newly created feature-list
     */
    public static <T extends FeatureInput> FeatureList<T> mapFromRange(
            int startInclusive, int endExclusive, IntFunction<Feature<T>> mapFunc) {
        return new FeatureList<>(IntStream.range(startInclusive, endExclusive).mapToObj(mapFunc));
    }

    /**
     * Creates a new feature-list by mapping integers (from a range) each to an optional feature
     *
     * @param  <T> feature input-type for the result of the mapping
     * @param  <E> an exception that be thrown during mapping
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
     * @param throwableClass the class of {@code E}
     * @param mapFunc function for mapping
     * @return a newly created feature-list
     * @throws E if the exception is thrown during mapping
     */
    public static <T extends FeatureInput, E extends Exception> FeatureList<T> mapFromRangeOptional(
            int startInclusive,
            int endExclusive,
            Class<? extends Exception> throwableClass,
            CheckedIntFunction<Optional<Feature<T>>, E> mapFunc)
            throws E {
        FeatureList<T> out = new FeatureList<>();
        CheckedStream.mapIntStream(
                        IntStream.range(startInclusive, endExclusive), throwableClass, mapFunc)
                .forEach(opt -> opt.ifPresent(out::add));
        return out;
    }
}
