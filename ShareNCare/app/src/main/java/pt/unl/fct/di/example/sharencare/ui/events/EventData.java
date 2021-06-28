package pt.unl.fct.di.example.sharencare.ui.events;

import java.util.List;

public class EventData {
    public String name, description;
    public String minParticipants, maxParticipants;
    //public String coordinates;
    public String temporary;
    public String date;
    public List<Integer> tags;
    public Double lat, lon;

    public EventData(String name, String description,String minParticipants,String maxParticipants,
                     Double lat, Double lon, String temporary, String date, List<Integer> tags) {

        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = minParticipants;
        this.lat = lat;
        this.lon = lon;
        this.temporary = temporary;
        this.date = date;
        this.tags = tags;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(String minParticipants) {
        this.minParticipants = minParticipants;
    }

    public String getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(String maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
