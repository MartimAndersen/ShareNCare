package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.events;

public class AddEventData {

    private String eventId;
    private String tokenId;

    public AddEventData(String tokenId, String eventId){
        this.eventId = eventId;
        this.tokenId = tokenId;
    }

}
