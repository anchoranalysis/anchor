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

package org.anchoranalysis.feature.session.strategy.replace;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.SessionInputSequential;
import org.anchoranalysis.feature.session.strategy.child.DefaultFindChildStrategy;
import org.anchoranalysis.feature.session.strategy.child.FindChildStrategy;

/** Always re-use a singleton SessionInput, invalidating it each time a new call occurs */
public class ReuseSingletonStrategy<T extends FeatureInput> implements ReplaceStrategy<T> {

    private Optional<SessionInputSequential<T>> sessionInput = Optional.empty();

    /** Means to create the session-input */
    private Function<T, SessionInputSequential<T>> createSessionInput;

    /**
     * Constructor with default means of creating a session-input
     *
     * @param cacheCreator
     */
    public ReuseSingletonStrategy(CacheCreator cacheCreator) {
        this(cacheCreator, DefaultFindChildStrategy.instance());
    }

    /**
     * Constructor with custom means of creating a session-input
     *
     * @param cacheCreator
     */
    public ReuseSingletonStrategy(CacheCreator cacheCreator, FindChildStrategy findChildStrategy) {
        super();
        this.createSessionInput =
                input -> new SessionInputSequential<T>(input, cacheCreator, findChildStrategy);
    }

    @Override
    public SessionInput<T> createOrReuse(T input) throws CreateException {

        if (input == null) {
            throw new CreateException("The input may not be null");
        }

        if (sessionInput.isPresent()) {
            sessionInput.get().replaceInput(input);
        } else {
            sessionInput = Optional.of(createSessionInput.apply(input));
        }

        return sessionInput.get();
    }
}
