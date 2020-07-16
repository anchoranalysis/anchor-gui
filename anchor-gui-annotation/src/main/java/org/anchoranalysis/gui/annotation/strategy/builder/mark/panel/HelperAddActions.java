/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.frame.cfgproposer.CfgProposerMouseClickAdapter;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.ControllerMouse;
import org.anchoranalysis.gui.frame.overlays.ExtractOverlays;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelMark;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelTool;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperAddActions {

    public static void apply(
            ControllerAction action,
            ExtractOverlays extractOverlays,
            AnnotationPanelParams params,
            PanelTool panelTool,
            PanelMark panelMark) {
        addClickListener(
                extractOverlays,
                action.mouse(),
                panelTool,
                params.getSliderState(),
                params.getRandomNumberGenerator(),
                params.getErrorReporter().getErrorReporter());

        setupKeystrokesOnInputMap(action.keyboard().getInputMap());

        AddConfirmRotate.apply(
                panelTool, panelMark, params.getSliderState(), action.keyboard(), action.mouse());
    }

    private static void setupKeystrokesOnInputMap(InputMap inputMap) {
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "confirmMark");
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "confirmMark");
        inputMap.put(KeyStroke.getKeyStroke("INSERT"), "confirmMark");
        inputMap.put(KeyStroke.getKeyStroke('z'), "confirmMark");
        inputMap.put(KeyStroke.getKeyStroke('y'), "confirmMark");

        //			inputMap.put( KeyStroke.getKeyStroke('d'), "removeMark");
        //			inputMap.put( KeyStroke.getKeyStroke("DELETE"), "removeMark");
        //			inputMap.put( KeyStroke.getKeyStroke("BACKSPACE"), "removeMark");

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "rotateToolRight");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "rotateToolLeft");
    }

    private static void addClickListener(
            ExtractOverlays extractOverlays,
            ControllerMouse controllerMouse,
            PanelTool panelTool,
            ISliderState sliderState,
            RandomNumberGenerator rng,
            ErrorReporter errorReporter) {
        CfgProposerMouseClickAdapter clickListener =
                new CfgProposerMouseClickAdapter(
                        extractOverlays,
                        sliderState,
                        () -> panelTool.getTool().evaluatorWithContextGetter(),
                        rng,
                        errorReporter);
        clickListener.addCfgProposedListener(
                proposedCfg -> panelTool.getTool().proposed(proposedCfg));

        controllerMouse.addMouseListener(clickListener, false);
    }
}
