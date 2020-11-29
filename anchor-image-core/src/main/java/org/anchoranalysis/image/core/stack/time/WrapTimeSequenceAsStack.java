/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.stack.time;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;

// Always takes t=0 from the time-sequence
public class WrapTimeSequenceAsStack implements NamedProvider<Stack> {

    private static final int TIME_INDEX = 0;

    private NamedProvider<TimeSequence> namedProvider;

    public WrapTimeSequenceAsStack(NamedProvider<TimeSequence> namedProvider) {
        this.namedProvider = namedProvider;
    }

    @Override
    public Optional<Stack> getOptional(String key) throws NamedProviderGetException {
        return namedProvider.getOptional(key).map(prov -> prov.get(TIME_INDEX));
    }

    @Override
    public Set<String> keys() {
        return namedProvider.keys();
    }
}
