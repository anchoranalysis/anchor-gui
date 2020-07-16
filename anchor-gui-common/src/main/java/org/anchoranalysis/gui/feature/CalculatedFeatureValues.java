/*-
 * #%L
 * anchor-gui-common
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
