public class Repository {
    int id;
    String name = "";
    String url = "";

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Repository)
            return this.id == ((Repository) obj).id
                    && this.name.equals(((Repository) obj).name)
                    && this.url.equals(((Repository) obj).url);
        else return super.equals(obj);
    }

    @Override
    public String toString() {
        return "id: " + id + " name: " + name + " url: " + url;
    }

    @Override
    public int hashCode() {
        return id + 1069 * name.hashCode() + (int) (1142761L * url.hashCode());
    }
}
