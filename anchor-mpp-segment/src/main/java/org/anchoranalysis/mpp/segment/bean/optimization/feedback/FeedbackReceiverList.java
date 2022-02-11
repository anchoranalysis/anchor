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

package org.anchoranalysis.mpp.segment.bean.optimization.feedback;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackBeginParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackEndParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackReceiver;
import org.anchoranalysis.mpp.segment.optimization.feedback.ReporterException;
import org.anchoranalysis.mpp.segment.optimization.step.Reporting;

// Aggregates reporting across multiple entities
public class FeedbackReceiverList<T> extends FeedbackReceiverBean<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<FeedbackReceiver<T>> list = Arrays.asList();
    // END BEAN PROPERTIES

    @Override
    public String describeBean() {

        String newLine = System.getProperty("line.separator");

        StringBuilder builder = new StringBuilder();
        builder.append(getBeanName());
        builder.append("{");
        builder.append(newLine);
        for (FeedbackReceiver<T> fr : list) {
            builder.append(fr.toString());
            builder.append(newLine);
        }
        builder.append("}");
        builder.append(getBeanName());
        return builder.toString();
    }

    public boolean add(FeedbackReceiver<T> receiver) {
        return list.add(receiver);
    }

    @Override
    public void reportIteration(Reporting<T> reporting) throws ReporterException {

        for (FeedbackReceiver<T> receiver : list) {
            receiver.reportIteration(reporting);
        }
    }

    @Override
    public void reportNewBest(Reporting<T> reporting) throws ReporterException {

        for (FeedbackReceiver<T> receiver : list) {
            receiver.reportNewBest(reporting);
        }
    }

    @Override
    public void reportEnd(FeedbackEndParameters<T> parameters) throws ReporterException {

        for (FeedbackReceiver<T> receiver : list) {
            receiver.reportEnd(parameters);
        }
    }

    @Override
    public void reportBegin(FeedbackBeginParameters<T> initialization) throws ReporterException {
        for (FeedbackReceiver<T> receiver : list) {
            receiver.reportBegin(initialization);
        }
    }
}
