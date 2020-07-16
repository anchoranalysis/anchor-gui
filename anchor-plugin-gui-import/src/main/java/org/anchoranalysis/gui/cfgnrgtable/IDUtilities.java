/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JTable;

public class IDUtilities {

    public static Set<Integer> setFromIntArr(int[] arr) {

        HashSet<Integer> idSet = new HashSet<>();

        // If arr is null, then there is nothing in our set
        if (arr == null) {
            return idSet;
        }

        for (int item : arr) {
            idSet.add(item);
        }
        return idSet;
    }

    public static void scrollJTableToRow(JTable table, int id) {
        table.scrollRectToVisible(new Rectangle(table.getCellRect(id, 0, true)));
    }
}
