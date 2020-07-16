/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.FileExecutedExperimentImageWithManifest;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.plugin.io.bean.input.manifest.CoupledManifestsInputManager;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;
import org.anchoranalysis.plugin.io.manifest.ManifestCouplingDefinition;

// TODO duplication not right
public class ExecutedExperimentFileCreator extends FileCreatorGeneralList {

    // START BEANS
    @BeanField @Getter @Setter private CoupledManifestsInputManager coupledManifestsInputManager;
    // END BEANS

    private List<String> experimentNames;

    @Override
    public void addFilesToList(
            List<InteractiveFile> listFiles,
            FileCreatorParams params,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        // TODO for now, we have no features from the NamedDefinitons added to the global feature
        // list

        ManifestCouplingDefinition manifestCouplingDefinition;
        try {
            manifestCouplingDefinition =
                    coupledManifestsInputManager.manifestCouplingDefinition(
                            new InputManagerParams(
                                    params.createInputContext(),
                                    progressReporter,
                                    params.getLogErrorReporter()));
        } catch (DeserializationFailedException e) {
            throw new OperationFailedException(e);
        }

        experimentNames = new ArrayList<>();

        for (Iterator<ManifestRecorder> itrExp =
                        manifestCouplingDefinition.iteratorExperimentalManifests();
                itrExp.hasNext(); ) {
            ManifestRecorder manifestExperiment = itrExp.next();
            addVideoStatsFileFromManifestExperiment(
                    manifestExperiment, params, manifestCouplingDefinition, listFiles);
        }

        for (Iterator<CoupledManifests> itrCM =
                        manifestCouplingDefinition.iteratorCoupledManifests();
                itrCM.hasNext(); ) {
            CoupledManifests cm = itrCM.next();

            // We are only interested in coupledmanifests that we did not find previously
            if (!cm.getExperimentManifest().isPresent()) {
                addVideoStatsFileToList(cm, params, listFiles);
            }
        }
    }

    private void addVideoStatsFileFromManifestExperiment(
            ManifestRecorder manifestExperiment,
            FileCreatorParams params,
            ManifestCouplingDefinition manifestCouplingDefinition,
            List<InteractiveFile> listFiles) {
        experimentNames.add(manifestExperiment.getRootFolder().getRelativePath().toString());

        FinderSerializedObject<NRGScheme> finderNRGScheme =
                new FinderSerializedObject<>(
                        "nrgScheme", params.getLogErrorReporter().errorReporter());
        finderNRGScheme.doFind(manifestExperiment);

        for (Iterator<CoupledManifests> i =
                        manifestCouplingDefinition.iteratorCoupledManifestsFor(manifestExperiment);
                i.hasNext(); ) {
            CoupledManifests coupledManifests = i.next();
            addVideoStatsFileToList(coupledManifests, params, listFiles);
        }
    }

    private void addVideoStatsFileToList(
            CoupledManifests coupledManifests,
            FileCreatorParams params,
            List<InteractiveFile> listFiles) {

        FileExecutedExperimentImageWithManifest file =
                new FileExecutedExperimentImageWithManifest(
                        coupledManifests, params.getRasterReader(), params.getMarkCreatorParams());

        listFiles.add(file);
    }

    @Override
    public String suggestName() {

        if (hasCustomName()) {
            return getCustomName();
        }

        if (experimentNames.isEmpty()) {
            return "Untitled Executed Experiment Files";
        } else if (experimentNames.size() == 1) {
            return experimentNames.get(0);
        } else {
            return "Combined Executed Experiment Files";
        }
    }
}
