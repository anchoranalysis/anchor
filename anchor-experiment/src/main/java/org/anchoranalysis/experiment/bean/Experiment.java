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

public abstract class Experiment extends AnchorBean<Experiment> implements AssociateXMLUponLoad {

    // Allows to reference the xml configuration from where the experiment was defined
    private XMLConfiguration xmlConfiguration = null;

    /**
     * Executes the experiment.
     *
     * @param arguments arguments that may influence how the experiment is run.
     * @return the path files written into as <i>output</i>, if such a path exists.
     * @throws ExperimentExecutionException
     */
    public abstract Optional<Path> executeExperiment(ExecutionArguments arguments)
            throws ExperimentExecutionException;

    public XMLConfiguration getXMLConfiguration() {
        return xmlConfiguration;
    }

    @Override
    public void associateXML(XMLConfiguration xmlConfiguration) {
        this.xmlConfiguration = xmlConfiguration;
    }

    /**
     * Whether to detail more (a lengthy experiment) or less (something quick and simple - suitable
     * for console)?
     */
    public abstract boolean useDetailedLogging();
}
