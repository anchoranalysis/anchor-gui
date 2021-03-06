/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.videostats.dropdown.opened;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JPopupMenu;
import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.BridgeElementWithIndex;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.frame.multioverlay.RasterMultiCreator;
import org.anchoranalysis.gui.frame.multiraster.NamedRasterSet;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.DualMenuWrapper;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.MultiInput;
import org.anchoranalysis.gui.videostats.modulecreator.RasterMultiModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperation;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationOrMenu;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationSequence;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.gui.videostats.operation.combine.VideoStatsOperationCombine;
import org.anchoranalysis.overlay.IndexableOverlays;

public class OpenedFileGUIMultipleDropDown {

    @Getter private JPopupMenu popupMenu = new JPopupMenu();
    private AddVideoStatsModule adder;
    private VideoStatsModuleGlobalParams mpg;
    private MarkDisplaySettings markDisplaySettings;

    public OpenedFileGUIMultipleDropDown(
            AddVideoStatsModule adder,
            List<OpenedFileGUI> listOpenedGUI,
            VideoStatsModuleGlobalParams moduleParamsGlobal,
            MarkDisplaySettings markDisplaySettings) {
        if (listOpenedGUI.isEmpty()) {
            return;
        }

        this.adder = adder;
        this.mpg = moduleParamsGlobal;
        this.markDisplaySettings = markDisplaySettings;

        List<VideoStatsOperationMenu> firstList = new ArrayList<>();
        for (OpenedFileGUI mdd : listOpenedGUI) {
            firstList.add(mdd.getRootMenu());
        }

        VideoStatsOperationMenu out = new VideoStatsOperationMenu(new DualMenuWrapper(popupMenu));

        VideoStatsOperationMenu individually = out.createSubMenu("Individually", false);
        VideoStatsOperationMenu combined = out.createSubMenu("Combined", false);

        traverse(firstList, individually, combined, markDisplaySettings);
    }

    private void traverse(
            List<VideoStatsOperationMenu> listIn,
            VideoStatsOperationMenu outIndividually,
            VideoStatsOperationMenu outCombined,
            MarkDisplaySettings markDisplaySettings) {

        VideoStatsOperationMenu first = listIn.get(0);

        for (VideoStatsOperationOrMenu vsoom : first.getListOperations()) {
            if (vsoom.isSeparator()) {
                outIndividually.addSeparator();
            } else if (vsoom.isOperation()) {
                addOperation(
                        vsoom.getOperation(),
                        listIn,
                        outIndividually,
                        outCombined,
                        markDisplaySettings);
            } else if (vsoom.isMenu()) {
                addMenu(vsoom.getMenu(), listIn, outIndividually, outCombined);
            } else {
                assert false;
            }
        }
    }

    private void addOperation(
            VideoStatsOperation or,
            List<VideoStatsOperationMenu> list,
            VideoStatsOperationMenu outIndividually,
            VideoStatsOperationMenu outCombined,
            MarkDisplaySettings markDisplaySettings) {
        VideoStatsOperationSequence all = new VideoStatsOperationSequence(or.getName());
        all.add(or);

        List<VideoStatsOperationCombine> listCombined = new ArrayList<>();
        or.getCombiner().ifPresent(listCombined::add);

        // We loop through all the other menus, looking to see if they have the same item. We accept
        // an item if they
        //  all have it.  We skip the first one, as it's not necessary.
        for (int i = 1; i < list.size(); i++) {
            VideoStatsOperation sameNameItem = findOperationOfSameNameOrNull(or, list.get(i));
            if (sameNameItem == null) {
                return;
            }
            all.add(sameNameItem);

            sameNameItem.getCombiner().ifPresent(listCombined::add);
        }
        outIndividually.add(all);

        if (!listCombined.isEmpty()) {
            addCombinedOperations(listCombined, outCombined, or, markDisplaySettings);
        }
    }

    // We can improve this logic, as it's not the most clear cut
    private void addCombinedOperations(
            List<VideoStatsOperationCombine> listCombined,
            VideoStatsOperationMenu out,
            VideoStatsOperation rootOperation,
            MarkDisplaySettings markDisplaySettings) {
        addBackgroundSetAndNoObjects(listCombined, out);

        // Get marks menu
        addMulti(
                listCombined,
                out,
                rootOperation,
                "Marks",
                new MultiInputMarksToOverlay(markDisplaySettings),
                VideoStatsOperationCombine::getMarks);
        addMulti(
                listCombined,
                out,
                rootOperation,
                "Objects",
                new MultiObjectsInputToOverlay(),
                VideoStatsOperationCombine::getObjects);
    }

