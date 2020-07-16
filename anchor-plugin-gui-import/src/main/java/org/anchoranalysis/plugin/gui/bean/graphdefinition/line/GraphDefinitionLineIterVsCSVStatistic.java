/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public abstract class GraphDefinitionLineIterVsCSVStatistic extends GraphDefinition<CSVStatistic> {

    private LinePlot<CSVStatistic> delegate;

    private String title;
    private String shortTitle;

    public GraphDefinitionLineIterVsCSVStatistic(
            String title,
            String[] seriesName,
            String yAxisLabel,
            YValGetter<CSVStatistic> yValGetter,
            GraphColorScheme graphColorScheme) {
        this(title, title, seriesName, yAxisLabel, yValGetter, graphColorScheme);
    }

    public GraphDefinitionLineIterVsCSVStatistic(
            String title,
            String shortTitle,
            String[] seriesName,
            String yAxisLabel,
            YValGetter<CSVStatistic> yValGetter,
            GraphColorScheme graphColorScheme) {
        this.title = title;
        this.shortTitle = shortTitle;

        delegate = new LinePlot<>(getTitle(), seriesName, yValGetter);
        delegate.getLabels().setX("Iteration");
        delegate.getLabels().setY(yAxisLabel);
        delegate.setGraphColorScheme(graphColorScheme);
    }

    @Override
    public GraphInstance create(
            Iterator<CSVStatistic> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        return delegate.create(items, domainLimits, rangeLimits);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getShortTitle() {
        return shortTitle;
    }
}
