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

package org.anchoranalysis.mpp.segment.optscheme.feedback.aggregate;

import java.io.PrintWriter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Aggregator {

    @Getter private double energy;
    @Getter @Setter private double size;
    @Getter private double temperature;
    @Getter private double acceptAll;

    @Getter private int lastDivider;

    @Getter private AggIDCounter kernelAccepted;
    @Getter private AggIDCounter kernelProposed;

    public Aggregator(int maxID) {
        kernelAccepted = new AggIDCounter(maxID);
        kernelProposed = new AggIDCounter(maxID);
        reset();
    }

    public Aggregator deepCopy() {
        Aggregator agg = new Aggregator();
        agg.kernelAccepted = kernelAccepted.deepCopy();
        agg.kernelProposed = kernelProposed.deepCopy();
        agg.energy = energy;
        agg.size = size;
        agg.temperature = temperature;
        agg.acceptAll = acceptAll;
        agg.lastDivider = lastDivider;
        return agg;
    }

    public void reset() {
        energy = 0;
        size = 0;
        temperature = 0;
        acceptAll = 0;
        kernelAccepted.reset();
        kernelProposed.reset();
        lastDivider = -1;
    }

    public void div(int divider) {
        energy /= divider;
        size /= divider;
        temperature /= divider;
        acceptAll /= divider;
        kernelAccepted.div(divider);
        kernelProposed.div(divider);
        kernelAccepted.div(kernelProposed);
        this.lastDivider = divider;
    }

    @Override
    public String toString() {
        return String.format(
                "agg(energy=%e  size=%3.3f  aa=%3.6f temp=%e  ka=%s)",
                energy, size, acceptAll, temperature, kernelAccepted.toString());
    }

    public void outputToWriter(PrintWriter writer) {
        writer.printf("%e,%f,%e,%e,", energy, size, acceptAll, temperature);
        kernelProposed.outputToWriter(writer);
        writer.print(",");
        kernelAccepted.outputToWriter(writer);
    }

    public void outputHeaderToWriter(PrintWriter writer) {
        writer.print("Energy,Size,AccptAll,Temperature,");
        kernelProposed.outputHeaderToWriter(writer, "Prop");
        writer.print(",");
        kernelAccepted.outputHeaderToWriter(writer, "Accpt");
    }

    public void incrEnergy(double incr) {
        energy += incr;
    }

    public void incrSize(double incr) {
        size += incr;
    }

    public void incrTemperature(double incr) {
        temperature += incr;
    }

    public void incrKernelProp(int kernelID) {
        kernelProposed.incr(kernelID);
    }

    public void incrKernelAccpt(int kernelID) {
        kernelAccepted.incr(kernelID);
        acceptAll++;
    }

    public boolean hasLastDivider() {
        return (lastDivider != -1);
    }
}
