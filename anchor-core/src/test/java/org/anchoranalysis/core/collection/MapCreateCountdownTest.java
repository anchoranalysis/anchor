/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MapCreateCountdown}.
 *
 * @author Owen Feehan
 */
class MapCreateCountdownTest {

    /** The first key added. */
    private static final String KEY1 = "key1";

    /** The second key added. */
    private static final String KEY2 = "key2";

    /** A constant string used for all the values. */
    private static final String VALUE = "value";

    /** Mock used for processor. */
    @SuppressWarnings("unchecked")
    private CheckedConsumer<String, OperationFailedException> processor =
            (CheckedConsumer<String, OperationFailedException>) mock(CheckedConsumer.class);

    /** Mock used for clean-up. */
    @SuppressWarnings("unchecked")
    private CheckedBiConsumer<String, String, OperationFailedException> cleanUp =
            (CheckedBiConsumer<String, String, OperationFailedException>)
                    mock(CheckedBiConsumer.class);

    /** Map used for testing. */
    private MapCreateCountdown<String, String> map = new MapCreateCountdown<>(() -> VALUE, cleanUp);

    /** Tests all the main operations of the map together. */
    @Test
    void testManyOperations() throws OperationFailedException {

        // Key 1 should be removed after two calls
        map.increment(KEY1);
        map.increment(KEY1);

        // Key 2 should be removed after one call
        map.increment(KEY2);

        decrement(KEY1);

        assertKeys(KEY1, KEY2);

        decrement(KEY1);
        verifyProcessor(2);
        verifyCleanUp(KEY1);

        assertKeys(KEY2);

        decrement(KEY2);

        assertKeys();
        verifyCleanUp(KEY2);
    }

    /** Processes an element in the map and decrements the count. */
    private void decrement(String key) throws OperationFailedException {
        map.processElementDecrement(key, processor);
    }

    /** Assert the map has particular keys. */
    private void assertKeys(String... keys) {
        assertEquals(set(keys), map.keySet());
    }

    /** Verify that the processor mock was called {@code numberTimes}. */
    private void verifyProcessor(int numberTimes) throws OperationFailedException {
        verify(processor, times(numberTimes)).accept(VALUE);
    }

    /** Verify that the cleanUp mock was called for a particular {@code key}. */
    private void verifyCleanUp(String key) throws OperationFailedException {
        verify(cleanUp, times(1)).accept(key, VALUE);
    }

    /** Builds a set of {@link Strings} from each argument. */
    private static Set<String> set(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
