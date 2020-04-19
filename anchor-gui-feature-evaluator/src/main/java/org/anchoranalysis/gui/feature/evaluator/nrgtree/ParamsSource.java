package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

public class ParamsSource {

	private CreateParams<FeatureCalcParams> createParams;
	private FeatureCalculatorMulti<FeatureCalcParams> featureCalculator;

	public ParamsSource(CreateParams<FeatureCalcParams> createParams, FeatureCalculatorMulti<FeatureCalcParams> featureCalculator) {
		super();
		this.createParams = createParams;
		this.featureCalculator = featureCalculator;
	}

	public double calc(Feature<FeatureCalcParams> feature) throws FeatureCalcException {
		try {
			FeatureCalcParams params = createParams.createForFeature(feature);
			return featureCalculator.calc(params, new FeatureList<>(feature)).get(0);
			
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
	}
}
