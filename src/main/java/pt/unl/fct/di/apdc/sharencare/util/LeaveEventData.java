package pt.unl.fct.di.apdc.sharencare.util;

public class LeaveEventData {
	
public String eventId;
	
	public LeaveEventData() { 
	}
	
	public LeaveEventData( String eventId) {

		this.eventId = eventId;
	}

	public boolean atLeastOneEmptyParameter() {
        return eventId.equals("")	;
	}

}
