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

package org.anchoranalysis.feature.session.strategy.replace.bind;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculate.CacheCreator;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.creator.CacheCreatorSimple;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * Attaches a replacement-strategy to a session lazily (i.e. when it is needed)
 *
 * <p>This is because as the relevant parameters are not available when we need to call the
 * constructor</op>.
 */
public class BoundReplaceStrategy<T extends FeatureInput, S extends ReplaceStrategy<T>> {

    private Optional<S> strategy = Optional.empty();

    private Function<CacheCreator, S> funcCreateStrategy;

    public BoundReplaceStrategy(Function<CacheCreator, S> funcCreateStrategy) {
        super();
        this.funcCreateStrategy = funcCreateStrategy;
    }

    public ReplaceStrategy<T> bind(
            FeatureList<T> featureList,
            FeatureInitParams featureInitParams,
            SharedFeatureMulti sharedFeatures,
            Logger logger) {
        CacheCreator cacheCreator =
                new CacheCreatorSimple(featureList, sharedFeatures, featureInitParams, logger);
        if (!strategy.isPresent()) {
            strategy = Optional.of(funcCreateStrategy.apply(cacheCreator));
        }
        return strategy.get();
    }

    public Optional<S> getStrategy() {
        return strategy;
    }
}
