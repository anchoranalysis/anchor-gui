/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;

class CntKernelsFromCsv {

    public static int apply(String[] headers) {
        // Find the last header that matches Prop

        String finalProp = lastPropStr(headers);
        if (finalProp != null) {
            String numPart = extractNumPart(finalProp);
            return Integer.parseInt(numPart) + 1;
        } else {
            return 0;
        }
    }

    private static String lastPropStr(String[] headers) {
        String finalProp = null;
        for (String s : headers) {
            if (s.startsWith("Prop")) {
                finalProp = s;
            }
        }
        return finalProp;
    }

    private static String extractNumPart(String s) {
        return s.substring(4);
    }
}
