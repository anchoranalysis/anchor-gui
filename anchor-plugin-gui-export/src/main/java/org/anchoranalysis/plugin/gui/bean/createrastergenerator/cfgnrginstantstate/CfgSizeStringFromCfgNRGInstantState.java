package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateStringRasterGenerator;

/*
 * #%L
 * anchor-gui
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


import ch.ethz.biol.cell.mpp.nrg.CfgNRG;

public class CfgSizeStringFromCfgNRGInstantState extends CreateStringRasterGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 153327858028426724L;
	
	// START BEAN PROPERTIES
	@BeanField @AllowEmpty
	private String prefix = "";
	// END BEAN PROPERTIES
	
	@Override
	public String getBeanDscr() {
		return getBeanName();
	}

	@Override
	protected String extractStringFrom( CfgNRG cfgNRG ) {
		if (cfgNRG!=null) {
			return prefix + String.valueOf( cfgNRG.getCfg().size() );
		} else {
			return prefix + "0";
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
