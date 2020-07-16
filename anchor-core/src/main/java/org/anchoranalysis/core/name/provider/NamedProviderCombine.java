/* (C)2020 */
package org.anchoranalysis.core.name.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NamedProviderCombine<T> implements NamedProvider<T> {

    private List<NamedProvider<T>> list = new ArrayList<>();

    @Override
    public Optional<T> getOptional(String key) throws NamedProviderGetException {

        for (NamedProvider<T> item : list) {

            Optional<T> ret = item.getOptional(key);

            if (ret.isPresent()) {
                return ret;
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<String> keys() {
        return list.stream()
                .flatMap(item -> item.keys().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public void add(NamedProvider<T> key) {
        list.add(key);
    }
}
