/* (C)2020 */
package org.anchoranalysis.mpp.io.input;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.TimeSequence;

/** Combines a Stack with a map of other stacks */
public class StackWithMap implements MultiInputSubMap<TimeSequence> {

    // Needed for getting main-stack
    private String mainObjectName;
    private ProvidesStackInput mainInputObject;

    // Where the other stacks are stored
    private OperationMap<TimeSequence> map = new OperationMap<>();

    public StackWithMap(String mainObjectName, ProvidesStackInput mainInputObject) {
        super();
        this.mainObjectName = mainObjectName;
        this.mainInputObject = mainInputObject;
    }

    @Override
    public void addToStore(NamedProviderStore<TimeSequence> stackCollection)
            throws OperationFailedException {
        addToStore(stackCollection, 0, ProgressReporterNull.get());
    }

    public void addToStore(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        // We add the main object
        mainInputObject.addToStoreWithName(
                mainObjectName, stackCollection, seriesNum, progressReporter);

        // We add the other objects
        map.addToStore(stackCollection);
    }

    @Override
    public void add(String name, Operation<TimeSequence, OperationFailedException> op) {
        map.add(name, op);
    }

    public void close(ErrorReporter errorReporter) {
        mainInputObject.close(errorReporter);
        map = null;
    }

    public String getMainObjectName() {
        return mainObjectName;
    }

    public String descriptiveName() {
        return mainInputObject.descriptiveName();
    }

    public Optional<Path> pathForBinding() {
        return mainInputObject.pathForBinding();
    }

    public int numFrames() throws OperationFailedException {
        return mainInputObject.numFrames();
    }

    @Override
    public Operation<TimeSequence, OperationFailedException> get(String name)
            throws OperationFailedException {

        if (name.equals(mainObjectName)) {
            throw new OperationFailedException("Retrieving the main-object name is not allowed");
        }

        return map.get(name);
    }
}
