package org.anchoranalysis.io.output.bean.rules;

import java.util.Optional;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.io.output.bean.enabled.None;
import org.anchoranalysis.io.output.bean.enabled.SpecificEnabled;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOr;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;

/**
 * Adds additional outputs-names to be enabled to the defaults.
 *
 * @author Owen Feehan
 */
public class AddToDefaults extends OutputEnableRulesSpecify {

    private class AddImplementation implements MultiLevelOutputEnabled {

        @Override
        public boolean isOutputEnabled(String outputName) {
            return firstLevelContains(outputName);
        }

        @Override
        public SingleLevelOutputEnabled second(String outputName) {
            return secondLevelOutputs(outputName, None.INSTANCE);
        }
    }

    @Override
    public MultiLevelOutputEnabled create(Optional<MultiLevelOutputEnabled> defaultRules) {
        return maybeWrap(new AddImplementation(), defaultRules);
    }

    @Override
    protected SingleLevelOutputEnabled createSecondLevelFromSet(StringSet outputNames) {
        return new SpecificEnabled(outputNames);
    }

    private MultiLevelOutputEnabled maybeWrap(
            MultiLevelOutputEnabled source, Optional<MultiLevelOutputEnabled> defaultRules) {
        if (defaultRules.isPresent()) {
            return new MultiLevelOr(source, defaultRules.get());
        } else {
            return source;
        }
    }
}
