/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer.error;

import java.io.Serializable;
import javax.swing.tree.DefaultTreeModel;

public class ProposerFailureDescription implements Serializable {

    /** */
    private static final long serialVersionUID = 4418671354471633349L;

    private DefaultTreeModel errorTree;
    private ErrorNode root = null;

    public ProposerFailureDescription() {
        root = ErrorNodeNull.instance();
        errorTree = null;
    }

    public static ProposerFailureDescription createRoot() {
        ProposerFailureDescription out = new ProposerFailureDescription();
        ErrorNodeImpl rootImpl = new ErrorNodeImpl(null, "root");
        out.root = rootImpl;
        out.errorTree = new DefaultTreeModel(rootImpl);
        return out;
    }

    public ErrorNode getRoot() {
        return root;
    }

    public DefaultTreeModel getErrorTree() {
        return errorTree;
    }

    public void setErrorTree(DefaultTreeModel errorTree) {
        this.errorTree = errorTree;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        root.addErrorDescription(sb);
        return sb.toString();
    }
}
