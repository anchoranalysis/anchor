/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.FeedbackReceiver;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackEndParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

// Aggregates reporting across multiple entities
public class FeedbackReceiverList<T> extends FeedbackReceiverBean<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<FeedbackReceiver<T>> list = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    public String getBeanDscr() {

        String newLine = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append(getBeanName());
        sb.append("{");
        sb.append(newLine);
        for (FeedbackReceiver<T> fr : list) {
            sb.append(fr.toString());
            sb.append(newLine);
        }
        sb.append("}");
        sb.append(getBeanName());
        return sb.toString();
    }

    public boolean add(FeedbackReceiver<T> receiver) {
        return list.add(receiver);
    }

    @Override
    public void reportItr(Reporting<T> reporting) throws ReporterException {

        for (FeedbackReceiver<T> fr : list) {
            fr.reportItr(reporting);
        }
    }

    @Override
    public void reportNewBest(Reporting<T> reporting) throws ReporterException {

        for (FeedbackReceiver<T> fr : list) {
            fr.reportNewBest(reporting);
        }
    }

    @Override
    public void reportEnd(OptimizationFeedbackEndParams<T> optStep) throws ReporterException {

        for (FeedbackReceiver<T> fr : list) {
            fr.reportEnd(optStep);
        }
    }

    @Override
    public void reportBegin(OptimizationFeedbackInitParams<T> initParams) throws ReporterException {
        for (FeedbackReceiver<T> fr : list) {
            fr.reportBegin(initParams);
        }
    }
}
