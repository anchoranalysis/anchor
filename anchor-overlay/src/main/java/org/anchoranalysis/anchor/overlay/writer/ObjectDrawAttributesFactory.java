package org.anchoranalysis.anchor.overlay.writer;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterMaskFromOverlay;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ObjectDrawAttributesFactory {
	
	public static ObjectDrawAttributes createFromOverlays(
		ColoredOverlayCollection overlays,
		IDGetter<Overlay> idGetter,
		IDGetter<ObjectWithProperties> idGetterColor
	) {
		return new ObjectDrawAttributes(
			overlays.getColorList(),
			createMaskIDGetter(overlays, idGetter),
			idGetterColor
		);
	}
	
	private static IDGetter<ObjectWithProperties> createMaskIDGetter( ColoredOverlayCollection oc, IDGetter<Overlay> idGetter) {
		return new IDGetterMaskFromOverlay(idGetter, oc, true);
	}
}
