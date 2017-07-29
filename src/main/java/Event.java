public class Event {
    Repository repo;
    private long instanciationTime;

    public Event() {
        instanciationTime = System.currentTimeMillis();
    }

    public boolean isNMinOld(int n) {
        return System.currentTimeMillis() - instanciationTime > n *60000L;
    }

    public boolean isNHourOld(int n) {
        return System.currentTimeMillis() - instanciationTime > n * 3600000L;
    }

    public boolean is1DayOld(int n) {
        return System.currentTimeMillis() - instanciationTime > n * 86400000L;
    }
}