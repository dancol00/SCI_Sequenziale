package progetto;

import it.unisa.di.dif.SCIManager;
import it.unisa.di.dif.pattern.Image;
import it.unisa.di.dif.pattern.ResidualNoise;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Aggiungi directory delle fotocamere all'esecuzione");
        }
        System.out.println("Camera directory: " + args[0]);

        List<ResidualNoise> residualNoises = new ArrayList<>();

        File folder = new File(args[0]);
        if(!folder.isDirectory()) {
            System.out.println("Error: not a directory");
            exit(1);
        }
        for(File f: folder.listFiles()) {
            if(f.isDirectory())
                continue;
            try {
                Image image = new Image(f);
                ResidualNoise noise = SCIManager.extractResidualNoise(image);
                System.out.println(noise);
                residualNoises.add(noise);
            } catch(IOException e) {
                System.out.println(e);
            }
        }
    }

}
