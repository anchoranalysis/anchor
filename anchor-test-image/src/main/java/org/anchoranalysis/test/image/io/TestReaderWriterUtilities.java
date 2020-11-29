/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.io.bioformats.bean.BioformatsReader;
import org.anchoranalysis.io.bioformats.bean.options.ForceTimeSeriesToStack;
import org.anchoranalysis.io.bioformats.bean.writer.Tiff;
import org.apache.commons.configuration.beanutils.BeanHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestReaderWriterUtilities {

    static {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
    }

    public static void ensureStackReader() {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
        addIfMissing(StackReader.class, createReader());
    }

    public static void ensureStackWriter() {
        addIfMissing(StackWriter.class, defaultStackWriterForTests());
    }

    private static StackReader createReader() {
        return new BioformatsReader(new ForceTimeSeriesToStack());
    }

    private static AnchorDefaultBeanFactory getOrCreateBeanFactory() {
        if (!RegisterBeanFactories.isCalledRegisterAllPackage()) {
            return RegisterBeanFactories.registerAllPackageBeanFactories();
        } else {
            return (AnchorDefaultBeanFactory) BeanHelper.getDefaultBeanFactory();
        }
    }

    private static void addIfMissing(Class<?> cls, Object obj) {
        AnchorDefaultBeanFactory defaultFactory = getOrCreateBeanFactory();
        if (defaultFactory.getDefaultInstances().get(cls) == null) {
            add(defaultFactory, cls, obj);
        }
    }

    private static void add(AnchorDefaultBeanFactory defaultFactory, Class<?> cls, Object obj) {
        BeanInstanceMap instanceMap = new BeanInstanceMap();
        instanceMap.put(cls, obj);

        defaultFactory.getDefaultInstances().addFrom(instanceMap);
    }

    /** This is the default stack-writer used in tests. */
    private static StackWriter defaultStackWriterForTests() {
        return new Tiff();
    }
}
