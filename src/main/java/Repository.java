public class Repository {
    String name = "";
    String url = "";

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
