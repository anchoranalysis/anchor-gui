/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import org.anchoranalysis.core.log.Logger;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

public class TreeTableProperties {

    private RowModel rowModel;
    private RenderDataProvider renderDataProvider;
    private String title;
    private Logger logger;

    public TreeTableProperties(
            RowModel rowModel, RenderDataProvider renderDataProvider, String title, Logger logger) {
        super();
        this.rowModel = rowModel;
        this.renderDataProvider = renderDataProvider;
        this.title = title;
        this.logger = logger;
    }

    public RowModel getRowModel() {
        return rowModel;
    }

    public RenderDataProvider getRenderDataProvider() {
        return renderDataProvider;
    }

    public String getTitle() {
        return title;
    }

    public Logger getLogErrorReporter() {
        return logger;
    }
}
