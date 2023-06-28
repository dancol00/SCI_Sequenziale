package progetto;

public class NoiseTuple<X, Y> {
    public final X filename;
    public final X rpCamera;
    public final Y compareValue;

    public NoiseTuple(X f, X rpc, Y d) {
        filename = f;
        rpCamera = rpc;
        compareValue = d;
    }
}

