/* (C)2020 */
package org.anchoranalysis.gui.annotation.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.annotation.io.bean.background.AnnotationBackgroundDefinition;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionIgnoreContains;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionMapped;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BackgroundDefinitionCreator {

    public static ChangeableBackgroundDefinition create(
            AnnotationBackground existingBackground, AnnotationBackgroundDefinition abd) {
        ChangeableBackgroundDefinition def =
                backgroundDefinition(existingBackground, abd.getBackgroundStackMap());

        if (!abd.getIgnoreContains().isEmpty()) {
            return new ChangeableBackgroundDefinitionIgnoreContains(def, abd.getIgnoreContains());
        } else {
            return def;
        }
    }

    private static ChangeableBackgroundDefinition backgroundDefinition(
            AnnotationBackground background, StringMap map) {
        if (map != null) {
            return new ChangeableBackgroundDefinitionMapped(background.getBackgroundSetOp(), map);
        } else {
            return new ChangeableBackgroundDefinitionSimple(background.getBackgroundSetOp());
        }
    }
}
