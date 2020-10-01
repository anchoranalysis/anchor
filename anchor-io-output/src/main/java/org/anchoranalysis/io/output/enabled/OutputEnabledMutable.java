/*-
 * #%L
 * anchor-io-output
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
package org.anchoranalysis.io.output.enabled;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.io.output.bean.enabled.None;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelInSet;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOr;

/**
 * A specific set of first-level outputs are enabled, to which more can be added.
 * 
 * @author Owen Feehan
 *
 */
public class OutputEnabledMutable implements MultiLevelOutputEnabled {

    /** First level output-names. */
    private Set<String> enabledFirst = new HashSet<>();
    
    /** Second level output-names indexed by the first-level output with which they are associated. */
    private SecondLevelOutputEnabled enabledSecond = new SecondLevelOutputEnabled();
    
    /** Employed in addition to {@code enabledSecond} to enable second-level outputs. */ 
    private final Optional<SingleLevelOutputEnabled> additionalSecond;

    /**
     * Creates with one or more enabled outputs.
     * 
     * @param outputNames the names of the enabled-outputs
     */
    public OutputEnabledMutable(String ... outputNames) {
        this( Optional.empty(), outputNames );
    }
    
    /**
     * Creates with one or more enabled outputs.
     * 
     * @param outputNames the names of the enabled-outputs
     */
    public OutputEnabledMutable(SingleLevelOutputEnabled additionalSecond, String ... outputNames) {
        this( Optional.of(additionalSecond), outputNames );
    }
    
    private OutputEnabledMutable(Optional<SingleLevelOutputEnabled> additionalSecond, String[] outputNames) {
        this.additionalSecond = additionalSecond;
        Arrays.stream(outputNames).forEach(this::addEnabledOutputFirst);
    }
    
    @Override
    public boolean isOutputEnabled(String outputName) {
        return enabledFirst.contains(outputName);
    }

    @Override
    public SingleLevelOutputEnabled second(String outputName) {
        Optional<Set<String>> outputs = enabledSecond.secondLevelOutputsFor(outputName); 
        if (outputs.isPresent()) {
            return maybeCombineWithAdditional(outputs.get());
        } else {
            return additionalSecond.orElse(None.INSTANCE);
        }
    }
    
    /**
     * Adds enabled first-level outputs.
     * 
     * @param outputNames the names of the enabled-outputs
     * @return the current object
     */
    public OutputEnabledMutable addEnabledOutputFirst(String ... outputNames) {
        Arrays.stream(outputNames).forEach(enabledFirst::add);
        return this;
    }
    
    /**
     * Adds enabled second-level outputs.
     * 
     * @param outputNameFirstLevel the first-level output with which the second-level outputs are associated.
     * @param outputNames the names of the enabled-outputs
     * @return the current object
     */
    public OutputEnabledMutable addEnabledOutputSecond(String outputNameFirstLevel, String ...outputNames) {
        enabledSecond.addEnabledOutputs(outputNameFirstLevel, outputNames);
        return this;
    }
    
    /**
     * Adds enabled outputs from another {@link OutputEnabledMutable}.
     * 
     * @param other the other {@link OutputEnabledMutable} to add from
     * @return the current object
     */
    public OutputEnabledMutable addEnabledOutputs(OutputEnabledMutable other) {
        enabledFirst.addAll(other.enabledFirst);
        enabledSecond.addEnabledOutputs(other.enabledSecond);
        return this;
    }
    
    private SingleLevelOutputEnabled maybeCombineWithAdditional(Set<String> outputs) {
        SingleLevelInSet existingOutputs = new SingleLevelInSet(outputs);
        if (additionalSecond.isPresent()) {
            return new SingleLevelOr(existingOutputs, additionalSecond.get());
        } else {
            return existingOutputs;
        }
    }
}
