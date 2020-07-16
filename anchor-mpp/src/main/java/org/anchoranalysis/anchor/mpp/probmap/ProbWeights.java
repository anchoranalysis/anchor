/* (C)2020 */
package org.anchoranalysis.anchor.mpp.probmap;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.random.RandomNumberGenerator;

// A set of weights for a number of items
public class ProbWeights {

    private List<Double> listWeights = new ArrayList<>();
    private List<Double> listWeightsCum = new ArrayList<>();

    public void add(Double e) {

        listWeights.add(e);

        // Update cumulative
        if (listWeightsCum.isEmpty()) {
            // First
            listWeightsCum.add(e);
        } else {
            listWeightsCum.add(listWeightsCum.get(listWeightsCum.size() - 1) + e);
            // Others
        }
    }

    public double getTotal() {
        if (listWeightsCum.isEmpty()) {
            return 0;
        }
        return listWeightsCum.get(listWeightsCum.size() - 1);
    }

    public int sample(RandomNumberGenerator randomNumberGenerator) {

        double tot = getTotal();

        // Our returned value
        double val = randomNumberGenerator.sampleDoubleZeroAndOne() * tot;

        // TODO REPLACE WITH SOMETHING MORE EFFICIENT than looping through the cumulative values
        //   e.g. some kind of balanced binary search tree

        // We iterate forwardly until we find the correct interval
        for (int i = 0; i < listWeightsCum.size(); i++) {
            if (listWeightsCum.get(i) >= val) {
                return i;
            }
        }
        // we shouldn't be able to get here, apart from some perhaps floating point round-up error
        return listWeightsCum.size() - 1;
    }

    public int size() {
        return listWeights.size();
    }
}
