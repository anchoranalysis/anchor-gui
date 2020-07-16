/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public abstract class CreateRasterGenerator<T> extends AnchorBean<CreateRasterGenerator<T>> {

    public abstract IterableObjectGenerator<MappedFrom<T>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException;

    public abstract boolean hasNecessaryParams(ExportTaskParams params);
}
