/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.replace;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureCalcException;
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
     * @param createSessionInput
     */
    public ReuseSingletonStrategy(CacheCreator cacheCreator) {
        this(cacheCreator, DefaultFindChildStrategy.instance());
    }

    /**
     * Constructor with custom means of creating a session-input
     *
     * @param createSessionInput
     */
    public ReuseSingletonStrategy(CacheCreator cacheCreator, FindChildStrategy findChildStrategy) {
        super();
        this.createSessionInput =
                input -> new SessionInputSequential<T>(input, cacheCreator, findChildStrategy);
    }

    @Override
    public SessionInput<T> createOrReuse(T input) throws FeatureCalcException {

        if (input == null) {
            throw new FeatureCalcException("The input may not be null");
        }

        if (sessionInput.isPresent()) {
            sessionInput.get().replaceInput(input);
        } else {
            sessionInput = Optional.of(createSessionInput.apply(input));
        }

        return sessionInput.get();
    }
}
