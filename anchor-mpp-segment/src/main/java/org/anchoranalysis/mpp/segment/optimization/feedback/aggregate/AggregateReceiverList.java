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

package org.anchoranalysis.mpp.segment.optimization.feedback.aggregate;

import java.util.ArrayList;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackBeginParameters;
import org.anchoranalysis.mpp.segment.optimization.step.Reporting;

public class AggregateReceiverList<T> implements AggregateReceiver<T> {

    private ArrayList<AggregateReceiver<T>> delegate = new ArrayList<>();

    public boolean add(AggregateReceiver<T> e) {
        return delegate.add(e);
    }

    @Override
    public void aggStart(FeedbackBeginParameters<T> initParams, Aggregator agg)
            throws AggregatorException {

        for (AggregateReceiver<T> receiver : delegate) {
            receiver.aggStart(initParams, agg);
        }
    }

    @Override
    public void aggEnd(Aggregator agg) throws AggregatorException {

        for (AggregateReceiver<T> receiver : delegate) {
            receiver.aggEnd(agg);
        }
    }

    @Override
    public void aggReport(Reporting<T> reporting, Aggregator agg) throws AggregatorException {

        for (AggregateReceiver<T> receiver : delegate) {
            receiver.aggReport(reporting, agg);
        }
    }
}
