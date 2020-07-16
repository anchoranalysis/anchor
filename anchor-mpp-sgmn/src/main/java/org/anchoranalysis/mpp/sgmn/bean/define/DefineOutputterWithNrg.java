/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.define;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProviderReference;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;

public abstract class DefineOutputterWithNrg extends DefineOutputter {

    // START BEAN PROPERTIES
    @BeanField
    private StackProvider nrgStackProvider =
            new StackProviderReference(ImgStackIdentifiers.NRG_STACK);

    @BeanField @OptionalBean private KeyValueParamsProvider nrgParamsProvider;
    // END BEAN PROPERTIES

    protected NRGStackWithParams createNRGStack(ImageInitParams so, Logger logger)
            throws InitException, CreateException {

        // Extract the NRG stack
        StackProvider nrgStackProviderLoc = nrgStackProvider.duplicateBean();
        nrgStackProviderLoc.initRecursive(so, logger);
        NRGStackWithParams stack = new NRGStackWithParams(nrgStackProviderLoc.create());

        if (nrgParamsProvider != null) {
            nrgParamsProvider.initRecursive(so.getParams(), logger);
            stack.setParams(nrgParamsProvider.create());
        }
        return stack;
    }

    public StackProvider getNrgStackProvider() {
        return nrgStackProvider;
    }

    public void setNrgStackProvider(StackProvider nrgStackProvider) {
        this.nrgStackProvider = nrgStackProvider;
    }

    public KeyValueParamsProvider getNrgParamsProvider() {
        return nrgParamsProvider;
    }

    public void setNrgParamsProvider(KeyValueParamsProvider nrgParamsProvider) {
        this.nrgParamsProvider = nrgParamsProvider;
    }
}
