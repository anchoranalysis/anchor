/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer.error;

import java.io.Serializable;
import org.anchoranalysis.anchor.mpp.mark.Mark;

public abstract class ErrorNode implements Serializable {
    /** */
    private static final long serialVersionUID = -5769879460789165274L;

    public abstract ErrorNode add(String errorMessage);

    public abstract ErrorNode addFormatted(String formatString, Object... args);

    public abstract ErrorNode add(String errorMessage, Mark mark);

    public abstract ErrorNode add(Exception e);

    public abstract ErrorNode addIter(int i);

    public abstract ErrorNode addBean(String propertyName, Object object);

    public abstract void addErrorDescription(StringBuilder sb);
}
