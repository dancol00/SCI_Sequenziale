package progetto;

import it.unisa.di.dif.pattern.ResidualNoise;

public class NoiseTuple<X, Y> {
    public final X filename;
    public final Y compareValue;

    public NoiseTuple(X f, Y d) {
        filename = f;
        compareValue = d;
    }
}

