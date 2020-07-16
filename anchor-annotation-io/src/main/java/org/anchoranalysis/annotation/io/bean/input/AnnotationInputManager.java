/* (C)2020 */
package org.anchoranalysis.annotation.io.bean.input;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalProgress;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;

public class AnnotationInputManager<T extends ProvidesStackInput, S extends AnnotatorStrategy>
        extends InputManager<AnnotationWithStrategy<S>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private InputManager<T> input;

    @BeanField @Getter @Setter private S annotatorStrategy;
    // END BEAN PROPERTIES

    @Override
    public List<AnnotationWithStrategy<S>> inputObjects(InputManagerParams params)
            throws AnchorIOException {

        try (ProgressReporterMultiple prm =
                new ProgressReporterMultiple(params.getProgressReporter(), 2)) {

            List<T> inputs = input.inputObjects(params);

            prm.incrWorker();

            List<AnnotationWithStrategy<S>> outList =
                    createListInput(inputs, new ProgressReporterOneOfMany(prm));
            prm.incrWorker();

            return outList;
        }
    }

    private List<AnnotationWithStrategy<S>> createListInput(
            List<T> listInputObjects, ProgressReporter progressReporter) throws AnchorIOException {
        return FunctionalProgress.mapList(
                listInputObjects,
                progressReporter,
                inputObject -> new AnnotationWithStrategy<S>(inputObject, annotatorStrategy));
    }
}
