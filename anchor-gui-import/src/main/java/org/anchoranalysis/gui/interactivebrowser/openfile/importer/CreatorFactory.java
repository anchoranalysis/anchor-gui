/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.function.BiConsumer;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.apache.commons.io.FilenameUtils;

class CreatorFactory {

    public static <S extends FileCreator, T> FileCreator create(
            S creator,
            T propertyToSet,
            BiConsumer<S, T> setPropertyFunc,
            File f,
            String customNamePrefix) {

        // Perform whatever custom setting operation is implemented by the lambda
        setPropertyFunc.accept(creator, propertyToSet);

        // Set the custom name
        String fileNameWithoutExt = FilenameUtils.removeExtension(f.getName());
        creator.setCustomName(String.format("%s: %s", customNamePrefix, fileNameWithoutExt));

        return creator;
    }
}
