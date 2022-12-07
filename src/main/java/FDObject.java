public class FDObject {
    int fdNo;

    public FDObject(int fdNo, String fdType, String resolvedString) {
        this.fdNo = fdNo;
        this.fdType = fdType;
        this.resolvedString = resolvedString;
    }

    @Override
    public String toString() {
        return "FDObject{" +
                "fdNo=" + fdNo +
                ", fdType='" + fdType + '\'' +
                ", resolvedString='" + resolvedString + '\'' +
                '}';
    }

    String fdType;
    String resolvedString;
}
