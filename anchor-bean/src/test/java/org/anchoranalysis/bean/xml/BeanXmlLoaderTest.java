/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.anchoranalysis.bean.xml.exception.BeanXmlException;
import org.anchoranalysis.bean.xml.mock.MockBeanNested;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BeanXmlLoaderTest {

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @Test
    void testLoadBean() throws BeanXmlException {
        testSimpleAndBean("nestedBean");
    }

    @Test
    void testLoadBeanInclude() throws BeanXmlException {
        testSimpleAndBean("nestedBeanInclude");
    }

    /**
     * Loads XML that replaces a filepath for an include with a different filepath
     *
     * @throws BeanXmlException
     */
    @Test
    void testLoadBeanReplaceAttribute() throws BeanXmlException {
        MockBeanNested bean = registerLoad("replaceBeanAttribute");
        assertEquals("helloChanged", bean.getFieldSimpleNecessary());
    }

    /**
     * Loads XML that replaces a bean with another bean
     *
     * @throws BeanXmlException
     */
    @Test
    public void testLoadBeanReplaceElement() throws BeanXmlException {
        testBean("replaceBeanElement", "world2");
    }

    /**
     * Loads XML that replaces a bean with another bean
     *
     * @throws BeanXmlException
     */
    @Test
    void testLoadBeanReplaceInclude() throws BeanXmlException {
        testBean("replaceBeanInclude", "worldAlternative");
    }

    /**
     * A replace bean targeting a missing attribute
     */
    @Test
    void testLoadBeanReplaceAttributeMissing() {
        assertException(() ->
            testSimple("replaceBeanAttributeMissing", "helloChanged")
        );
    }

    /**
     * A replace bean targettng a missing attribute element
     */
    @Test
    void testLoadBeanReplaceElementMissing() {
        assertException(() ->
            testBean("replaceBeanElementMissing", "world2")
        );
    }
    
    private void testSimple(String fileIdentifier, String expectedFieldValue) throws BeanXmlException {
        MockBeanNested bean = registerLoad(fileIdentifier);
        assertEquals(expectedFieldValue, bean.getFieldSimpleNecessary() );
    }
    
    private void testBean(String fileIdentifier, String expectedMessage) throws BeanXmlException {
        MockBeanNested bean = registerLoad(fileIdentifier);
        assertEquals(expectedMessage, bean.getFieldBeanNecessary().getMessage());
    }
    
    private void testSimpleAndBean(String fileIdentifier) throws BeanXmlException {
        MockBeanNested bean = registerLoad(fileIdentifier);
        assertEquals("hello", bean.getFieldSimpleNecessary());
        assertEquals("world", bean.getFieldBeanNecessary().getMessage());
    }
    
    private <T> T registerLoad(String fileIdentifier) throws BeanXmlException {
        RegisterBeanFactories.registerAllPackageBeanFactories();

        Path path =
                NonImageFileFormat.XML.buildPath(
                        loader.resolveTestPath("org.anchoranalysis.bean.xml"), fileIdentifier);
        T bean = BeanXMLLoader.loadBean(path);
        return bean;
    }
    
    private static void assertException(Executable executable) {
        assertThrows(BeanXmlException.class, executable);
    }
}
