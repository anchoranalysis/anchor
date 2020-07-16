/* (C)2020 */
package org.anchoranalysis.io.output.bean;

import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;
import org.anchoranalysis.io.bean.color.generator.HSBColorSetGenerator;
import org.anchoranalysis.io.bean.color.generator.PrependColorSetGenerator;
import org.anchoranalysis.io.bean.color.generator.ShuffleColorSetGenerator;

/*
 *
 * It is very important that init() is run before using the bean. This normally occurs from checkMisconfigured() that
 *   is called automatically from the bean-loading framework
 *
 *   But if the bean is not loaded through this mechanism, please call init explicitly before usage
 */
public class OutputWriteSettings extends AnchorBean<OutputWriteSettings> {

    // START BEAN PROPERTIES
    @BeanField
    private ColorSetGenerator defaultColorSetGenerator =
            new ShuffleColorSetGenerator(new HSBColorSetGenerator());

    /**
     * Specifies a writer bean instance for a particular type of writer (identified by the writer
     * bean class)
     */
    @BeanField @OptionalBean private List<NamedBean<?>> writers;
    // END BEAN PROPERTIES

    private static final String HTML_EXTENSION = "html";
    private static final String XML_EXTENSION = "xml";
    private static final String TEXT_EXTENSION = "txt";
    private static final String SERIALIZED_EXTENSION = "ser";

    // Contains instances for each writer
    private BeanInstanceMap writerInstances;

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        init(defaultInstances);
    }

    public void init(BeanInstanceMap defaultInstances) throws BeanMisconfiguredException {

        // A convenient place to set up our writerInstances, as it is executed once, before
        // getDefaultWriter()
        //  is called, and the defaults are available, and an error message can potentially be
        // thrown.

        writerInstances = new BeanInstanceMap();

        // First load in the defaults
        writerInstances.addFrom(defaultInstances);

        // Then load in the explicitly-specified writers (overriding any existing entries)
        if (writers != null) {
            writerInstances.addFrom(writers);
        }
    }

    public boolean hasBeenInit() {
        return (writerInstances != null);
    }

    /**
     * Gets a writer-instance for type c
     *
     * <p>1. First, it looks for a match among the bean-field 'writers' 2. If no match is found,
     * then it looks among the general default-instances 3. If no match is found, then it returns
     * null.
     *
     * <p>When a writer is returned, it will always inherits from type c.
     *
     * @param c the class identifying which type of writer is sought
     * @return a matching writer, or null.
     */
    public Object getWriterInstance(Class<?> c) {
        assert (writerInstances != null);

        // We look for the default instance, corresponding to the particular class
        return writerInstances.get(c);
    }

    public ColorIndex genDefaultColorIndex(int cols) throws OperationFailedException {
        return new ColorIndexModulo(getDefaultColorSetGenerator().generateColors(cols));
    }

    public ColorIndex genPrependedDefaultColorIndex(int cols, RGBColor color)
            throws OperationFailedException {
        PrependColorSetGenerator prependedColIndex =
                new PrependColorSetGenerator(getDefaultColorSetGenerator(), color);
        return prependedColIndex.generateColors(cols);
    }

    public String getExtensionHTML() {
        return HTML_EXTENSION;
    }

    public String getExtensionSerialized() {
        return SERIALIZED_EXTENSION;
    }

    public String getExtensionXML() {
        return XML_EXTENSION;
    }

    public String getExtensionText() {
        return TEXT_EXTENSION;
    }

    // START BEAN GETTERS AND SETTERS
    public ColorSetGenerator getDefaultColorSetGenerator() {
        return defaultColorSetGenerator;
    }

    public void setDefaultColorSetGenerator(ColorSetGenerator defaultColorSetGenerator) {
        this.defaultColorSetGenerator = defaultColorSetGenerator;
    }

    public List<NamedBean<?>> getWriters() {
        return writers;
    }

    public void setWriters(List<NamedBean<?>> writers) {
        this.writers = writers;
    }
}
