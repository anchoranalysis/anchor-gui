/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.util.Optional;
import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.DualCfg;

public class InitAnnotation {

    private Optional<AnnotationWithCfg> annotation;
    private DualCfg initCfg;
    private String initMsg; // A message that loads at the start;

    public InitAnnotation(Optional<AnnotationWithCfg> annotation) {
        this(annotation, null, "");
    }

    public InitAnnotation(Optional<AnnotationWithCfg> annotation, DualCfg initCfg) {
        this(annotation, initCfg, "");
    }

    public InitAnnotation(Optional<AnnotationWithCfg> annotation, DualCfg cfg, String initMsg) {
        super();
        this.annotation = annotation;
        this.initCfg = cfg;
        this.initMsg = initMsg;
    }

    public DualCfg getInitCfg() {
        return initCfg;
    }

    public String getInitMsg() {
        return initMsg;
    }

    public Optional<AnnotationWithCfg> getAnnotation() {
        return annotation;
    }
}
