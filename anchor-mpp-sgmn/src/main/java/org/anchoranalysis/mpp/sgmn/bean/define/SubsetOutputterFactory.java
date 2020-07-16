/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.define;

import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.io.objects.ObjectCollectionWriter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.histogram.HistogramCSVGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.io.output.StackOutputKeys;
import org.anchoranalysis.mpp.sgmn.define.OutputterDirectories;

/**
 * This class will expect for the following second-level output keys: {@link StackOutputKeys.CFG}
 * {@link StackOutputKeys.HISTOGRAM} {@link StackOutputKeys.OBJECTS}
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class SubsetOutputterFactory {

    private MPPInitParams soMPP;
    private BoundOutputManagerRouteErrors outputManager;
    private boolean suppressSubfolders;

    public SubsetOutputter<Cfg> cfg() {
        return create(
                soMPP.getCfgCollection(),
                new XStreamGenerator<Cfg>(Optional.of("cfg")),
                (BoundOutputManagerRouteErrors bom) ->
                        bom.outputAllowedSecondLevel(StackOutputKeys.CFG),
                OutputterDirectories.CFG);
    }

    public SubsetOutputter<Histogram> histogram() {
        return create(
                soMPP.getImage().getHistogramCollection(),
                new HistogramCSVGenerator(),
                (BoundOutputManagerRouteErrors bom) ->
                        bom.outputAllowedSecondLevel(StackOutputKeys.HISTOGRAM),
                OutputterDirectories.HISTOGRAM);
    }

    public SubsetOutputter<ObjectCollection> objects() {
        return create(
                soMPP.getImage().getObjectCollection(),
                ObjectCollectionWriter.generator(),
                (BoundOutputManagerRouteErrors bom) ->
                        bom.outputAllowedSecondLevel(StackOutputKeys.OBJECTS),
                OutputterDirectories.OBJECT);
    }

    private <T> SubsetOutputter<T> create(
            NamedProviderStore<T> store,
            IterableGenerator<T> generator,
            Function<BoundOutputManagerRouteErrors, OutputAllowed> outputAllowedFunc,
            String id) {
        return new SubsetOutputter<>(
                store,
                outputAllowedFunc.apply(outputManager),
                generator,
                outputManager.getDelegate(),
                id,
                "",
                suppressSubfolders);
    }
}
