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
