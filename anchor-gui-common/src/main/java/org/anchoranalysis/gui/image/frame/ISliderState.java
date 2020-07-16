/* (C)2020 */
package org.anchoranalysis.gui.image.frame;

import org.anchoranalysis.gui.image.ISliceNumGetter;
import org.anchoranalysis.gui.videostats.link.LinkModules.Adder;

public interface ISliderState extends ISliceNumGetter {

    int getIndex();

    void setSliceNum(int sliceNum);

    void addSliceTo(Adder<Integer> adder);

    void addIndexTo(Adder<Integer> adder);
}
