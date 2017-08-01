import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StatsMap {
    private ConcurrentHashMap<Repository, Long> data =
            new ConcurrentHashMap<Repository, Long>(1 << 23);
    //set so that can contain max number of events during a day.
    private long sum = 0L;
    private String name;


    public StatsMap(String name) {
        this.name = name;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void add(Repository repo) {
        if (data.containsKey(repo)) {
            data.put(repo, data.get(repo) + 1L);
//            System.out.println(name + ".increase: " + repo);
        } else {
//            System.out.println(name + ".add: " + repo);
            data.put(repo, 1L);
        }
        sum++;
    }

    public void remove(Repository repo) {
//        System.out.println(name + ".remove: " + repo);
        data.put(repo, data.get(repo) - 1);
        sum--;
    }

    public Long stat(Repository repo) {
        return data.get(repo) != null ? data.get(repo) : 0L;
    }

    public long getSum() {
        return sum;
    }

    @Override
    public String toString() {
        ArrayList<RepositoryStat> list = new ArrayList<RepositoryStat>(11);
        Iterator<Repository> iterator = data.keySet().iterator();
        for (int i = 0; i < 11 && iterator.hasNext(); i++) {
            Repository r = iterator.next();
            list.add(new RepositoryStat(r, data.get(r)));
        }
        Collections.sort(list);
        while (iterator.hasNext()) {
            Repository r = iterator.next();
            list.add(new RepositoryStat(r, data.get(r)));
            Collections.sort(list);
        }

        StringBuilder result = new StringBuilder(name);
        result.append(": { ");
        for (int i = 0; i < 10; i++) {
            RepositoryStat t = list.get(i);
            if (t == null)
                break;
            result.append(t.toString());
            result.append(" , ");
        }
        result.append("}\n");

        return result.toString();
    }
}
