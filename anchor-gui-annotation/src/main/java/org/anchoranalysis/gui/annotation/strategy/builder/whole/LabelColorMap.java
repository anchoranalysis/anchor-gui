/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.gui.annotation.bean.label.AnnotationLabel;

public class LabelColorMap {

    private Map<String, Color> map = new HashMap<>();

    public LabelColorMap(Collection<AnnotationLabel> labels) {
        for (AnnotationLabel al : labels) {
            map.put(al.getUniqueLabel(), al.getColor().toAWTColor());
        }
    }

    public Color get(String key) {
        return map.get(key);
    }
}
