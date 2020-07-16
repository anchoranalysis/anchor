/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer.error;

import org.anchoranalysis.core.error.AnchorCheckedException;

// An exception that adds a string to the current error node
public class ErrorNodeException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -4000166470139114311L;

    private final String str;

    public ErrorNodeException(String str) {
        super(str);
        this.str = str;
    }

    public ErrorNodeException(Exception e) {
        super(e.toString());
        this.str = e.toString();
    }

    public void addToErrorNode(ErrorNode errorNode) {
        errorNode.add(str);
    }
}
