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

package org.anchoranalysis.mpp.sgmn;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.BeanXMLLoader;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.image.bean.provider.ChannelProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefineTest {

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @BeforeEach
    void setUp() {
        RegisterBeanFactories.registerAllPackageBeanFactories();
    }

    @Test
    void testStatic() throws BeanXMLException {
        checkPath("defineStatic.xml");
    }

    @Test
    void testDynamic() throws BeanXMLException {
        checkPath("defineDynamic.xml");
    }

    private void checkPath(String fileName) throws BeanXMLException {
        Path path = loader.resolveTestPath(fileName);
        Define define = BeanXMLLoader.loadBean(path);
        checkDefine(define);
    }

    private void checkDefine(Define define) {
        // We assume an order of channel1 before channel2
        assertTwoElements(define, ChannelProvider.class, "channel");

        // We assume an order of stack1 before stack2
        assertTwoElements(define, StackProvider.class, "stack");
    }

    private void assertTwoElements(Define define, Class<?> provider, String prefix) {
        List<NamedBean<AnchorBean<?>>> list = define.listFor(provider);
        assertEquals(2, list.size());
        assertElement(list, 0, prefix + "1");
        assertElement(list, 1, prefix + "2");
    }

    private void assertElement(
            List<NamedBean<AnchorBean<?>>> list, int index, String expectedName) {
        assertEquals(expectedName, list.get(index).getName());
    }
}
