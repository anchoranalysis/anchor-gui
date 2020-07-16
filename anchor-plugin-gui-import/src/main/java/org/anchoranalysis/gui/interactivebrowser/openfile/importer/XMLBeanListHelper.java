/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import static org.anchoranalysis.gui.interactivebrowser.openfile.importer.CreatorFactory.*;

import java.io.File;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.FileFeatureEvaluatorCreator;
import org.anchoranalysis.gui.bean.filecreator.NamedSingleStackCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.NrgSchemeCreatorState;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.bean.input.InputManager;

class XMLBeanListHelper {

    public static FileCreator createSingleStack(
            InputManager<? extends ProvidesStackInput> inputManager, File f) {
        return CreatorFactory.create(
                new NamedSingleStackCreator(),
                inputManager,
                (creator, input) -> creator.setInput(inputManager),
                f,
                "raster-set");
    }

    @SuppressWarnings("unchecked")
    public static FileCreator creatorForList(List<Object> list, File f) throws CreateException {
        if (list.size() == 0) {
            throw new CreateException(
                    "There are no bean in the list, so cannot figure out the type");
        }

        Object bean = (Object) list; // Upcast to make down-casting easy later
        Object firstItem = list.get(0);

        if (firstItem instanceof NamedBean) {

            NamedBean<AnchorBean<?>> castFirstItem = (NamedBean<AnchorBean<?>>) firstItem;
            return creatorForNamedBean(castFirstItem, bean, f);

        } else {
            throw new CreateException(
                    String.format(
                            "The class of list-item is not currently supported: %s",
                            firstItem.getClass()));
        }
    }

    @SuppressWarnings("unchecked")
    private static FileCreator creatorForNamedBean(
            NamedBean<AnchorBean<?>> namedBean, Object bean, File f) throws CreateException {

        AnchorBean<?> item = namedBean.getItem();

        if (item instanceof FeatureListProvider) {
            return createFeatureEvaluator(
                    (List<NamedBean<FeatureListProvider<FeatureInput>>>) bean, f);
        } else {
            throw new CreateException(
                    String.format(
                            "The class of named-bean is not currently supported: %s",
                            item.getClass()));
        }
    }

    private static FileCreator createFeatureEvaluator(
            List<NamedBean<FeatureListProvider<FeatureInput>>> features, File f) {
        return create(
                new FileFeatureEvaluatorCreator(),
                features,
                (creator, fl) -> {
                    creator.setListFeatures(fl);
                    creator.setNrgSchemeCreator(NrgSchemeCreatorState.instance().getItem());
                },
                f,
                "feature evaluator");
    }
}
