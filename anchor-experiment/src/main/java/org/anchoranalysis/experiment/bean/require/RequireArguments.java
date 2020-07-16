/* (C)2020 */
package org.anchoranalysis.experiment.bean.require;

import org.anchoranalysis.bean.AnchorBean;

public abstract class RequireArguments extends AnchorBean<RequireArguments> {

    public abstract boolean hasAllRequiredArguments(boolean debugModeEnabled);
}
