/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.cfg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.mark.factory.MarkFactory;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;

@NoArgsConstructor
public class CfgGen extends NullParamsBean<CfgGen> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private double referencePoissonIntensity = 1e-5;

    // A template mark from which all new marks are copied
    @BeanField @Getter @Setter private MarkFactory templateMark = null;
    // END BEAN PARAMETERS

    private IdCounter idCounter;

    // Constructor
    public CfgGen(MarkFactory templateMark) {
        this.templateMark = templateMark;
    }

    @Override
    public String getBeanDscr() {
        return String.format(
                "%s templateMark=%s, referencePoissonIntensity=%f",
                getBeanName(), templateMark.toString(), referencePoissonIntensity);
    }

    @Override
    public void onInit() throws InitException {
        super.onInit();
        idCounter = new IdCounter(1);
    }

    public Mark newTemplateMark() {
        assert (templateMark != null);
        Mark mark = this.templateMark.create();
        mark.setId(idAndIncrement());
        return mark;
    }

    public int idAndIncrement() {
        assert idCounter != null;
        return idCounter.getIdAndIncrement();
    }
}
