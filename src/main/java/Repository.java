import java.io.Serializable;

public class Repository implements Serializable {
    String name = "";
    String url = "";

    static long serialVerisonUID = 1974L;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Repository)
            return this.name.equals(((Repository) obj).name)
                    && this.url.equals(((Repository) obj).url);
        else return super.equals(obj);
    }

    @Override
    public String toString() {
        return "name: " + name + " ,url: " + url;
    }

    @Override
    public int hashCode() {
        return url.hashCode() + 1069 * name.hashCode();
    }
}
