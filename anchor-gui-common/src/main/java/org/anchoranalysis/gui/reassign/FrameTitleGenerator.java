/* (C)2020 */
package org.anchoranalysis.gui.reassign;

public class FrameTitleGenerator {

    public String genTitleString(String frameName) {
        return frameName;
    }

    public String genTitleString(String prefixName, int iter) {
        return String.format("%s: iter=%09d", prefixName, iter);
    }

    public String genFramePrefix(String prefixName, String subTitle) {

        if (!prefixName.isEmpty()) {
            return String.format("%s: %s", prefixName, subTitle);
        } else {
            return subTitle;
        }
    }
}
