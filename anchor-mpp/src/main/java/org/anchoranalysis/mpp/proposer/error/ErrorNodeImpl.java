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

package org.anchoranalysis.mpp.proposer.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import org.anchoranalysis.mpp.mark.Mark;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Implementation of ErrorNode that also implements TreeNode for use in tree structures.
 */
public class ErrorNodeImpl extends ErrorNode implements TreeNode {

    private static final long serialVersionUID = -3967665095180902654L;

    @SuppressWarnings("unused")
    private ErrorNode parent;

    private ArrayList<ErrorNodeImpl> children = new ArrayList<>();
    private String errorMessage;
    private transient Mark associatedMark;

    /**
     * Constructs a new ErrorNodeImpl.
     *
     * @param parent the parent ErrorNode
     * @param errorMessage the error message for this node
     */
    ErrorNodeImpl(ErrorNode parent, String errorMessage) {
        super();
        this.parent = parent;
        this.errorMessage = errorMessage;
    }

    @Override
    public ErrorNodeImpl add(String errorMessage) {
        ErrorNodeImpl toAdd = new ErrorNodeImpl(this, errorMessage);
        this.children.add(toAdd);
        return toAdd;
    }

    /**
     * Gets the error message associated with this node.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the Mark associated with this error node.
     *
     * @return the associated Mark
     */
    public Mark getAssociatedMark() {
        return associatedMark;
    }

    @Override
    public String toString() {
        return getErrorMessage();
    }

    @Override
    public ErrorNode addFormatted(String formatString, Object... args) {
        return add(String.format(formatString, args));
    }

    @Override
    public ErrorNode add(Exception e) {
        return add(ExceptionUtils.getStackTrace(e));
    }

    @Override
    public void addErrorDescription(StringBuilder sb) {
        sb.append(errorMessage);
        sb.append(System.getProperty("line.separator"));
        for (ErrorNodeImpl child : children) {
            child.addErrorDescription(sb);
        }
    }

    // TreeNode interface methods

    @Override
    public Enumeration<ErrorNodeImpl> children() {
        return Collections.enumeration(children);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public int getIndex(TreeNode node) {
        return this.children.indexOf(node);
    }

    @Override
    public TreeNode getParent() {
        return this.getParent();
    }

    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }
}