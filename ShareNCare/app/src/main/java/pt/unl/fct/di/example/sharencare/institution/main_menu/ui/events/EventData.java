package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.events;

import java.util.List;

public class EventData {
    public String description;
    public String durability;
    public String endingDate;
    public String initialDate;
    public Double lat;
    public Double lon;
    public String maxParticipants;
    public List<String> members;
    public String minParticipants;
    public String name;
    public List<Integer> tags;
    public String time;

    public EventData(String description, String durability, String endingDate, String initialDate, Double lat, Double lon, String maxParticipants, List<String> members, String minParticipants, String name, List<Integer> tags, String time) {
        this.description = description;
        this.durability = durability;
        this.endingDate = endingDate;
        this.initialDate = initialDate;
        this.lat = lat;
        this.lon = lon;
        this.maxParticipants = maxParticipants;
        this.members = members;
        this.minParticipants = minParticipants;
        this.name = name;
        this.tags = tags;
        this.time = time;
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

    public String getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(String maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(String minParticipants) {
        this.minParticipants = minParticipants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
