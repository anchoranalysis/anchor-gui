/* (C)2020 */
package org.anchoranalysis.gui.bean.exporttask;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class ExportTaskFailedException extends AnchorCheckedException {

    private static final long serialVersionUID = -7375672476538506254L;

    public ExportTaskFailedException(String string) {
        super(string);
    }

    public ExportTaskFailedException(Exception exc) {
        super(exc);
    }
}
