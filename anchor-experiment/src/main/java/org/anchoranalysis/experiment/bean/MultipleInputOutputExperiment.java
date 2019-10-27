package org.anchoranalysis.experiment.bean;

/*
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifierSimple;
import org.anchoranalysis.experiment.bean.io.InputOutputExperiment;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.descriptivename.DescriptiveNameFromFile;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.input.InputFromManager;

// Not finished
public class MultipleInputOutputExperiment<T extends InputFromManager> extends Experiment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FileProvider inputManagerBeanPathProvider;
	
	@BeanField
	private DescriptiveNameFromFile descriptiveNameFromFile;
	
	@BeanField
	private InputOutputExperiment<T> experiment;
	
	@BeanField
	private String version = "1.0";
	
	@BeanField
	private ExperimentIdentifier experimentIdentifier = null;
	// END BEAN PROPERTIES
	
	@Override
	public void doExperiment(ExperimentExecutionArguments expArgs)
			throws ExperimentExecutionException {
			
		Collection<File> files;
		try {
			files = inputManagerBeanPathProvider.matchingFiles(
				ProgressReporterNull.get(),
				expArgs.createInputContext()
			);
		} catch (IOException e1) {
			throw new ExperimentExecutionException(e1);
		}
		
		try {
		
			int i = 0;
			for( File f : files ) {
				String name = descriptiveNameFromFile.createDescriptiveNameOrElse(
					f,
					i++,
					"Invalid Name"
				);
				System.out.printf("Starting\t%03d:\t%s\tat %s%n", i, name, f.getPath() );
				
				InputManager<T> inputManager = BeanXmlLoader.loadBean(f.toPath(), "bean");

				experiment.setInput(inputManager);
				experiment.setExperimentIdentifier( new ExperimentIdentifierSimple(name,version));
				System.out.printf("Ending   \t%03d:\t%s\tat %s%n", i, name, f.getPath() );

				experiment.doExperiment(expArgs);
			}
		
		} catch (BeanXmlException e ) {
			System.out.printf("Ending early due to exception" );
			throw new ExperimentExecutionException(e);
		}
	}
	
	

	public FileProvider getInputManagerBeanPathProvider() {
		return inputManagerBeanPathProvider;
	}

	public void setInputManagerBeanPathProvider(
			FileProvider inputManagerBeanPathProvider) {
		this.inputManagerBeanPathProvider = inputManagerBeanPathProvider;
	}

	public DescriptiveNameFromFile getDescriptiveNameFromFile() {
		return descriptiveNameFromFile;
	}

	public void setDescriptiveNameFromFile(
			DescriptiveNameFromFile descriptiveNameFromFile) {
		this.descriptiveNameFromFile = descriptiveNameFromFile;
	}

	public InputOutputExperiment<T> getExperiment() {
		return experiment;
	}

	public void setExperiment(InputOutputExperiment<T> experiment) {
		this.experiment = experiment;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ExperimentIdentifier getExperimentIdentifier() {
		return experimentIdentifier;
	}

	public void setExperimentIdentifier(ExperimentIdentifier experimentIdentifier) {
		this.experimentIdentifier = experimentIdentifier;
	}

}
