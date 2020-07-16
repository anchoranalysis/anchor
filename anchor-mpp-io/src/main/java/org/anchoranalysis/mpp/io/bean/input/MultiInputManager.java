/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.input;

import static org.anchoranalysis.mpp.io.bean.input.AppendHelper.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.io.input.MultiInput;

// An input stack
@NoArgsConstructor
public class MultiInputManager extends MultiInputManagerBase {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String inputName = MultiInput.DEFAULT_IMAGE_INPUT_NAME;

    @BeanField @Getter @Setter private InputManager<? extends ProvidesStackInput> input;

    @BeanField @DefaultInstance @Getter @Setter
    private RasterReader rasterReader; // For reading appended files

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> appendStack = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendCfg = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotation =
            new ArrayList<>(); // Uses both accepted and rejected

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotationAcceptedOnly =
            new ArrayList<>(); // Uses both accepted only

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotationRejectedOnly =
            new ArrayList<>(); // Uses both accepted rejectedonly

    /** Appends object-collections to the multi-input */
    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> appendObjects = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendKeyValueParams = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> appendHistogram = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathGenerator>> listAppendFilePath = new ArrayList<>();
    // END BEAN PROPERTIES

    public MultiInputManager(String inputName, InputManager<? extends ProvidesStackInput> input) {
        this.inputName = inputName;
        this.input = input;
    }

    @Override
    public List<MultiInput> inputObjects(InputManagerParams params) throws AnchorIOException {

        List<MultiInput> outList = new ArrayList<>();

        Iterator<? extends ProvidesStackInput> itr = input.inputObjects(params).iterator();

        while (itr.hasNext()) {
            ProvidesStackInput mainStack = itr.next();

            MultiInput inputObject = new MultiInput(inputName, mainStack);
            appendFromLists(inputObject, params.isDebugModeActivated());

            outList.add(inputObject);
        }

        return outList;
    }

    private void appendFromLists(MultiInput inputObject, boolean doDebug) {
        appendStack(appendStack, inputObject, doDebug, rasterReader);
        appendFromVariousCfgSources(inputObject, doDebug);
        appendObjects(appendObjects, inputObject, doDebug);
        appendKeyValueParams(listAppendKeyValueParams, inputObject, doDebug);
        appendHistogram(appendHistogram, inputObject, doDebug);
        appendFilePath(listAppendFilePath, inputObject, doDebug);
    }

    private void appendFromVariousCfgSources(MultiInput inputObject, boolean doDebug) {
        appendCfg(listAppendCfg, inputObject, doDebug);
        appendCfgFromAnnotation(listAppendCfgFromAnnotation, inputObject, true, true, doDebug);
        appendCfgFromAnnotation(
                listAppendCfgFromAnnotationAcceptedOnly, inputObject, true, false, doDebug);
        appendCfgFromAnnotation(
                listAppendCfgFromAnnotationRejectedOnly, inputObject, false, true, doDebug);
    }
}
