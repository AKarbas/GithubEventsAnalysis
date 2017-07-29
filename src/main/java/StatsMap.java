import java.util.HashMap;

public class StatsMap {
    HashMap<String, Long> map = new HashMap<String, Long>();
    long sum = 0;

    public void add(String t) {
        map.put(t, map.get(t) != null ? map.get(t) + 1 : 1);
        sum++;
    }

    public void remove(String t) {
        map.put(t, map.get(t) - 1);
        sum--;
    }

    public Long stat(String t) {
        return map.get(t) != null ? map.get(t) : 0L;
    }

    public long sum (){
        return sum;
    }
}
