/* (C)2020 */
package org.anchoranalysis.core.name.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;

public class NameValueSet<T> implements Iterable<NameValue<T>>, NamedProvider<T> {

    private HashMap<String, NameValue<T>> map = new HashMap<>();

    public NameValueSet() {
        super();
    }

    public NameValueSet(Iterable<? extends NameValue<T>> list) {
        super();

        for (NameValue<T> nmp : list) {
            map.put(nmp.getName(), nmp);
        }
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public Optional<T> getOptional(String key) {
        NameValue<T> item = map.get(key);
        return Optional.ofNullable(item).map(NameValue::getValue);
    }

    @Override
    public Iterator<NameValue<T>> iterator() {
        return map.values().iterator();
    }

    public void add(String name, T value) {
        NameValue<T> item = new SimpleNameValue<>(name, value);
        map.put(name, item);
    }

    public void add(NameValue<T> ni) {
        map.put(ni.getName(), ni);
    }

    public void add(NameValueSet<T> set) {
        for (String key : set.keys()) {
            try {
                add(key, set.getException(key));
            } catch (NamedProviderGetException e) {
                // This should never occur as we always use known key-values
                assert false;
            }
        }
    }

    public void removeIfExists(T item) {
        map.remove(item);
    }

    public int size() {
        return map.size();
    }

    // Maybe this doesn't work too well
    public T getArbitrary() {
        return map.get(map.keySet().iterator().next()).getValue();
    }
}
