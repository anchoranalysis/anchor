/* (C)2020 */
package org.anchoranalysis.core.random;

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

public class RandomNumberGeneratorMersenne implements RandomNumberGenerator {

    private RandomEngine randomEngine;

    public RandomNumberGeneratorMersenne(boolean fixedSeed) {
        randomEngine = createTwister(fixedSeed);
    }

    @Override
    public double sampleDoubleZeroAndOne() {
        return randomEngine.nextDouble();
    }

    @Override
    public Poisson generatePoisson(double param) {
        return new Poisson(param, randomEngine);
    }

    @Override
    public Normal generateNormal(double mean, double standardDeviation) {
        return new Normal(mean, standardDeviation, randomEngine);
    }

    private static RandomEngine createTwister(boolean fixedSeed) {
        if (fixedSeed) {
            return new MersenneTwister();
        } else {
            return new MersenneTwister(new java.util.Date());
        }
    }
}
