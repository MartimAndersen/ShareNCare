package pt.unl.fct.di.example.sharencare.user.main_menu.ui.events;

import java.util.List;

public class FilterData {

    private String coordinates;
    private String date;
    private String institution;
    private String name;
    private String popularity;
    private List<Integer> tags;

    public FilterData(String coordinates, String date, String institution, String name, String popularity, List<Integer> tags) {
        this.coordinates = coordinates;
        this.date = date;
        this.institution = institution;
        this.name = name;
        this.popularity = popularity;
        this.tags = tags;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }
}
