package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

public class ParamsSource {

	private CreateParams<FeatureInput> createParams;
	private FeatureCalculatorMulti<FeatureInput> featureCalculator;

	public ParamsSource(CreateParams<FeatureInput> createParams, FeatureCalculatorMulti<FeatureInput> featureCalculator) {
		super();
		this.createParams = createParams;
		this.featureCalculator = featureCalculator;
	}

	public double calc(Feature<FeatureInput> feature) throws FeatureCalcException {
		try {
			FeatureInput params = createParams.createForFeature(feature);
			return featureCalculator.calc(params, new FeatureList<>(feature)).get(0);
			
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
	}
}
