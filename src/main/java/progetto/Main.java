package progetto;

import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Aggiungi directory delle fotocamere all'esecuzione");
        }
        System.out.println("Directory containing cameras: " + args[0]);

        long startTime = System.nanoTime();
        File folder = new File(args[0]);
        if(!folder.isDirectory()) {
            System.out.println("Error: not a directory");
            exit(1);
        }

        List<Camera> cameraList = new ArrayList<>();
        List<ReferencePatternTuple> referencePatternsList = new ArrayList<>();

        for(File f: folder.listFiles()) {
            if(f.isDirectory()) {
                // Le foto sono in ${CAMERA_FOLDER}/img/
                Camera camera = new Camera(f);
                cameraList.add(camera);
                System.out.println("Computing reference pattern for camera " + camera.getCameraName());
                referencePatternsList.add(new ReferencePatternTuple(camera.getCameraName(), camera.computeReferencePattern()));
            }
        }

        File outputFile = new File(args[0], "output.txt");
        PrintWriter writer = new PrintWriter(outputFile);
        for(Camera camera: cameraList) {
            List<NoiseTuple> correlationList = camera.computeResidualAndCompare(referencePatternsList);
            for(NoiseTuple tuple: correlationList) {
                writer.println(camera.getCameraName() + "\t" + tuple.filename + "\t" + tuple.rpCamera + "\t" + tuple.compareValue);
            }
        }


        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Runtime is " + totalTime/1e9);

        exit(0);
    }
}
