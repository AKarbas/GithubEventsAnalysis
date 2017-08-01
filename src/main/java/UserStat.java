public class UserStat implements Comparable<UserStat> {
    private Long occurrence = 1L;
    private User user;

    public UserStat(User user, Long occurrence) {
        this.user = user;
        this.occurrence = occurrence;
    }


    @Override
    public int compareTo(UserStat o) {
        return -1 * occurrence.compareTo(o.occurrence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserStat)
            return user.equals(((UserStat) obj).user);
        else
            return super.equals(obj);
    }

    @Override
    public String toString() {
        return "User: [" + user + "] : [" + occurrence + "]";
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }
}
