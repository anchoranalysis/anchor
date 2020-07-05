package org.anchoranalysis.io.manifest.match;

/*-
 * #%L
 * anchor-io-manifest
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class FolderWriteManifestMatch implements Match<FolderWrite> {

	private Match<ManifestDescription> manifestDescriptionMatch;
	private Match<SequenceType> sequenceTypeMatch;
	

	// If match is null, we match everything
	public FolderWriteManifestMatch(Match<ManifestDescription> manifestDescriptionMatch) {
		super();
		this.manifestDescriptionMatch = manifestDescriptionMatch;
		this.sequenceTypeMatch = null;
	}
	
	// If match is null, we match everything
	public FolderWriteManifestMatch(
			Match<ManifestDescription> manifestDescriptionMatch,
			Match<SequenceType> sequenceTypeMatch) {
		super();
		this.manifestDescriptionMatch = manifestDescriptionMatch;
		this.sequenceTypeMatch = sequenceTypeMatch;
	}


	@Override
	public boolean matches(FolderWrite obj) {
		
		if (obj.getManifestFolderDescription()==null) {
			return false;
		}
		
		if (manifestDescriptionMatch!=null && !manifestDescriptionMatch.matches( obj.getManifestFolderDescription().getFileDescription() )) {
			return false;
		}
		
		return !(sequenceTypeMatch!=null && !sequenceTypeMatch.matches( obj.getManifestFolderDescription().getSequenceType() ));
	}

}
