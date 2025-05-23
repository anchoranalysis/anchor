/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.mark.factory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NullParametersBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.mpp.mark.Mark;

/**
 * A factory for creating marks with unique identifiers.
 *
 * <p>This class extends NullParametersBean, providing functionality to create marks with unique
 * identifiers based on a template mark in the MPP (Marked Point Process) framework.
 */
@NoArgsConstructor
public class MarkWithIdentifierFactory extends NullParametersBean<MarkWithIdentifierFactory> {

    // START BEAN PARAMETERS
    /** The reference Poisson intensity for mark creation. */
    @BeanField @Getter @Setter private double referencePoissonIntensity = 1e-5;

    /** A template mark factory from which all new marks are created. */
    @BeanField @Getter @Setter private MarkFactory templateMark = null;

    // END BEAN PARAMETERS

    private IdCounter idCounter;

    /**
     * Constructs a MarkWithIdentifierFactory with a specified template mark factory.
     *
     * @param templateMark the template mark factory to use for creating new marks
     */
    public MarkWithIdentifierFactory(MarkFactory templateMark) {
        this.templateMark = templateMark;
    }

    @Override
    public String describeBean() {
        return String.format(
                "%s templateMark=%s, referencePoissonIntensity=%f",
                getBeanName(), templateMark.toString(), referencePoissonIntensity);
    }

    @Override
    public void onInitialization() throws InitializeException {
        super.onInitialization();
        idCounter = new IdCounter(1);
    }

    /**
     * Creates a new mark based on the template and assigns it a unique identifier.
     *
     * @return a new Mark object with a unique identifier
     */
    public Mark newTemplateMark() {
        assert (templateMark != null);
        Mark mark = this.templateMark.create();
        mark.setId(idAndIncrement());
        return mark;
    }

    /**
     * Generates a new unique identifier and increments the counter.
     *
     * @return a new unique identifier
     */
    public int idAndIncrement() {
        assert idCounter != null;
        return idCounter.getIdAndIncrement();
    }
}
