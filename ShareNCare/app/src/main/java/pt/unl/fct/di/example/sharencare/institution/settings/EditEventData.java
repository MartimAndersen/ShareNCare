package pt.unl.fct.di.example.sharencare.institution.settings;

import java.util.List;

public class EditEventData {

    public String name;
    public String description;
    public String durability;
    public String endingDate;
    public String initialDate;
    public String lat;
    public String lon;
    public String maxParticipants;
    public String minParticipants;
    public List<Integer> tags;
    public String time;

    public EditEventData(String name, String description, String durability, String endingDate, String initialDate, String lat, String lon, String maxParticipants, String minParticipants, List<Integer> tags, String time) {
        this.name = name;
        this.description = description;
        this.durability = durability;
        this.endingDate = endingDate;
        this.initialDate = initialDate;
        this.lat = lat;
        this.lon = lon;
        this.maxParticipants = maxParticipants;
        this.minParticipants = minParticipants;
        this.tags = tags;
        this.time = time;
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

    public String getDurability() {
        return durability;
    }

    public void setDurability(String durability) {
        this.durability = durability;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(String maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(String minParticipants) {
        this.minParticipants = minParticipants;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
