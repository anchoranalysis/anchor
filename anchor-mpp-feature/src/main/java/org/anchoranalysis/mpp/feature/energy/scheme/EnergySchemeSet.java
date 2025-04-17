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

package org.anchoranalysis.mpp.feature.energy.scheme;

import java.util.HashMap;
import java.util.Iterator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.identifier.name.SimpleNameValue;
import org.anchoranalysis.feature.shared.SharedFeatures;

/**
 * A set of {@link EnergyScheme}s, each associated with a unique name.
 *
 * <p>{@link SharedFeatures} are also associated with this set.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class EnergySchemeSet implements Iterable<SimpleNameValue<EnergyScheme>> {

    // START REQUIRED ARGUMENTS
    /** Shared features associated with all energy schemes in the set. */
    @Getter private final SharedFeatures sharedFeatures;
    // END REQUIRED ARGUMENTS

    /** Internal storage for named energy schemes. */
    private HashMap<String, SimpleNameValue<EnergyScheme>> delegate = new HashMap<>();

    /**
     * Adds a new {@link EnergyScheme} to the set with the given name.
     *
     * @param name the unique name for the energy scheme
     * @param energyScheme the {@link EnergyScheme} to add
     * @return true if the energy scheme was successfully added
     */
    public boolean add(String name, EnergyScheme energyScheme) {
        delegate.put(name, new SimpleNameValue<>(name, energyScheme));
        return true;
    }

    /**
     * Retrieves an {@link EnergyScheme} by its name.
     *
     * @param name the name of the energy scheme to retrieve
     * @return the {@link SimpleNameValue} containing the named {@link EnergyScheme}, or null if not
     *     found
     */
    public SimpleNameValue<EnergyScheme> get(String name) {
        return delegate.get(name);
    }

    @Override
    public Iterator<SimpleNameValue<EnergyScheme>> iterator() {
        return delegate.values().iterator();
    }
}
