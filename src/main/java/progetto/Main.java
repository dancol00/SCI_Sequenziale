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

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Aggiungi directory delle fotocamere all'esecuzione");
        }
        System.out.println("Directory containing cameras: " + args[0]);

        File folder = new File(args[0]);
        if(!folder.isDirectory()) {
            System.out.println("Error: not a directory");
            exit(1);
        }

        List<Camera> cameraList = new ArrayList<>();

        for(File f: folder.listFiles()) {
            if(f.isDirectory()) {
                // Le foto sono in ${CAMERA_FOLDER}/img/
                Camera camera = new Camera(f);
                cameraList.add(camera);
            }
        }

        for(Camera camera: cameraList) {
            // Salvo rp e rn in locale per eventuale confronti tra pattern non della stessa fotocamera
            ReferencePattern rp = camera.computeReferencePattern();
            List<ResidualNoise> rn = camera.computeResidualNoises();
            List<NoiseTuple> correlationList = camera.compareNoises();
        }

        exit(0);
    }
}
