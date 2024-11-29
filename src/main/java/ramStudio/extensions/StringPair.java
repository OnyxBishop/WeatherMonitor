package ramStudio.extensions;

public class StringPair {
    private String lat;
    private String lon;

    public StringPair(String first, String second) {
        lat = first;
        lon = second;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
