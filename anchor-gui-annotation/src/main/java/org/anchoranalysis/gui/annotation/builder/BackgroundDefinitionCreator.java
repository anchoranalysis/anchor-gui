package org.anchoranalysis.gui.annotation.builder;

/*-
 * #%L
 * anchor-gui-annotation
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

import org.anchoranalysis.annotation.io.bean.background.AnnotationBackgroundDefinition;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionIgnoreContains;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionMapped;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;

class BackgroundDefinitionCreator {

	/**
	 * Creates a changeable-background from an existing background and an AnnotationBackgroundDefinition
	 * 
	 * @param existingBackground current background to the annotation
	 * @param abd defines how to construct set of multiple backgrounds to choose from
	 * @return
	 */
	public static ChangeableBackgroundDefinition create( AnnotationBackground existingBackground, AnnotationBackgroundDefinition abd ) {
		ChangeableBackgroundDefinition def = backgroundDefinition(existingBackground, abd.getBackgroundStackMap());
		
		if (!abd.getIgnoreContains().isEmpty()) {
			return new ChangeableBackgroundDefinitionIgnoreContains(def, abd.getIgnoreContains());
		} else {
			return def;
		}
	}
	
	private static ChangeableBackgroundDefinition backgroundDefinition( AnnotationBackground background, StringMap map ) {
		if (map!=null) {
			return new ChangeableBackgroundDefinitionMapped(
				background.getBackgroundSetOp(),
				map
			);
		} else {
			return new ChangeableBackgroundDefinitionSimple( background.getBackgroundSetOp() );
		}
	}
}
