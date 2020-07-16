/*-
 * #%L
 * anchor-mpp-sgmn
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
/* (C)2020 */
package org.anchoranalysis.mpp.sgmn;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.test.TestLoader;
import org.junit.Before;
import org.junit.Test;

public class DefineMPPTest {

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @Before
    public void setUp() {
        RegisterBeanFactories.registerAllPackageBeanFactories();
    }

    @Test
    public void testStatic() throws BeanXmlException {
        checkPath("defineStatic.xml");
    }

    @Test
    public void testDynamic() throws BeanXmlException {
        checkPath("defineDynamic.xml");
    }

    private void checkPath(String fileName) throws BeanXmlException {
        Path path = loader.resolveTestPath(fileName);
        Define define = BeanXmlLoader.loadBean(path);
        checkDefine(define);
    }

    private void checkDefine(Define define) {
        // We assume an order of chnl1 before chnl2
        assertTwoElements(define, ChnlProvider.class, "chnl");

        // We assume an order of stack1 before stack2
        assertTwoElements(define, StackProvider.class, "stack");
    }

    private void assertTwoElements(Define define, Class<?> provider, String prefix) {
        List<NamedBean<AnchorBean<?>>> list = define.getList(provider);
        assertTrue(list.size() == 2);
        assertElement(list, 0, prefix + "1");
        assertElement(list, 1, prefix + "2");
    }

    private void assertElement(
            List<NamedBean<AnchorBean<?>>> list, int index, String expectedName) {
        assertTrue(list.get(index).getName().equals(expectedName));
    }
}
