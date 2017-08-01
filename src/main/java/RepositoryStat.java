public class RepositoryStat implements Comparable<RepositoryStat> {
    private Long occurrence = 1L;
    private Repository repo;

    public RepositoryStat(Repository repo, Long occurrence) {
        this.repo = repo;
        this.occurrence = occurrence;
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
