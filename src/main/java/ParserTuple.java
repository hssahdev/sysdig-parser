public class ParserTuple {
    Process process;
    String type;
    FDObject object;
    double time;

    @Override
    public String toString() {
        return "ParserTuple{" +
                "process=" + process +
                ", type='" + type + '\'' +
                ", object=" + object +
                ", time='" + time + '\'' +
                '}';
    }

    public ParserTuple(Process process, String type, FDObject object, double time) {
        this.process = process;
        this.type = type;
        this.object = object;
        this.time = time;
    }

}
