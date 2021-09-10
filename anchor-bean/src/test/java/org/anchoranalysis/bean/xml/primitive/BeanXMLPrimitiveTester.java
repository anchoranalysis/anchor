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
import org.anchoranalysis.bean.xml.exception.BeanXmlException;
import org.junit.jupiter.api.Test;

/**
 * Tests loading primitive types via beans.
 *
 * @author Owen Feehan
 */
class BeanXMLPrimitiveTester {

    private LoadFromResources loader = new LoadFromResources("primitive");

    @Test
    void testStringSet() throws BeanXmlException {
        // The last element is empty just to check handling of empty strings
        StringSet set = new StringSet("a", "b", "");
        testPrimitive("stringSet", set);
    }

    @Test
    void testStringList() throws BeanXmlException {
        // The last element is empty just to check handling of empty strings
        StringList set = new StringList("a", "b", "");
        testPrimitive("stringList", set);
    }

    @Test
    void testIntegerSet() throws BeanXmlException {
        IntegerSet set = new IntegerSet(8, 1, -4);
        testPrimitive("integerSet", set);
        assertException("integerSetInvalid");
    }

    @Test
    void testIntegerList() throws BeanXmlException {
        IntegerList set = new IntegerList(8, 1, -4);
        testPrimitive("integerList", set);
        assertException("integerListInvalid");
    }

    @Test
    void testDoubleSet() throws BeanXmlException {
        DoubleSet set = new DoubleSet(8.1, 1.2, -4.3e7);
        testPrimitive("doubleSet", set);
        assertException("doubleSetInvalid");
    }

    @Test
    void testDoubleList() throws BeanXmlException {
        DoubleList set = new DoubleList(8.1, 1.2, -4.3e7);
        testPrimitive("doubleList", set);
        assertException("doubleListInvalid");
    }

    /** Tests the loading of a collection of primitives or {@link String}. */
    private <T> void testPrimitive(
            String fileIdentifier, PrimitiveBeanCollection<T> expectedPrimitiveCollection)
            throws BeanXmlException {
        PrimitiveBeanCollection<T> primitiveCollection = loader.loadBean(fileIdentifier);
        assertEquals(expectedPrimitiveCollection, primitiveCollection);
    }

    private void assertException(String fileIdentifier) {
        assertThrows(BeanXmlException.class, () -> loader.loadBean(fileIdentifier));
    }
}
