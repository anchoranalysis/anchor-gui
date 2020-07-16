/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;

@Value
@AllArgsConstructor
public class AnnotationPanelParams {

    private SaveMonitor saveMonitor;
    private RandomNumberGenerator randomNumberGenerator;
    private ISliderState sliderState;
    private ToolErrorReporter errorReporter;
}
