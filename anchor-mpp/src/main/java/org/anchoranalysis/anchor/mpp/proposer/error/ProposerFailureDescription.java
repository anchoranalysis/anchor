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
