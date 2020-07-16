/* (C)2020 */
package org.anchoranalysis.image.io.bean;

import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.factory.IndirectlyFromListBeanFactory;
import org.anchoranalysis.bean.xml.factory.ListBeanFactory;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterCell;
import org.anchoranalysis.image.io.chnl.map.CreateImgChnlMapFromEntries;

// An externally loadable component of the system
public final class RegisterBeanFactoriesIO {

    private RegisterBeanFactoriesIO() {}

    public static void registerBeanFactories() {
        RegisterBeanFactories.register(
                "imgChnlMap",
                new IndirectlyFromListBeanFactory<>(new CreateImgChnlMapFromEntries()));
        RegisterBeanFactories.register(
                "arrangeRasterCellList", new ListBeanFactory<ArrangeRasterCell>());
    }
}
