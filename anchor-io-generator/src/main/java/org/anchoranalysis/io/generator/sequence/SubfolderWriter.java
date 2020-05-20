package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.folder.FolderWriteIndexableOutputName;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.Writer;

public class SubfolderWriter extends SequenceWriter {

	private BoundOutputManager parentOutputManager;
	private IndexableOutputNameStyle outputNameStyle;
	private ManifestDescription folderManifestDescription;
	
	private BoundOutputManager subFolderOutputManager = null;
	private boolean checkIfAllowed;
	private String subfolderName;
	
	public SubfolderWriter(
			BoundOutputManager outputManager,
			String subfolderName,			
			IndexableOutputNameStyle outputNameStyle,
			ManifestDescription folderManifestDescription, boolean checkIfAllowed) {
		super();
		this.parentOutputManager = outputManager;
		this.outputNameStyle = outputNameStyle;
		this.folderManifestDescription = folderManifestDescription;
		this.checkIfAllowed = checkIfAllowed;
		this.subfolderName = subfolderName;
	}

	private BoundOutputManager createSubfolder(boolean suppressSubfolder, ManifestFolderDescription folderDescription, 	FolderWriteIndexableOutputName subFolderWrite) throws OutputWriteFailedException {
		if (suppressSubfolder) {
			return parentOutputManager;
		} else {
			Writer writer = checkIfAllowed ? parentOutputManager.getWriterCheckIfAllowed() : parentOutputManager.getWriterAlwaysAllowed();
			return writer.bindAsSubFolder(subfolderName, folderDescription, subFolderWrite );
		}
	}
	
	@Override
	public void init( FileType[] fileTypes, SequenceType sequenceType, boolean suppressSubfolder ) throws InitException {
		
		ManifestFolderDescription folderDescription = new ManifestFolderDescription();
		
		if (fileTypes.length==0) {
			throw new InitException("The generator has no associated FileTypes");
		}
		
		folderDescription.setFileDescription( createFolderDescription(fileTypes) );
		folderDescription.setSequenceType( sequenceType );
		
 		// FolderWriteIndexableOutputName
		FolderWriteIndexableOutputName subFolderWrite = new FolderWriteIndexableOutputName(
			outputNameStyle
		);
		for (FileType fileType : fileTypes) {
			subFolderWrite.addFileType(fileType);
		}

		
		try {
			this.subFolderOutputManager = createSubfolder( suppressSubfolder, folderDescription, subFolderWrite );
		} catch (OutputWriteFailedException e) {
			throw new InitException(e);
		}
		
	}

	@Override
	public boolean isOn() {
		return (this.subFolderOutputManager!=null);
	}

	@Override
	public void write(Operation<Generator,OutputWriteFailedException> generator, String index) throws OutputWriteFailedException {
		
		if (!isOn()) {
			return;
		}
		
		if (checkIfAllowed) {
			this.subFolderOutputManager.getWriterCheckIfAllowed().write(outputNameStyle, generator, index );
		} else {
			this.subFolderOutputManager.getWriterAlwaysAllowed().write( outputNameStyle, generator, index );
		}
	}
	
	
	
	// Requires the iterableGenerator to be in a valid state
	private ManifestDescription createFolderDescription( FileType[] fileTypes  ) {
		if (folderManifestDescription!=null) {
			return folderManifestDescription;
		} else {
			return guessFolderDescriptionFromFiles( fileTypes );
		}
	}
	
	// Attempts to come up with a description of the folder from the underlying file template
	//  If there is only one filetype, we use it
	//  If there is more than one, we look for bits which are the same
	private static ManifestDescription guessFolderDescriptionFromFiles( FileType[] fileTypes ) {
		
		assert( fileTypes.length>0);
		
		if (fileTypes.length==1) {
			return fileTypes[0].getManifestDescription();
		}
		
		String function = "";
		String type = "";
		boolean first = true;
		for (FileType fileType : fileTypes ) {
			String functionItr = fileType.getManifestDescription().getFunction();
			String typeItr = fileType.getManifestDescription().getType();
			
			if (first) {
				// FIRST ITERATION
				function = functionItr;
				type = typeItr;
				first = false;
			} else {
				// SUBSEQUENT ITERATIONS
				if (!functionItr.equals(function)) {
					function = "combined";
				}
				
				if (!typeItr.equals(type)) {
					type = "combined";
				}
			}
		}
		
		ManifestDescription manifestDescription = new ManifestDescription(type, function);
		manifestDescription.setFunction(function);
		manifestDescription.setType(type);
		return manifestDescription;
	}

	@Override
	public BoundOutputManager getOutputManagerForFiles() {
		return subFolderOutputManager;
	}
	

}
