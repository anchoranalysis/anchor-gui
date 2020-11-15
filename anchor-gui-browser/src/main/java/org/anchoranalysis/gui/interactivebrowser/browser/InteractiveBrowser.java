/*-
 * #%L
 * anchor-gui-browser
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.interactivebrowser.browser;

import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.shared.color.scheme.HSB;
import org.anchoranalysis.bean.shared.color.scheme.Shuffle;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.core.random.RandomNumberGeneratorMersenne;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.FileOpenManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.gui.interactivebrowser.openfile.FileCreatorLoader;
import org.anchoranalysis.gui.interactivebrowser.openfile.OpenFile;
import org.anchoranalysis.gui.interactivebrowser.openfile.OpenFileTypeFactory;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.frame.VideoStatsFrame;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.io.generator.sequence.SequenceMemory;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InteractiveBrowser {

    // How long the splash screen displays for
    private static final int SPLASH_SCREEN_TIME = 2000;
    private static final int NUMBER_COLORS = 20;

    private VideoStatsFrame videoStatsFrame;

    // START REQUIRED ARGUMENTS
    private final InputOutputContext context;
    // END REQUIRED ARGUMENTS
    
    // Manages the available mark evaluators
    private MarkEvaluatorManager markEvaluatorManager;

    private OpenFile openFileCreator;

    private OpenFileTypeFactory openFileTypeFactory;

    public void init(InteractiveBrowserInput interactiveBrowserInput) throws InitException {

        videoStatsFrame = new VideoStatsFrame("Interactive Browser");

        openFileTypeFactory =
                new OpenFileTypeFactory(
                        interactiveBrowserInput.getImporterSettings().getOpenFileImporters());

        displaySplashScreen();

        setup(interactiveBrowserInput);
    }

    public void showWithDefaultView() {
        videoStatsFrame.showWithDefaultView();
    }

    private void setup(InteractiveBrowserInput interactiveBrowserInput) throws InitException {
        SubgrouppedAdder globalSubgroupAdder =
                new SubgrouppedAdder(videoStatsFrame, new DefaultModuleState());

        VideoStatsModuleGlobalParams moduleParams =
                createModuleParams(
                        createExportPopupParams(),
                        createColorIndex(),
                        new RandomNumberGeneratorMersenne(false));

        initMarkEvaluatorManager(interactiveBrowserInput);

        addGUIComponents(
                interactiveBrowserInput,
                moduleParams,
                globalSubgroupAdder,
                createFileOpenManager(globalSubgroupAdder));
    }

    private FileOpenManager createFileOpenManager(SubgrouppedAdder globalSubgroupAdder) {
        return new FileOpenManager(globalSubgroupAdder, videoStatsFrame, context);
    }

    private void initMarkEvaluatorManager(InteractiveBrowserInput interactiveBrowserInput) {
        markEvaluatorManager = new MarkEvaluatorManager(context);

        if (interactiveBrowserInput.getNamedItemMarkEvaluatorList() != null) {
            for (NamedBean<MarkEvaluator> ni :
                    interactiveBrowserInput.getNamedItemMarkEvaluatorList()) {
                markEvaluatorManager.add(ni.getName(), ni.getValue());
            }
        }
    }

    private void addGUIComponents(
            InteractiveBrowserInput interactiveBrowserInput,
            VideoStatsModuleGlobalParams moduleParams,
            SubgrouppedAdder globalSubgroupAdder,
            FileOpenManager fileOpenManager)
            throws InitException {

        FeatureListSrc featureListSrc;
        try {
            featureListSrc = interactiveBrowserInput.createFeatureListSrc(context.common());
        } catch (CreateException e) {
            throw new InitException(e);
        }

        AdderWithEnergy adderWithEnergy =
                new AdderWithEnergy(moduleParams, featureListSrc, globalSubgroupAdder);

        FileCreatorLoader fileCreatorLoader =
                creatorLoader(
                        adderWithEnergy,
                        interactiveBrowserInput.getStackReader(),
                        fileOpenManager,
                        interactiveBrowserInput.getImporterSettings());

        openFileCreator =
                new OpenFile(
                        videoStatsFrame,
                        fileCreatorLoader,
                        openFileTypeFactory,
                        context.getLogger());
        videoStatsFrame.getListFileActions().add(openFileCreator);

        // We add the GUI components
        addGUIComponentsInner(
                adderWithEnergy,
                fileCreatorLoader,
                fileOpenManager,
                interactiveBrowserInput.getListFileCreators());
    }

    private FileCreatorLoader creatorLoader(
            AdderWithEnergy adderWithEnergy,
            StackReader stackReader,
            FileOpenManager fileOpenManager,
            ImporterSettings importerSettings) {
        return adderWithEnergy.createFileCreatorLoader(
                stackReader,
                fileOpenManager,
                markEvaluatorManager,
                importerSettings,
                videoStatsFrame.getLastMarkDisplaySettings());
    }

    private void addGUIComponentsInner(
            AdderWithEnergy adderWithEnergy,
            FileCreatorLoader fileCreatorLoader,
            FileOpenManager fileOpenManager,
            List<FileCreator> fileCreators)
            throws InitException {

        videoStatsFrame.getToolbar().add(openFileCreator);
        videoStatsFrame.getToolbar().addSeparator();

        videoStatsFrame.initBeforeAddingFrames(context.getErrorReporter());

        videoStatsFrame.getToolbar().addSeparator();

        adderWithEnergy.addGlobalSet(videoStatsFrame.getToolbar());

        videoStatsFrame.getToolbar().addSeparator();

        videoStatsFrame.getToolbar().addSeparator();

        // We maintain a mapping between modules and
        videoStatsFrame.addVideoStatsModuleClosedListener(
                evt -> {
                    assert (evt.getModule() != null);
                    fileOpenManager.closeModule(evt.getModule());
                    videoStatsFrame.selectFrame(true);
                });

        fileCreatorLoader.addFileListSummaryModule(fileCreators, videoStatsFrame);

        videoStatsFrame.setDropTarget(
                new CustomDropTarget(
                        openFileCreator,
                        videoStatsFrame,
                        fileCreatorLoader.getImporterSettings(),
                        context.getErrorReporter()));
    }

    private ExportPopupParams createExportPopupParams() {
        return new ExportPopupParams(videoStatsFrame, new SequenceMemory(), context);
    }

    private VideoStatsModuleGlobalParams createModuleParams(
            ExportPopupParams popUpParams,
            ColorIndex colorIndex,
            RandomNumberGenerator randomNumberGenerator) {
        return new VideoStatsModuleGlobalParams(
                popUpParams,
                context.common(),
                videoStatsFrame.getThreadPool(),
                randomNumberGenerator,
                colorIndex,
                videoStatsFrame.getGraphicsConfiguration());
    }

    private ColorIndex createColorIndex() throws InitException {
        try {
            return new Shuffle(new HSB()).colorForEachIndex(NUMBER_COLORS);
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
    }

    private void displaySplashScreen() {
        // Display splash scren
        new SplashScreenTime(
                "/appSplash/anchor_splash.png",
                videoStatsFrame,
                SPLASH_SCREEN_TIME,
                context.getErrorReporter());
    }
}
