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

import static org.junit.Assert.*;

import java.nio.file.Path;
import org.anchoranalysis.bean.xml.exception.BeanXmlException;
import org.anchoranalysis.bean.xml.mock.MockBeanNested;
import org.anchoranalysis.test.TestLoader;
import org.junit.Test;

public class BeanXmlLoaderTest {

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @Test
    public void testLoadBean() throws BeanXmlException {
        MockBeanNested bean = registerLoad("nestedBean");

        assertTrue(bean.getFieldSimpleNecessary().equals("hello"));
        assertTrue(bean.getFieldBeanNecessary().getMessage().equals("world"));
    }

    @Test
    public void testLoadBeanInclude() throws BeanXmlException {
        MockBeanNested bean = registerLoad("nestedBeanInclude");

        assertTrue(bean.getFieldSimpleNecessary().equals("hello"));
        assertTrue(bean.getFieldBeanNecessary().getMessage().equals("world"));
    }

    /**
     * Loads XML that replaces a filepath for an include with a different filepath
     *
     * @throws BeanXmlException
     */
    @Test
    public void testLoadBeanReplaceAttribute() throws BeanXmlException {
        MockBeanNested bean = registerLoad("replaceBeanAttribute");

        assertTrue(bean.getFieldSimpleNecessary().equals("helloChanged"));
    }

    /**
     * Loads XML that replaces a bean with another bean
     *
     * @throws BeanXmlException
     */
    @Test
    public void testLoadBeanReplaceElement() throws BeanXmlException {
        MockBeanNested bean = registerLoad("replaceBeanElement");

        assertTrue(bean.getFieldBeanNecessary().getMessage().equals("world2"));
    }

    /**
     * Loads XML that replaces a bean with another bean
     *
     * @throws BeanXmlException
     */
    @Test
    public void testLoadBeanReplaceInclude() throws BeanXmlException {
        MockBeanNested bean = registerLoad("replaceBeanInclude");

        assertTrue(bean.getFieldBeanNecessary().getMessage().equals("worldAlternative"));
    }

    private <T> T registerLoad(String fileId) throws BeanXmlException {
        RegisterBeanFactories.registerAllPackageBeanFactories();

        Path path =
                loader.resolveTestPath(String.format("org.anchoranalysis.bean.xml/%s.xml", fileId));
        T bean = BeanXmlLoader.loadBean(path);
        return bean;
    }

    /**
     * A replace bean targeting a missing attribute
     *
     * @throws BeanXmlException
     */
    @Test(expected = Exception.class)
    public void testLoadBeanReplaceAttributeMissing() throws BeanXmlException {
        MockBeanNested bean = registerLoad("replaceBeanAttributeMissing");

        assertTrue(bean.getFieldSimpleNecessary().equals("helloChanged"));
    }

    /**
     * A replace bean targettng a missing attribute element
     *
     * @throws BeanXmlException
     */
    @Test(expected = Exception.class)
    public void testLoadBeanReplaceElementMissing() throws BeanXmlException {
        MockBeanNested bean = registerLoad("replaceBeanElementMissing");

        assertTrue(bean.getFieldBeanNecessary().getMessage().equals("world2"));
    }
}
