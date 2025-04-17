/*-
 * #%L
 * anchor-plugin-opencv
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
package org.anchoranalysis.test.image.load;

import java.io.File;
import java.nio.file.Path;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.io.TestLoaderImage;

/**
 * Loads some images of a car.
 *
 * <p>The directory {@code car/} should be present in {@code source/test/resources} of any project
 * that uses this class, containing the necessary files:
 *
 * <ul>
 *   <li>{@code car.jpg}
 *   <li>{@code carGrayscale8bit.jpg}
 *   <li>{@code carGrayscale16bit.jpg}
 * </ul>
 *
 * @author Owen Feehan
 */
public class CarImageLoader {

    private static final String PATH_SUBDIRECTORY = "car";

    private static final String PATH_CAR = "car.jpg";

    private static final String PATH_CAR_GRAYSCALE_8_BIT = "carGrayscale8bit.jpg";

    private static final String PATH_CAR_GRAYSCALE_16_BIT = "carGrayscale16bit.tif";

    private TestLoaderImage loader =
            new TestLoaderImage(TestLoader.createFromMavenWorkingDirectory());

    /**
     * Loads and returns the RGB image of a car.
     *
     * @return A Stack containing the RGB image of a car.
     */
    public Stack carRGB() {
        return loadFromSubdirectory(PATH_CAR);
    }

    /**
     * Loads and returns the 8-bit grayscale image of a car.
     *
     * @return A Stack containing the 8-bit grayscale image of a car.
     */
    public Stack carGrayscale8Bit() {
        return loadFromSubdirectory(PATH_CAR_GRAYSCALE_8_BIT);
    }

    /**
     * Loads and returns the 16-bit grayscale image of a car.
     *
     * @return A Stack containing the 16-bit grayscale image of a car.
     */
    public Stack carGrayscale16Bit() {
        return loadFromSubdirectory(PATH_CAR_GRAYSCALE_16_BIT);
    }

    /**
     * Loads the RGB image of a car and wraps it in an EnergyStackWithoutParameters.
     *
     * @return An EnergyStackWithoutParameters containing the RGB image of a car.
     */
    public EnergyStackWithoutParameters carRGBAsEnergy() {
        return new EnergyStackWithoutParameters(carRGB());
    }

    /**
     * Loads the 8-bit grayscale image of a car and wraps it in an EnergyStackWithoutParameters.
     *
     * @return An EnergyStackWithoutParameters containing the 8-bit grayscale image of a car.
     */
    public EnergyStackWithoutParameters carGrayscale8BitAsEnergy() {
        return new EnergyStackWithoutParameters(carGrayscale8Bit());
    }

    /**
     * Loads the 16-bit grayscale image of a car and wraps it in an EnergyStackWithoutParameters.
     *
     * @return An EnergyStackWithoutParameters containing the 16-bit grayscale image of a car.
     */
    public EnergyStackWithoutParameters carGrayscale16BitAsEnergy() {
        return new EnergyStackWithoutParameters(carGrayscale16Bit());
    }

    /**
     * Returns the root directory path for the model files.
     *
     * @return A Path object representing the root directory of the model files.
     */
    public Path modelDirectory() {
        return loader.getLoader().getRoot();
    }

    private Stack loadFromSubdirectory(String filename) {
        return loader.openStackFromTestPath(PATH_SUBDIRECTORY + File.separator + filename);
    }
}
