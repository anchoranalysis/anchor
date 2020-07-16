/* (C)2020 */
package org.anchoranalysis.experiment.bean;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.xml.IAssociateXmlUponLoad;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.apache.commons.configuration.XMLConfiguration;

public abstract class Experiment extends AnchorBean<Experiment> implements IAssociateXmlUponLoad {

    // Allows to reference the xml configuration from where the experiment was defined
    private XMLConfiguration xmlConfiguration = null;

    public abstract void doExperiment(ExperimentExecutionArguments arguments)
            throws ExperimentExecutionException;

    public XMLConfiguration getXMLConfiguration() {
        return xmlConfiguration;
    }

    @Override
    public void associateXml(XMLConfiguration xmlConfiguration) {
        this.xmlConfiguration = xmlConfiguration;
    }

    /**
     * Whether to detail more (a lengthy experiment) or less (something quick and simple - suitable
     * for console)?
     */
    public abstract boolean useDetailedLogging();
}
