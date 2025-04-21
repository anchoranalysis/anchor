/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.replace;

import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.HorizontalCacheCreator;
import org.anchoranalysis.feature.shared.SharedFeatures;

/**
 * Attaches a replacement-strategy to a session lazily.
 *
 * <p>i.e. the strategy is attached only when first needed.
 *
 * <p>This is because as the relevant parameters are not available when we need to call the
 * constructor.
 *
 * @param <T> feature-input type
 * @param <S> strategy-type
 */
@RequiredArgsConstructor
public class BoundReplaceStrategy<T extends FeatureInput, S extends ReplaceStrategy<T>> {

    // START REQUIRED ARGUMENTS
    /** Creates a strategy corresponding to a particular cache. */
    private final Function<CacheCreator, S> createStrategy;

    // END REQUIRED ARGUMENTS

    /** The strategy that has been bound (if it exists). */
    @Getter private Optional<S> strategy = Optional.empty();

    /**
     * Creates a {@link ReplaceStrategy} for a particular {@link CacheCreator}, or reuses any
     * previously-created {@link ReplaceStrategy} if it exists.
     *
     * @param features the features to use in the {@link CacheCreator}, if created.
     * @param initialization the initialization for the {@link CacheCreator}, if created.
     * @param sharedFeatures the shared-features for the {@link CacheCreator}, if created.
     * @param logger the logger.
     * @return a {@link ReplaceStrategy}, reused if it already exists, otherwise newly created.
     */
    public ReplaceStrategy<T> createOrReuse(
            FeatureList<T> features,
            FeatureInitialization initialization,
            SharedFeatures sharedFeatures,
            Logger logger) {
        if (!strategy.isPresent()) {
            CacheCreator cacheCreator =
                    new HorizontalCacheCreator(features, sharedFeatures, initialization, logger);
            strategy = Optional.of(createStrategy.apply(cacheCreator));
        }
        return strategy.get();
    }
}
