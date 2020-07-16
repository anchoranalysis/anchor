/* (C)2020 */
package org.anchoranalysis.bean.xml;

import static org.junit.Assert.*;

import java.nio.file.Path;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
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
