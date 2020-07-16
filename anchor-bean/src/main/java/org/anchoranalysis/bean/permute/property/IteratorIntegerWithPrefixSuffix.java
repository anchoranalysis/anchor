/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;

class IteratorIntegerWithPrefixSuffix implements Iterator<String> {
    private String prefix;
    private String suffix;
    private Iterator<Integer> delegate;

    public IteratorIntegerWithPrefixSuffix(
            Iterator<Integer> delegate, String prefix, String suffix) {
        super();
        this.prefix = prefix;
        this.suffix = suffix;
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public String next() {
        int outInt = delegate.next();
        return prefix + outInt + suffix;
    }

    @Override
    public void remove() {
        delegate.remove();
    }
}
