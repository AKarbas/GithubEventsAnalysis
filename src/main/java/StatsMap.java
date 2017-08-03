import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class StatsMap {
    private ConcurrentHashMap<Repository, Long> repoData =
            new ConcurrentHashMap<Repository, Long>(1 << 23);

    private ConcurrentHashMap<User, Long> userData =
            new ConcurrentHashMap<User, Long>(1 << 23);

    private ConcurrentHashMap<String, Long> langData =
            new ConcurrentHashMap<String, Long>(1 << 23);

    private ConcurrentHashMap<Repository, Long> mostIssued =
            new ConcurrentHashMap<Repository, Long>(1 << 23);

    private ConcurrentHashMap<Repository, Long> mostCommented =
            new ConcurrentHashMap<Repository, Long>(1 << 23);

    private ConcurrentHashMap<Repository, Long> mostWatched =
            new ConcurrentHashMap<Repository, Long>(1 << 23);

    private ConcurrentHashMap<Repository, Long> mostStarred =
            new ConcurrentHashMap<Repository, Long>(1 << 23);//Possible?
    //set so that can contain max number of events during a day.
    private long sum = 0L;
    private String name;


    public StatsMap(String name) {
        this.name = name;
    }


    public void add(Event e) {
        try {
            add(e.repo);
            add(e.actor);
            if (e.type.equals("PullRequestEvent"))
                add(e.payload.pull_request.head.repo.language);
            sum++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void add(Repository repo) {
        if (repoData.containsKey(repo)) {
            repoData.put(repo, repoData.get(repo) + 1L);
        } else {
            repoData.put(repo, 1L);
        }
    }

    private void add(String lang) {
        if (lang == null) return;
        if (langData.containsKey(lang)) {
            langData.put(lang, langData.get(lang) + 1L);
        } else {
            langData.put(lang, 1L);
        }

    }

    private void add(User user) {
        if (userData.containsKey(user)) {
            userData.put(user, userData.get(user) + 1L);
        } else {
            userData.put(user, 1L);
        }
    }

    public void remove(Event e) {
        remove(e.repo);
        if (e.type.equals("PullRequestEvent"))
            remove(e.payload.pull_request.head.repo.language);
        remove(e.actor);

        sum--;
    }

    private void remove(User user) {
        if (!userData.containsKey(user)) return;
        if (userData.get(user) == 1)
            userData.remove(user);
        else
            userData.put(user, userData.get(user) - 1);
    }

    private void remove(String lang) {
        if (lang == null || !langData.containsKey(lang)) return;
        if (langData.get(lang) == 1)
            langData.remove(lang);
        else
            langData.put(lang, langData.get(lang) - 1);
    }


    private void remove(Repository repo) {
        if (!repoData.containsKey(repo)) return;
        if (repoData.get(repo) == 1)
            repoData.remove(repo);
        else
            repoData.put(repo, repoData.get(repo) - 1);
    }
//
//
//    public long getSum() {
//        return sum;
//    }

    @Override
    public String toString() {
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < name.length(); i++)
            t.append(" ");
        return name + ": HotRepo" + repoOutput()
                + t.toString() + ", HotUser: " + userOutput()
                + t.toString() + ", HotLang: " + langOutput();
    }


    private String repoOutput() {
        ArrayList<RepositoryStat> list = new ArrayList<RepositoryStat>(11);
        Iterator<Repository> iterator = repoData.keySet().iterator();
        for (int i = 0; i < 11 && iterator.hasNext(); i++) {
            Repository r = iterator.next();
            list.add(new RepositoryStat(r, repoData.get(r)));
        }
        Collections.sort(list);
        while (iterator.hasNext()) {
            Repository r = iterator.next();
            list.add(new RepositoryStat(r, repoData.get(r)));
            Collections.sort(list);
        }
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < 10; i++) {
            if (i >= list.size()) break;
            RepositoryStat t = list.get(i);
            result.append(t.toString());
            result.append(" , ");
        }
        result.append("}\n");

        return result.toString();
    }

    private String userOutput() {
        ArrayList<UserStat> list = new ArrayList<UserStat>(11);
        Iterator<User> iterator = userData.keySet().iterator();
        for (int i = 0; i < 11 && iterator.hasNext(); i++) {
            User r = iterator.next();
            list.add(new UserStat(r, userData.get(r)));
        }
        Collections.sort(list);
        while (iterator.hasNext()) {
            User r = iterator.next();
            list.add(new UserStat(r, userData.get(r)));
            Collections.sort(list);
        }
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < 10; i++) {
            if (i >= list.size()) break;
            UserStat t = list.get(i);
            result.append(t.toString());
            result.append(" , ");
        }
        result.append("}\n");

        return result.toString();
    }

    private String langOutput() {
        ArrayList<LangStat> list = new ArrayList<LangStat>(11);
        Iterator<String> iterator = langData.keySet().iterator();
        for (int i = 0; i < 11 && iterator.hasNext(); i++) {
            String r = iterator.next();
            list.add(new LangStat(r, langData.get(r)));
        }
        Collections.sort(list);
        while (iterator.hasNext()) {
            String r = iterator.next();
            list.add(new LangStat(r, langData.get(r)));
            Collections.sort(list);
        }
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < 10; i++) {
            if (i >= list.size()) break;
            LangStat t = list.get(i);
            result.append(t.toString());
            result.append(" , ");
        }
        result.append("}\n");

        return result.toString();
    }


}
