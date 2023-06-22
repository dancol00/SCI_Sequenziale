package progetto;

import it.unisa.di.dif.SCIManager;
import it.unisa.di.dif.pattern.Image;
import it.unisa.di.dif.pattern.ReferencePattern;
import it.unisa.di.dif.pattern.ResidualNoise;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Camera {

    List<File> residualNoises_files;
    List<ResidualNoise> residualNoises;
    List<Path> referencePatterns_files;
    ReferencePattern referencePattern;

    public Camera(File folder) {
        for(File photo: folder.listFiles()) {
            residualNoises_files.add(photo);
        }
        residualNoises = new ArrayList();
        referencePattern = null;
    }

    public ReferencePattern computeReferencePattern(int sampleAmount) {
        if(referencePattern != null) {
            System.out.println("Reference Pattern has already been computed! Using the cached one.");
            return referencePattern;
        }

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

    public List<ResidualNoise> computeResidualNoises() {
        if(!residualNoises.isEmpty()) {
            System.out.println("Residual Noises have already been computed! Using the cached ones.");
            return residualNoises;
        }

        for(File f: residualNoises_files) {
            try {
                Image image = new Image(f);
                ResidualNoise noise = SCIManager.extractResidualNoise(image);
                residualNoises.add(noise);
            } catch(IOException e) {
                System.out.println(e);
                return null;
            }
        }
        return residualNoises;
    }

    public List<NoiseTuple> compareNoises() {
        List<NoiseTuple> compareList = new ArrayList<>();
        int i = 0;
        for (ResidualNoise rn: residualNoises) {
            Double compare = SCIManager.compare(referencePattern, rn);
            String filename = residualNoises_files.get(i).getName();
            NoiseTuple<String, Double> tuple = new NoiseTuple<>(filename, compare);

            System.out.println("Correlation value for photo " + filename + " is " + compare +".");

            compareList.add(tuple);
            i++;
        }

        return compareList;
    }
}
