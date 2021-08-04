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
import java.util.HashMap;
import org.anchoranalysis.mpp.segment.bean.optimization.ExtractScoreSize;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackBeginParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.ReporterException;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodTriggerBank;
import org.anchoranalysis.mpp.segment.optimization.step.Reporting;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AggregateTriggerBank<T> {

    private HashMap<Integer, AggregateTrigger<T, AggregateReceiverList<T>>> map = new HashMap<>();
    private ArrayList<AggregateTrigger<T, AggregateReceiverList<T>>> list = new ArrayList<>();

    private final ExtractScoreSize<T> extractScoreSize;

    public AggregateTrigger<T, AggregateReceiverList<T>> obtain(
            int period, AggregateReceiver<T> receiver, PeriodTriggerBank<T> periodTriggerBank) {

        AggregateTrigger<T, AggregateReceiverList<T>> existing = map.get(period);

        // If we don't already have a trigger for this specific period, we create one
        if (existing == null) {
            AggregateReceiverList<T> listNew = new AggregateReceiverList<>();
            existing = new AggregateTrigger<>(listNew, period, periodTriggerBank, extractScoreSize);
            list.add(existing);
        }

        existing.getPeriodReceiver().add(receiver);
        return existing;
    }

    public void start(FeedbackBeginParameters<T> initialization) throws AggregatorException {
        for (AggregateTrigger<T, AggregateReceiverList<T>> item : list) {
            item.start(initialization);
        }
    }

    public void trigger(Reporting<T> reporting) throws ReporterException {

        for (AggregateTrigger<T, AggregateReceiverList<T>> item : list) {
            item.trigger(reporting);
        }
    }

    public void end() throws AggregatorException {
        for (AggregateTrigger<T, AggregateReceiverList<T>> item : list) {
            item.end();
        }
    }
}
