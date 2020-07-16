/*-
 * #%L
 * anchor-mpp-sgmn
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
package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.FeedbackReceiver;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackEndParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

// Aggregates reporting across multiple entities
public class FeedbackReceiverList<T> extends FeedbackReceiverBean<T> {

    // START BEAN PROPERTIES
    @BeanField private List<FeedbackReceiver<T>> list;
    // END BEAN PROPERTIES

    public FeedbackReceiverList() {
        list = new ArrayList<>();
    }

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

    public List<FeedbackReceiver<T>> getList() {
        return list;
    }

    public void setList(List<FeedbackReceiver<T>> list) {
        this.list = list;
    }
}
