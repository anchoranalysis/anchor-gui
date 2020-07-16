/* (C)2020 */
package org.anchoranalysis.gui.series;

import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.image.stack.TimeSequence;

public class TimeSequenceProvider {

    private NamedProvider<TimeSequence> sequence;
    private int numFrames;

    public TimeSequenceProvider(NamedProvider<TimeSequence> sequence, int numFrames) {
        super();
        this.sequence = sequence;
        this.numFrames = numFrames;
    }

    public NamedProvider<TimeSequence> sequence() {
        return sequence;
    }

    public int getNumFrames() {
        return numFrames;
    }
}
