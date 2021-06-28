package pt.unl.fct.di.example.sharencare.ui.events;

public class AddEventData {

    private String eventId;
    private String tokenId;

    public AddEventData(String tokenId, String eventId){
        this.eventId = eventId;
        this.tokenId = tokenId;
    }

}
