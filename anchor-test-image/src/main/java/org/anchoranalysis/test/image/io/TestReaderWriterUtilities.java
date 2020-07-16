/* (C)2020 */
package org.anchoranalysis.test.image.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.io.bioformats.bean.BioformatsReader;
import org.anchoranalysis.io.bioformats.bean.options.ForceTimeSeriesToStack;
import org.anchoranalysis.io.ij.bean.writer.IJTiffWriter;
import org.apache.commons.configuration.beanutils.BeanHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestReaderWriterUtilities {

    public static void ensureRasterReader() {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
        addIfMissing(RasterReader.class, createReader());
    }

    public static void ensureRasterWriter() {
        addIfMissing(RasterWriter.class, new IJTiffWriter());
    }

    private static RasterReader createReader() {
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
}
