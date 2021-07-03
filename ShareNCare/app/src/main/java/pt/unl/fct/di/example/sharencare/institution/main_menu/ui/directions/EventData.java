package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.directions;

import java.util.List;

public class EventData {
    public String name, description;
    public String minParticipants, maxParticipants;
    public String time;
    public String durability;
    public String date;
    public List<Integer> tags;
    public Double lat, lon;

    public EventData(String name, String description, String minParticipants, String maxParticipants, String time, String durability, String date, List<Integer> tags, Double lat, Double lon) {
        this.name = name;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.time = time;
        this.durability = durability;
        this.date = date;
        this.tags = tags;
        this.lat = lat;
        this.lon = lon;
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
