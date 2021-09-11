/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.bean.xml.primitive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.bean.primitive.DoubleList;
import org.anchoranalysis.bean.primitive.DoubleSet;
import org.anchoranalysis.bean.primitive.IntegerList;
import org.anchoranalysis.bean.primitive.IntegerSet;
import org.anchoranalysis.bean.primitive.PrimitiveBeanCollection;
import org.anchoranalysis.bean.primitive.StringList;
import org.anchoranalysis.bean.primitive.StringSet;
import org.anchoranalysis.bean.xml.LoadFromResources;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.junit.jupiter.api.Test;

/**
 * Tests loading primitive types via beans.
 *
 * @author Owen Feehan
 */
class BeanXMLPrimitiveTester {

    private LoadFromResources loader = new LoadFromResources("primitive");

    @Test
    void testStringSet() throws BeanXMLException {
        // The last element is empty just to check handling of empty strings
        StringSet set = new StringSet("a", "b", "");
        testPrimitive("stringSet", set);
    }

    @Test
    void testStringList() throws BeanXMLException {
        // The last element is empty just to check handling of empty strings
        StringList set = new StringList("a", "b", "");
        testPrimitive("stringList", set);
    }

    @Test
    void testIntegerSet() throws BeanXMLException {
        IntegerSet set = new IntegerSet(8, 1, -4);
        testPrimitive("integerSet", set);
        assertException("integerSetInvalid");
    }

    @Test
    void testIntegerList() throws BeanXMLException {
        IntegerList set = new IntegerList(8, 1, -4);
        testPrimitive("integerList", set);
        assertException("integerListInvalid");
    }

    @Test
    void testDoubleSet() throws BeanXMLException {
        DoubleSet set = new DoubleSet(8.1, 1.2, -4.3e7);
        testPrimitive("doubleSet", set);
        assertException("doubleSetInvalid");
    }

    @Test
    void testDoubleList() throws BeanXMLException {
        DoubleList set = new DoubleList(8.1, 1.2, -4.3e7);
        testPrimitive("doubleList", set);
        assertException("doubleListInvalid");
    }

    /** Tests the loading of a collection of primitives or {@link String}. */
    private <T> void testPrimitive(
            String fileIdentifier, PrimitiveBeanCollection<T> expectedPrimitiveCollection)
            throws BeanXMLException {
        PrimitiveBeanCollection<T> primitiveCollection = loader.loadBean(fileIdentifier);
        assertEquals(expectedPrimitiveCollection, primitiveCollection);
    }

    private void assertException(String fileIdentifier) {
        assertThrows(BeanXMLException.class, () -> loader.loadBean(fileIdentifier));
    }
}
