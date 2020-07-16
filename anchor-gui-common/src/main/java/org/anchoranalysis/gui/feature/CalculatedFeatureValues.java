/* (C)2020 */
package org.anchoranalysis.gui.feature;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.feature.calc.results.ResultsVector;

public class CalculatedFeatureValues {

    private DoubleMatrix2D matrix;

    public CalculatedFeatureValues(int numMarks, int numFeatures) {

        matrix = DoubleFactory2D.dense.make(numMarks, numFeatures);
    }

    public void set(int markIndex, ResultsVector rv) {
        for (int i = 0; i < rv.length(); i++) {
            set(markIndex, i, rv.get(i));
        }
    }

    public void set(int markIndex, int featureIndex, double value) {
        matrix.set(markIndex, featureIndex, value);
    }

    public double getFeatureValue(int markIndex, int featureIndex) {
        return matrix.get(markIndex, featureIndex);
    }

    public double getFeatureMin(int featureIndex) {
        return matrix.viewColumn(featureIndex).aggregate(Functions.min, Functions.identity);
    }

    public double getFeatureMax(int featureIndex) {
        return matrix.viewColumn(featureIndex).aggregate(Functions.max, Functions.identity);
    }

    public double getFeatureMean(int featureIndex) {

        DoubleMatrix1D column = matrix.viewColumn(featureIndex);
        return column.zSum() / column.size();
    }

    public double getFeatureMedian(int featureIndex) {
        DoubleMatrix1D column = matrix.viewColumn(featureIndex);
        return cern.colt.matrix.doublealgo.Statistic.bin(column).median();
    }

    public double getFeatureQuantile(int featureIndex, double quantile) {
        DoubleMatrix1D column = matrix.viewColumn(featureIndex);
        return cern.colt.matrix.doublealgo.Statistic.bin(column).quantile(quantile);
    }

    public List<Double> createFeatureValuesList(int featureIndex) {

        DoubleMatrix1D column = matrix.viewColumn(featureIndex);

        ArrayList<Double> list = new ArrayList<>(column.size());
        for (int i = 0; i < column.size(); i++) {
            list.add(column.get(i));
        }
        return list;
    }
}
