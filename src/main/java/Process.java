public class Process {
    String processName;
    long pid;

    public Process(String processName, long pid) {
        this.processName = processName;
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "Process{" +
                "processName='" + processName + '\'' +
                ", pid=" + pid +
                '}';
    }
}
