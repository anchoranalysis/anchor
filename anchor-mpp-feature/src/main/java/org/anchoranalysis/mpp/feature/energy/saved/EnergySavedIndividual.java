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

package org.anchoranalysis.mpp.feature.energy.saved;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.mpp.feature.energy.EnergyTotal;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * Stores and manages individual energy values for a collection of marks.
 *
 * <p>This class implements {@link Serializable} for persistence and {@link Iterable} for easy iteration over energy values.</p>
 */
public class EnergySavedIndividual implements Serializable, Iterable<EnergyTotal> {

    private static final long serialVersionUID = -5205697546115728135L;

    /** The total energy of all individual marks. */
    @Getter @Setter private double energyTotal;

    /** List of individual energy totals for each mark. */
    private ArrayList<EnergyTotal> ind;

    /**
     * Creates a shallow copy of this instance.
     *
     * @return a new {@link EnergySavedIndividual} with a shallow copy of the energy totals
     */
    public EnergySavedIndividual shallowCopy() {

        EnergySavedIndividual out = new EnergySavedIndividual();

        // Copy each item directly
        out.ind = new ArrayList<>();
        this.ind.stream().forEach(out.ind::add);
        out.energyTotal = this.energyTotal;
        return out;
    }

    /**
     * Creates a deep copy of this instance.
     *
     * @return a new {@link EnergySavedIndividual} with a deep copy of the energy totals
     */
    public EnergySavedIndividual deepCopy() {

        EnergySavedIndividual out = new EnergySavedIndividual();

        // Copy each item directly
        out.ind = new ArrayList<>();
        ind.forEach(indComponent -> out.ind.add(indComponent.deepCopy()));
        out.energyTotal = this.energyTotal;
        return out;
    }

    /**
     * Gets the number of individual energy totals stored.
     *
     * @return the number of energy totals
     */
    public int size() {
        return ind.size();
    }

    /**
     * Gets the energy total at a specific index.
     *
     * @param index the index of the energy total to retrieve
     * @return the {@link EnergyTotal} at the specified index
     */
    public EnergyTotal get(int index) {
        return this.ind.get(index);
    }

    /**
     * Resets the list of individual energy totals.
     */
    public void resetInd() {
        this.ind = new ArrayList<>();
    }

    /**
     * Adds a new energy total to the list and updates the total energy.
     *
     * @param item the {@link EnergyTotal} to add
     */
    public void add(EnergyTotal item) {
        this.ind.add(item);
        this.energyTotal += item.getTotal();
    }

    /**
     * Removes an energy total at a specific index and updates the total energy.
     *
     * @param index the index of the energy total to remove
     */
    public void rmv(int index) {
        this.energyTotal -= this.ind.get(index).getTotal();
        this.ind.remove(index);
    }

    /**
     * Exchanges an energy total at a specific index with a new one and updates the total energy.
     *
     * @param index the index of the energy total to exchange
     * @param item the new {@link EnergyTotal} to replace the existing one
     */
    public void exchange(int index, EnergyTotal item) {

        // we make the necessary changes on the individual components
        this.energyTotal -= get(index).getTotal();
        this.energyTotal += item.getTotal();
        this.ind.set(index, item);
    }

    /**
     * Generates a string description of the marks and their associated energies.
     *
     * @param marks the {@link MarkCollection} containing the marks
     * @return a string representation of the marks and their energies
     */
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

    /**
     * Asserts that the total energy is valid (not NaN).
     */
    public void assertValid() {
        assert !Double.isNaN(energyTotal);
    }

    @Override
    public Iterator<EnergyTotal> iterator() {
        return ind.iterator();
    }
}