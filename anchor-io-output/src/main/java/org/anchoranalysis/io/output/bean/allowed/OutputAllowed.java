/* (C)2020 */
package org.anchoranalysis.io.output.bean.allowed;

import org.anchoranalysis.bean.AnchorBean;

public abstract class OutputAllowed extends AnchorBean<OutputAllowed> {

    public abstract boolean isOutputAllowed(String outputName);
}
