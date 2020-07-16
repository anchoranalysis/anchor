/* (C)2020 */
package org.anchoranalysis.annotation.wholeimage;

import org.anchoranalysis.annotation.Annotation;

public class WholeImageLabelAnnotation implements Annotation {

    private String label;

    public WholeImageLabelAnnotation(String label) {
        super();
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
