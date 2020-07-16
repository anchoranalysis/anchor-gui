/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.reassign.JDropdownButton;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class BoundVideoStatsModuleDropDown {

    private VideoStatsModuleDropdown delegate;

    private String name;

    public String getName() {
        return name;
    }

    private BoundVideoStatsModuleDropDown() {}

    public BoundVideoStatsModuleDropDown(String name, String iconPath) {
        this.name = name;
        ImageIcon icon = new IconFactory().icon(iconPath);
        delegate = new VideoStatsModuleDropdown(name, icon);
    }

    public BoundVideoStatsModuleDropDown createChild(String name) {
        BoundVideoStatsModuleDropDown child = new BoundVideoStatsModuleDropDown();
        child.name = this.name + ": " + name;
        child.delegate = this.delegate;
        return child;
    }

    public void addModule(
            String itemName,
            VideoStatsModuleCreatorAndAdder creator,
            InteractiveThreadPool threadPool,
            Logger logger) {
        delegate.getRootMenu()
                .add(
                        new VideoStatsOperationFromCreatorAndAdder(
                                itemName, creator, threadPool, logger));
    }

    public void addModule(NamedModule module, InteractiveThreadPool threadPool, Logger logger) {
        addModule(module.getTitle(), module, threadPool, logger);
    }

    public void addModule(
            String itemName, NamedModule module, InteractiveThreadPool threadPool, Logger logger) {
        addModule(itemName, module.getCreator(), threadPool, logger);
    }

    public void addModule(
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            SingleContextualModuleCreator creator,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws MenuAddException {
        addModule(
                creator.createSingle(namePrefix, adder, mpg), mpg.getThreadPool(), mpg.getLogger());
    }

    public VideoStatsModuleCreatorAndAdder addModule(
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            ContextualModuleCreator creator,
            VideoStatsModuleGlobalParams mpg)
            throws MenuAddException {
        try {
            NamedModule[] moduleToAdd = creator.create(getNameAsPrefix(), adder, mpg);
            return delegate.addNamedModules(moduleToAdd, mpg.getThreadPool(), mpg.getLogger());
        } catch (CreateException e) {
            throw new MenuAddException(e);
        }
    }

    public void addModule(
            String itemName,
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            VideoStatsModuleCreator creator,
            InteractiveThreadPool threadPool,
            Logger logger)
            throws MenuAddException {
        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(adder, creator);
        addModule(itemName, creatorAndAdder, threadPool, logger);
    }

    public IAddModuleToMenu createAddModuleToMenu(
            final OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder) {
        return new IAddModuleToMenu() {

            @Override
            public VideoStatsModuleCreatorAndAdder addModuleToMenu(
                    VideoStatsOperationMenu menu,
                    ContextualModuleCreator creator,
                    boolean useShortNames,
                    VideoStatsModuleGlobalParams mpg)
                    throws MenuAddException {
                try {
                    NamedModule[] moduleToAdd = creator.create(getNameAsPrefix(), adder, mpg);
                    return delegate.addNamedModules(
                            menu, moduleToAdd, mpg.getThreadPool(), mpg.getLogger(), useShortNames);
                } catch (CreateException e) {
                    throw new MenuAddException(e);
                }
            }
        };
    }

    public JButton getButton() {
        return delegate.getDropdownButton().getButton();
    }

    public JDropdownButton getDropdownButton() {
        return delegate.getDropdownButton();
    }

    public int getNumItems() {
        return delegate.getNumItems();
    }

    private String getNameAsPrefix() {
        return name;
    }

    public VideoStatsOperationMenu getRootMenu() {
        return delegate.getRootMenu();
    }

    public IOpenedFileGUI openedFileGUI() {
        return delegate;
    }
}
