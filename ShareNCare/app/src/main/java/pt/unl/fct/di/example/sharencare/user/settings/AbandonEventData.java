package pt.unl.fct.di.example.sharencare.user.settings;

import java.util.List;

public class AbandonEventData {

    private List<String> eventsId;
    private String username;

    public AbandonEventData(List<String> eventsId, String username) {
        this.eventsId = eventsId;
        this.username = username;
    }

    public List<String> getEventsId() {
        return eventsId;
    }

    public void setEventsId(List<String> eventsId) {
        this.eventsId = eventsId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
