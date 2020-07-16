/* (C)2020 */
package org.anchoranalysis.bean.init;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;

class BeanAndParent {

    /** The bean */
    private AnchorBean<?> bean;

    /** Parent bean, or NULL if the bean doesn't have a parent */
    private BeanAndParent parent;

    public BeanAndParent(AnchorBean<?> bean, BeanAndParent parent) {
        super();
        this.bean = bean;
        this.parent = parent;
    }

    public AnchorBean<?> getBean() {
        return bean;
    }

    public BeanAndParent getParent() {
        return parent;
    }

    public AnchorBean<?> parentBean() {
        return parent != null ? parent.getBean() : null;
    }

    /**
     * Path from the the root of the tree (null parent) to the current item expressed as a string
     * with ({@literal "->"} as a divider
     *
     * <p>{@literal From root->leaf = left->right}
     *
     * @return a string showing the path with {@literal "->"} as a divider
     */
    public String pathFromRootAsString() {
        // Populate list from root to leaf with the beans along the path, and convert to string
        return stringFromPath(beansRootToLeaf());
    }

    private List<AnchorBean<?>> beansRootToLeaf() {

        List<AnchorBean<?>> listObjects = new ArrayList<>();

        BeanAndParent next = this;
        listObjects.add(0, next.getBean());
        while ((next = next.getParent()) != null) {
            listObjects.add(0, next.getBean());
        }

        return listObjects;
    }

    private static String stringFromPath(List<AnchorBean<?>> listObjects) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listObjects.size(); i++) {

            if (i != 0) {
                sb.append("->");
            }

            AnchorBean<?> beanIter = listObjects.get(i);
            sb.append(beanIter.getBeanName());
        }
        return sb.toString();
    }
}
