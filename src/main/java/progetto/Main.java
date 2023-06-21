package progetto;

import it.unisa.di.dif.SCIManager;
import it.unisa.di.dif.pattern.Image;
import it.unisa.di.dif.pattern.ReferencePattern;
import it.unisa.di.dif.pattern.ResidualNoise;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Aggiungi directory delle fotocamere all'esecuzione");
        }
        System.out.println("Camera directory: " + args[0]);

        List<File> fileList = new ArrayList<>();
        List<Path> referencePattern_files = new ArrayList<>();

        List<ResidualNoise> residualNoises = new ArrayList<>();
        ReferencePattern referencePattern = new ReferencePattern();

        File folder = new File(args[0]);
        if(!folder.isDirectory()) {
            System.out.println("Error: not a directory");
            exit(1);
        }
        for(File f: folder.listFiles()) {
            fileList.add(f);
        }
        for(int i = 0; i < 60; i++) {
            int randomNumber = new Random().nextInt(fileList.size());
            File randomFile = fileList.get(randomNumber);
            referencePattern_files.add(randomFile.toPath());
            fileList.remove(randomNumber);
        }

        try {
            referencePattern = SCIManager.extractReferencePattern(referencePattern_files);
        } catch(IOException e) {
            System.out.println(e);
        }

        for(File f: fileList) {
            if(f.isDirectory())
                continue;
            try {
                Image image = new Image(f);
                ResidualNoise noise = SCIManager.extractResidualNoise(image);
                System.out.println(noise);
                residualNoises.add(noise);
                System.out.println(SCIManager.compare(referencePattern, noise));
            } catch(IOException e) {
                System.out.println(e);
            }
        }
    }

}
