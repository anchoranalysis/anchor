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

package org.anchoranalysis.mpp.segment.bean;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.value.KeyValueParams;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.core.stack.StackIdentifiers;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.output.bean.enabled.IgnoreUnderscorePrefix;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.mark.MarkCollection;

public abstract class SegmentIntoMarks extends AnchorBean<SegmentIntoMarks> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String backgroundStackName = StackIdentifiers.INPUT_IMAGE;
    // END BEAN PROPERTIES

    // Creates state for the experiment in general, that is not tied to any particular image
    public abstract ExperimentState createExperimentState();

    public abstract MarkCollection segment(
            NamedStacks stacks,
            NamedProvider<ObjectCollection> objects,
            Optional<KeyValueParams> keyValueParams,
            InputOutputContext context)
            throws SegmentationFailedException;

    /**
     * If specified, default rules for determine which outputs are enabled or not.
     *
     * @return the default rules if they exist.
     */
    public OutputEnabledMutable defaultOutputs() {
        return new OutputEnabledMutable(IgnoreUnderscorePrefix.INSTANCE);
    }
}
