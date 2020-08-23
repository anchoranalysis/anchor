/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.anchor.mpp.feature.energy.saved;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.feature.energy.EnergyTotal;
import lombok.Getter;
import lombok.Setter;

public class EnergySavedInd implements Serializable, Iterable<EnergyTotal> {

    /** */
    private static final long serialVersionUID = -5205697546115728135L;

    // Pairwise total
    @Getter @Setter private double energyTotal;

    private ArrayList<EnergyTotal> ind;

    public EnergySavedInd shallowCopy() {

        EnergySavedInd out = new EnergySavedInd();

        // Copy each item directly
        out.ind = new ArrayList<>();
        this.ind.stream().forEach(out.ind::add);
        out.energyTotal = this.energyTotal;
        return out;
    }

    public EnergySavedInd deepCopy() {

        EnergySavedInd out = new EnergySavedInd();

        // Copy each item directly
        out.ind = new ArrayList<>();
        ind.forEach(indComponent -> out.ind.add(indComponent.deepCopy()));
        out.energyTotal = this.energyTotal;
        return out;
    }

    public int size() {
        return ind.size();
    }

    public EnergyTotal get(int index) {
        return this.ind.get(index);
    }

    public void resetInd() {
        this.ind = new ArrayList<>();
    }

    public void add(EnergyTotal item) {
        this.ind.add(item);
        this.energyTotal += item.getTotal();
    }

    public void rmv(int index) {
        // We calculate it's individual contribution
        this.energyTotal -= this.ind.get(index).getTotal();
        this.ind.remove(index);
    }

    public void exchange(int index, EnergyTotal item) {

        // we make the necessary changes on the individual components
        this.energyTotal -= get(index).getTotal();
        this.energyTotal += item.getTotal();
        this.ind.set(index, item);
    }

    public String describeMarks(MarkCollection marks) {

        String newLine = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder("{");
        s.append(newLine);

        // We loop through each item and write out the configuration AND its associated individual
        // energy
        int i = 0;
        Iterator<EnergyTotal> iteratorEnergy = ind.iterator();
        for (Iterator<Mark> iteratorMark = marks.iterator(); iteratorMark.hasNext(); ) {
            assert iteratorEnergy.hasNext();
            s.append(
                    String.format(
                            "%d. %s    energy=%e%n",
                            i++, iteratorMark.next().toString(), iteratorEnergy.next().getTotal()));
        }

        s.append("}");
        s.append(newLine);

        return s.toString();
    }

    public void assertValid() {
        assert !Double.isNaN(energyTotal);
    }

    @Override
    public Iterator<EnergyTotal> iterator() {
        return ind.iterator();
    }
}
