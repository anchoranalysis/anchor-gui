/* (C)2020 */
package org.anchoranalysis.gui.finder;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.channel.Channel;

public interface FinderRasterSingleChnl {

    boolean exists();

    Channel getFirstChnl() throws GetOperationFailedException;

    int getNumChnl() throws GetOperationFailedException;
}
