package progetto;

import it.unisa.di.dif.SCIManager;
import it.unisa.di.dif.pattern.Image;
import it.unisa.di.dif.pattern.ReferencePattern;
import it.unisa.di.dif.pattern.ResidualNoise;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Camera {

    @Getter
    private String cameraName;
    @Getter
    private ReferencePattern referencePattern;

    private List<Path> referencePatterns_files;
    private List<File> residualNoises_files;

    public Camera(File folder) {
        residualNoises_files = new ArrayList<>();
        for(File photo: folder.listFiles()) {
            residualNoises_files.add(photo);
        }
        cameraName = folder.getName();
        referencePattern = null;
        referencePatterns_files = new ArrayList<>();
    }

    public ReferencePattern computeReferencePattern() {
        return computeReferencePattern(60);
    }

    public ReferencePattern computeReferencePattern(int sampleAmount) {
        if(referencePattern != null) {
            System.out.println("Reference Pattern has already been computed! Using the cached one.");
            return referencePattern;
        }

        System.out.println("Computing Reference Pattern");

        for(int i = 0; i < sampleAmount; i++) {
            int randomNumber = new Random().nextInt(residualNoises_files.size());
            referencePatterns_files.add(residualNoises_files.get(randomNumber).toPath());
            residualNoises_files.remove(randomNumber);
        }

        try {
            referencePattern = SCIManager.extractReferencePattern(referencePatterns_files);
        } catch(IOException e) {
            System.out.println(e);
            return null;
        }

        return referencePattern;
    }

    @Deprecated
    private ResidualNoise computeResidualNoises(@NotNull File f) {
        ResidualNoise noise;
            try {
                System.out.println("Computing residual noise of image " + f.getName());
                Image image = new Image(f);
                noise = SCIManager.extractResidualNoise(image);
            } catch(IOException e) {
                System.out.println(e);
                noise = null;
            }
        return noise;
    }

     public List<NoiseTuple> computeResidualAndCompare(List<ReferencePatternTuple> referencePatterns) {
        List<NoiseTuple> compareList = new ArrayList<>();
        int i = 0;
        for (File f: residualNoises_files) {
            String filename = residualNoises_files.get(i).getName();
            ResidualNoise noise = computeResidualNoises(f);
            for(ReferencePatternTuple rpt: referencePatterns) {
                ReferencePattern rp = (ReferencePattern) rpt.referencePattern;
                System.out.println("Starting comparison between image " + filename + " and Reference Pattern " + rpt.cameraName);
                Double compare = SCIManager.compare(rp, noise);
                NoiseTuple<String, Double> tuple = new NoiseTuple<>(filename, (String) rpt.cameraName, compare);
                System.out.println("Correlation value between image " + filename + " and Reference Pattern " + rpt.cameraName + " is " + compare + ".");
                compareList.add(tuple);
            }
            i++;
        }

        return compareList;
    }

}