    private void addBackgroundSetAndNoObjects(
            List<VideoStatsOperationCombine> listCombined, VideoStatsOperationMenu out) {

        List<NamedRasterSet> list = new ArrayList<>();
        for (VideoStatsOperationCombine op : listCombined) {
            if (op.getEnergyBackground() != null
                    && !op.getMarks().isPresent()
                    && !op.getObjects().isPresent()) {
                list.add(
                        new NamedRasterSet(
                                op.generateName(), op.getEnergyBackground().getBackgroundSet()));
            }
        }

        if (!list.isEmpty()) {
            RasterMultiModuleCreator creator =
                    new RasterMultiModuleCreator(list, "multi-raster", mpg);
            VideoStatsModuleCreatorAndAdder creatorAndAdder =
                    new VideoStatsModuleCreatorAndAdder(
                            progresssReporter -> adder.createChild(), creator);
            out.add(
                    new VideoStatsOperationFromCreatorAndAdder(
                            "Multi Raster", creatorAndAdder, mpg.getThreadPool(), mpg.getLogger()));
        }
    }

    /**
     * @param <T> input-type
     * @param listCombined
     * @param outMenu
     * @param rootOperation
     * @param subMenuName
     * @param bridge
     * @param getObjFromOperationCombine
     */
    private <T> void addMulti(
            List<VideoStatsOperationCombine> listCombined,
            VideoStatsOperationMenu outMenu,
            VideoStatsOperation rootOperation,
            String subMenuName,
            BridgeElementWithIndex<MultiInput<T>, IndexableOverlays, OperationFailedException>
                    bridge,
            GetObjFromOperationCombine<T> getObjFromOperationCombine) {

        // First we make a MultRaster, from all that suppport MultiRaster
        List<MultiInput<T>> list = new ArrayList<>();
        for (VideoStatsOperationCombine combine : listCombined) {
            if (combine.getEnergyBackground() != null
                    && getObjFromOperationCombine.getObj(combine).isPresent()) {

                getObjFromOperationCombine
                        .getObj(combine)
                        .ifPresent(
                                obj ->
                                        list.add(
                                                new MultiInput<>(
                                                        combine.generateName(),
                                                        combine.getEnergyBackground(),
                                                        obj)));
            }
        }

        if (!list.isEmpty()) {
            VideoStatsOperationMenu subMenu = outMenu.getOrCreateSubMenu(subMenuName, true);

            RasterMultiCreator<T> creator =
                    new RasterMultiCreator<>(list, rootOperation.getName(), mpg, bridge);
            VideoStatsModuleCreatorAndAdder creatorAndAdder =
                    new VideoStatsModuleCreatorAndAdder(progresssReporter -> adder, creator);
            subMenu.add(
                    new VideoStatsOperationFromCreatorAndAdder(
                            rootOperation.getName(),
                            creatorAndAdder,
                            mpg.getThreadPool(),
                            mpg.getLogger()));
        }
    }

    @FunctionalInterface
    private interface GetObjFromOperationCombine<T> {
        public Optional<OverlayCollectionSupplier<T>> getObj(VideoStatsOperationCombine op);
    }

    private void addMenu(
            VideoStatsOperationMenu menu,
            List<VideoStatsOperationMenu> list,
            VideoStatsOperationMenu out,
            VideoStatsOperationMenu outCombined) {

        ArrayList<VideoStatsOperationMenu> menuIn = new ArrayList<>();
        menuIn.add(menu);

        // We loop through all the other menus, looking to see if they have the same item. We accept
        // an item if they
        //  all have it.  We skip the first one, as it's not necessary.
        for (int i = 1; i < list.size(); i++) {
            Optional<VideoStatsOperationMenu> sameNameItem =
                    findMenuOfSameNameOrNull(menu, list.get(i));
            if (!sameNameItem.isPresent()) {
                return;
            }
            menuIn.add(sameNameItem.get());
        }

        VideoStatsOperationMenu menuNew = out.createSubMenu(menu.getName(), true);
        traverse(menuIn, menuNew, outCombined, markDisplaySettings);
    }

    private static VideoStatsOperation findOperationOfSameNameOrNull(
            VideoStatsOperation src, VideoStatsOperationMenu menu) {

        for (VideoStatsOperationOrMenu vsoom : menu.getListOperations()) {
            if (vsoom.isOperation() && src.getName().equals(vsoom.getOperation().getName())) {
                return vsoom.getOperation();
            }
        }
        return null;
    }

    private static Optional<VideoStatsOperationMenu> findMenuOfSameNameOrNull(
            VideoStatsOperationMenu src, VideoStatsOperationMenu menu) {

        for (VideoStatsOperationOrMenu vsoom : menu.getListOperations()) {
            if (vsoom.isMenu()) {

                // As names can also be null
                if (src.getName() == null) {
                    if (vsoom.getMenu().getName() == null) {
                        return Optional.of(vsoom.getMenu());
                    } else {
                        continue;
                    }
                }

                if (vsoom.getMenu().getName() != null
                        && src.getName().equals(vsoom.getMenu().getName())) {
                    return Optional.of(vsoom.getMenu());
                }
            }
        }
        return Optional.empty();
    }
}
