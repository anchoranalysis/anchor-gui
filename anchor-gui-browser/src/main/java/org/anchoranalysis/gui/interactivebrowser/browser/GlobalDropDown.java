/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.browser;

import javax.swing.JButton;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.FeatureEvaluatorCreator;

class GlobalDropDown {

    private BoundVideoStatsModuleDropDown delegate;
    private IAddVideoStatsModule adder;

    public GlobalDropDown(IAddVideoStatsModule adder) {

        delegate = new BoundVideoStatsModuleDropDown("Global", "/toolbarIcon/big_circle.png");
        this.adder = adder;
    }

    public void init(FeatureListSrc src, VideoStatsModuleGlobalParams mpg) throws InitException {

        try {
            delegate.addModule(
                    "Selected Mark Properties",
                    new IdentityOperationWithProgressReporter<>(adder),
                    new FeatureEvaluatorCreator(src, mpg.getLogger()),
                    mpg.getThreadPool(),
                    mpg.getLogger());

        } catch (MenuAddException e) {
            throw new InitException(e);
        }
    }

    public JButton getButton() {
        return delegate.getButton();
    }
}
