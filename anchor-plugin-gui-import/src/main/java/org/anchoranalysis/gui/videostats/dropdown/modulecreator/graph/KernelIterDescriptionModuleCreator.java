/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.cfgnrg.StatePanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelFrameHistory;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.kernel.KernelIterDescriptionNavigatorPanel;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class KernelIterDescriptionModuleCreator extends VideoStatsModuleCreatorContext {

    private final FinderHistoryFolder<KernelIterDescription> finderKernelIterDescriptionHistory;
    private final FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer;

    // private static Log log = LogFactory.getLog(GraphNRGModuleCreator.class);

    public KernelIterDescriptionModuleCreator(
            FinderHistoryFolder<KernelIterDescription> finderKernelIterDescriptionHistory,
            FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer) {
        super();
        this.finderKernelIterDescriptionHistory = finderKernelIterDescriptionHistory;
        this.finderKernelProposer = finderKernelProposer;
    }

    @Override
    public boolean precondition() {
        if (!finderKernelIterDescriptionHistory.exists()) {
            return false;
        }

        if (!finderKernelProposer.exists()) {
            return false;
        }
        return true;
    }

    @Override
    public Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException {

        ErrorReporter errorReporter = mpg.getLogger().errorReporter();

        try {
            KernelProposer<CfgNRGPixelized> kp = finderKernelProposer.get();

            // TODO We might end up calling this multiple times, let's some up with a more elegant
            // solution at some point
            kp.init();

            StatePanel<KernelIterDescription> panel =
                    new KernelIterDescriptionNavigatorPanel(
                            finderKernelIterDescriptionHistory.get().getCntr(), kp);

            StatePanelFrameHistory<KernelIterDescription> frame =
                    new StatePanelFrameHistory<>(namePrefix, true);
            frame.init(
                    defaultStateManager.getState().getLinkState().getFrameIndex(),
                    finderKernelIterDescriptionHistory.get(),
                    panel,
                    errorReporter);
            frame.controllerSize().configureSize(500, 500);

            return Optional.of(frame.moduleCreator());

        } catch (GetOperationFailedException | InitException | IOException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public String title() {
        return "Kernel History Navigator";
    }

    @Override
    public Optional<String> shortTitle() {
        return Optional.empty();
    }
}
