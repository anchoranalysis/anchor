/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.bean;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.xml.AssociateXMLUponLoad;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * A base class for a type of task that produces some kind of result of interest.
 *
 * <p>It is an atomic unit of execution in the Anchor framework in many contexts.
 *
 * @author Owen Feehan
 */
public abstract class Experiment extends AnchorBean<Experiment> implements AssociateXMLUponLoad {

    /** Allows to reference the XML configuration from where the experiment was defined. */
    private XMLConfiguration xmlConfiguration = null;

    /**
     * Executes the experiment.
     *
     * @param arguments arguments that may influence how the experiment is run.
     * @return the path files written into as <i>output</i>, if such a path exists.
     * @throws ExperimentExecutionException if an error occurs executing the experiment.
     */
    public abstract Optional<Path> executeExperiment(ExecutionArguments arguments)
            throws ExperimentExecutionException;

    /**
     * The configuration of experiment serialized into XML form.
     *
     * @return the internal XML-representation that has been associated with the experiment.
     */
    public XMLConfiguration getXMLConfiguration() {
        return xmlConfiguration;
    }

    @Override
    public void associateXML(XMLConfiguration xmlConfiguration) {
        this.xmlConfiguration = xmlConfiguration;
    }

    /**
     * Whether to log in more or less detail.
     *
     * <p>It's appropriate to perform more detailed logging for a lengthier experiment, but
     * something quick and simple is preferable for a short job, which may only be outputted to the
     * console.
     *
     * @return true iff detailed logging should be employed.
     */
    public abstract boolean useDetailedLogging();
}
