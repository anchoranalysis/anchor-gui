package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import static org.anchoranalysis.gui.interactivebrowser.openfile.importer.CreatorFactory.*;

import java.io.File;
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.FileFeatureEvaluatorCreator;
import org.anchoranalysis.gui.bean.filecreator.NamedSingleStackCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.NrgSchemeCreatorState;
import org.anchoranalysis.image.io.input.StackInput;
import org.anchoranalysis.io.bean.input.InputManager;

class XMLBeanListHelper {
	
	public static FileCreator createSingleStack( InputManager<? extends StackInput> inputManager, File f) {
		return CreatorFactory.create(
			new NamedSingleStackCreator(),				
			inputManager,
			(creator,input)-> creator.setInput(inputManager),			
			f,
			"raster-set"
		);
	}
	
	@SuppressWarnings("unchecked")
	public static FileCreator creatorForList( List<Object> list, File f ) throws CreateException {
		if (list.size()==0) {
			throw new CreateException("There are no bean in the list, so cannot figure out the type");
		}
		
		Object bean = (Object) list;	// Upcast to make down-casting easy later
		Object firstItem = list.get(0);
		
		if (firstItem instanceof NamedBean) {
			
			NamedBean<AnchorBean<?>> castFirstItem = (NamedBean<AnchorBean<?>>) firstItem;  
			return creatorForNamedBean(castFirstItem, bean, f);

		} else {
			throw new CreateException(
				String.format("The class of list-item is not currently supported: %s", firstItem.getClass() )
			);
		}
	}
	
	/** Can return null */
	@SuppressWarnings("unchecked")
	private static FileCreator creatorForNamedBean( NamedBean<AnchorBean<?>> namedBean, Object bean, File f ) throws CreateException {
		
		AnchorBean<?> item = namedBean.getItem(); 
		
		if (item instanceof FeatureListProvider) {
			return createFeatureEvaluator( (List<NamedBean<FeatureListProvider>>) bean, f);
		} else {
			throw new CreateException(
				String.format("The class of named-bean is not currently supported: %s", item.getClass() )
			);
		}
	}
	
	private static FileCreator createFeatureEvaluator(List<NamedBean<FeatureListProvider>> features, File f) {
		return create(
			new FileFeatureEvaluatorCreator(),
			features,
			(creator,fl)-> {
				creator.setListFeatures(fl);
				creator.setNrgSchemeCreator(
					NrgSchemeCreatorState.instance().getItem()
				);
			},
			f,
			"feature evaluator"
		);
	}


}
