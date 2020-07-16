/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterBean;
import org.anchoranalysis.image.io.bean.generator.CombineRasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class Combined<T> extends CreateRasterGenerator<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<CreateRasterGenerator<T>> createRasterGeneratorList;

    @BeanField @Getter @Setter private ArrangeRasterBean arrangeRaster;
    // END BEAN PROPERTIES

    @Override
    public IterableObjectGenerator<MappedFrom<T>, Stack> createGenerator(ExportTaskParams params)
            throws CreateException {

        CombineRasterGenerator<MappedFrom<T>> combineGenerator = new CombineRasterGenerator<>();
        combineGenerator.setArrangeRaster(arrangeRaster);

        for (CreateRasterGenerator<T> creator : createRasterGeneratorList) {
            combineGenerator.add(creator.createGenerator(params));
        }

        return combineGenerator.createGenerator();
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {

        for (CreateRasterGenerator<T> creator : createRasterGeneratorList) {

            if (!creator.hasNecessaryParams(params)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }
}
