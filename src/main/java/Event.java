import java.io.Serializable;

public class Event implements Serializable {
    static final long serialVersionUID = 1973L;
    String type;
    Payload payload;
    Repository repo;
    User actor;
    long instanciationTime;

    public Event() {
        instanciationTime = System.currentTimeMillis();
    }

    public boolean isNMinOld(int n) {
        return System.currentTimeMillis() - instanciationTime > n * 60000L;
    }

    public boolean isNHourOld(int n) {
        return System.currentTimeMillis() - instanciationTime > n * 3600000L;
    }

    public boolean is1DayOld(int n) {
        return System.currentTimeMillis() - instanciationTime > n * 86400000L;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event)
            return this.repo.equals(((Event) obj).repo);
        else return super.equals(obj);
    }

    @Override
    public String toString() {
        return "time: " + instanciationTime
                + " Type: [" + type + "]"
                + " Repo: [" + repo.toString() + "]"
                + " User: [" + actor.toString() + "]"
                + " Lang: " + payload.pull_request.head.repo.language;
    }
}