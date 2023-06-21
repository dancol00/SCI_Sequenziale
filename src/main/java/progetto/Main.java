package progetto;

import it.unisa.di.dif.SCIManager;
import it.unisa.di.dif.pattern.Image;

import java.io.File;
import java.io.IOException;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Aggiungi directory delle fotocamere all'esecuzione");
        }
        System.out.println("Camera directory: " + args[0]);

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
                SCIManager.extractResidualNoise(image);
            } catch(IOException e) {
                System.out.println(e);
            }
        }
    }

}
