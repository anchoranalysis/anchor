/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import java.util.HashMap;
import java.util.Iterator;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * A set of NRGSchemes each with a name.
 *
 * <p>SharedFeatures and a CachedCalculationList are also associated
 *
 * @author Owen Feehan
 */
public class NamedNRGSchemeSet implements Iterable<SimpleNameValue<NRGScheme>> {

    private HashMap<String, SimpleNameValue<NRGScheme>> delegate = new HashMap<>();
    private SharedFeatureMulti sharedFeatures;

    public NamedNRGSchemeSet(SharedFeatureMulti sharedFeatures) {
        super();
        this.sharedFeatures = sharedFeatures;
    }

    public SharedFeatureMulti getSharedFeatures() {
        return sharedFeatures;
    }

    public void setSharedFeatures(SharedFeatureMulti sharedFeatures) {
        this.sharedFeatures = sharedFeatures;
    }

    public boolean add(String name, NRGScheme nrgScheme) {
        delegate.put(name, new SimpleNameValue<>(name, nrgScheme));
        return true;
    }

    public SimpleNameValue<NRGScheme> get(String name) {
        return delegate.get(name);
    }

    @Override
    public Iterator<SimpleNameValue<NRGScheme>> iterator() {
        return delegate.values().iterator();
    }
}
