/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.define;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.collection.IterableGeneratorOutputHelper;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

class SubsetOutputter<T> {
    private NamedProvider<T> providers;
    private OutputAllowed oa;
    private IterableGenerator<T> generator;
    private BoundOutputManager outputManager;
    private String outputName;
    private String suffix;
    private boolean suppressSubfoldersIn;

    public SubsetOutputter(
            NamedProvider<T> providers,
            OutputAllowed oa,
            IterableGenerator<T> generator,
            BoundOutputManager outputManager,
            String outputName,
            String suffix,
            boolean suppressSubfoldersIn) {
        super();
        this.providers = providers;
        this.oa = oa;
        this.generator = generator;
        this.outputManager = outputManager;
        this.outputName = outputName;
        this.suffix = suffix;
        this.suppressSubfoldersIn = suppressSubfoldersIn;
    }

    public void outputSubset(ErrorReporter errorReporter) {

        if (!outputManager.isOutputAllowed(outputName)) {
            return;
        }

        IterableGeneratorOutputHelper.output(
                IterableGeneratorOutputHelper.subset(providers, oa, errorReporter),
                generator,
                outputManager,
                outputName,
                suffix,
                errorReporter,
                suppressSubfoldersIn);
    }

    public void outputSubsetWithException() throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return;
        }

        IterableGeneratorOutputHelper.outputWithException(
                IterableGeneratorOutputHelper.subsetWithException(providers, oa),
                generator,
                outputManager,
                outputName,
                suffix,
                suppressSubfoldersIn);
    }
}
