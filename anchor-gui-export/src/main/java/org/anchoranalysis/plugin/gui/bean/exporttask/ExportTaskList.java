/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;

@NoArgsConstructor
public class ExportTaskList extends AnchorBean<ExportTaskList> implements Iterable<ExportTaskBean> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<ExportTaskBean> list = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    public Iterator<ExportTaskBean> iterator() {
        return list.iterator();
    }

    public boolean add(ExportTaskBean e) {
        return list.add(e);
    }
}
