/* (C)2020 */
package org.anchoranalysis.mpp.io.output;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceUtilities;
import org.anchoranalysis.io.generator.serialized.KeyValueParamsGenerator;
import org.anchoranalysis.io.output.bound.BoundIOContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NRGStackWriter {

    public static void writeNRGStack(NRGStackWithParams nrgStack, BoundIOContext context) {
        // We write the nrg stack seperately as individual channels
        GeneratorSequenceUtilities.generateListAsSubfolder(
                "nrgStack",
                2,
                nrgStack.getNrgStack().asStack().asListChnls(),
                new ChnlGenerator("nrgStackChnl"),
                context);

        if (nrgStack.getParams() != null) {
            context.getOutputManager()
                    .getWriterCheckIfAllowed()
                    .write(
                            "nrgStackParams",
                            () ->
                                    new KeyValueParamsGenerator(
                                            nrgStack.getParams(), "nrgStackParams"));
        }
    }
}
