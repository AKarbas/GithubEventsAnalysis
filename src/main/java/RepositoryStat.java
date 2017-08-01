import java.util.Map;

public class RepositoryStat implements Comparable<RepositoryStat> {
    private Long occurrence = 1L;
    private Repository repo;
    private Map.Entry<Repository, Long> entry = new Map.Entry<Repository, Long>() {
        @Override
        public Repository getKey() {
            return repo;
        }

        @Override
        public Long getValue() {
            return occurrence;
        }

        @Override
        public Long setValue(Long value) {
            Long p = occurrence;
            occurrence = value;
            return p;
        }
    };

    public RepositoryStat(Repository repo) {
        this.repo = repo;
    }

    public RepositoryStat(Repository repo, Long occurrence) {
        this.repo = repo;
        this.occurrence = occurrence;
    }

    public void recordOccurrence() {
        occurrence++;
    }

    public void removeOccurrence() {
        occurrence--;
    }

    @Override
    public int compareTo(RepositoryStat o) {
        return -1 * occurrence.compareTo(o.occurrence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RepositoryStat)
            return repo.equals(((RepositoryStat) obj).repo);
        else
            return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Repo: [" + this.repo + "] : [" + this.occurrence + "]";
    }

    @Override
    public int hashCode() {
        return repo.hashCode();
    }
}
