package progetto;

public class ReferencePatternTuple<X, Y> {
    public final X cameraName;
    public final Y referencePattern;

    public ReferencePatternTuple(X cn, Y rp) {
        cameraName = cn;
        referencePattern = rp;
    }
}