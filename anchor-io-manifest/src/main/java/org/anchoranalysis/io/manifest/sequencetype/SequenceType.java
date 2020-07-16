/* (C)2020 */
package org.anchoranalysis.io.manifest.sequencetype;

import java.io.Serializable;
import org.anchoranalysis.core.index.container.BoundedRangeIncomplete;
import org.anchoranalysis.core.index.container.OrderProvider;

public abstract class SequenceType implements BoundedRangeIncomplete, Serializable {

    /** */
    private static final long serialVersionUID = -4972832404633715034L;

    public abstract String getName();

    public abstract void update(String index) throws SequenceTypeException;

    public abstract int getNumElements();

    // String for index
    public abstract String indexStr(int index);

    public abstract OrderProvider createOrderProvider();
}
