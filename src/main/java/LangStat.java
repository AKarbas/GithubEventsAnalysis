public class LangStat implements Comparable<LangStat> {
    private Long occurrence;
    private String lang;

    public LangStat(String lang, Long occurrence) {
        this.lang = lang;
        this.occurrence = occurrence;
    }

    @Override
    public int compareTo(LangStat o) {
        return -1 * occurrence.compareTo(o.occurrence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LangStat)
            return lang.equals(((LangStat) obj).lang);
        else
            return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Lang: [" + lang + "] : [" + occurrence + "]";
    }

    @Override
    public int hashCode() {
        return lang.hashCode();
    }
}
