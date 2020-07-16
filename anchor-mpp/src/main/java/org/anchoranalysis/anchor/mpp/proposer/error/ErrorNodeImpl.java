/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer.error;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ErrorNodeImpl extends ErrorNode implements TreeNode {

    /** */
    private static final long serialVersionUID = -3967665095180902654L;

    @SuppressWarnings("unused")
    private ErrorNode parent;

    private ArrayList<ErrorNodeImpl> children = new ArrayList<>();
    private String errorMessage;
    private transient Mark associatedMark;

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

    @Override
    public ErrorNode add(String errorMessage, Mark mark) {
        ErrorNodeImpl toAdd = add(errorMessage);
        toAdd.associatedMark = mark;
        return toAdd;
    }

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

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return getErrorMessage();
    }

    public Mark getAssociatedMark() {
        return associatedMark;
    }

    @Override
    public ErrorNode addFormatted(String formatString, Object... args) {
        return add(String.format(formatString, args));
    }

    @Override
    public ErrorNode addIter(int i) {
        return add("iter=" + NumberFormat.getIntegerInstance().format(i));
    }

    @Override
    public ErrorNode addBean(String propertyName, Object object) {
        return add(propertyName + ": " + object.toString());
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
}
