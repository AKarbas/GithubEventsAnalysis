import java.io.Serializable;

public class User implements Serializable {
    String display_login;
    String url;

    static long serialVerisonUID = 1975L;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User)
            return this.display_login.equals(((User) obj).display_login)
                    && this.url.equals(((User) obj).url);
        else return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return display_login.hashCode() + 1069 * url.hashCode();
    }

    @Override
    public String toString() {
        return "name: " + display_login + " ,url: " + url;
    }

}
