package pt.unl.fct.di.example.sharencare.user.settings;

public class LeaveEventData {

    public String eventId;

    public LeaveEventData(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
