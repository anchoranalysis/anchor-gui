/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import org.anchoranalysis.gui.videostats.dropdown.manifest.ManifestDropDown;
import org.anchoranalysis.io.manifest.ManifestRecorder;

public interface IAddManifestRecorder {

    ManifestDropDown add(ManifestRecorder manifestRecorder);
}
