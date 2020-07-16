/*-
 * #%L
 * anchor-gui-feature-evaluator
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
package org.anchoranalysis.gui.feature.evaluator.nrgtree;


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

public class ParamsSource {

	private CreateFeatureInput<FeatureInput> createParams;
	private FeatureCalculatorMulti<FeatureInput> featureCalculator;

	public ParamsSource(CreateFeatureInput<FeatureInput> createParams, FeatureCalculatorMulti<FeatureInput> featureCalculator) {
		super();
		this.createParams = createParams;
		this.featureCalculator = featureCalculator;
	}

	public double calc(Feature<FeatureInput> feature) throws FeatureCalcException {
		try {
			FeatureInput params = createParams.createForFeature(feature);
			return featureCalculator.calc(
				params,
				FeatureListFactory.from(feature)
			).get(0);
			
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
	}
}
