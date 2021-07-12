package pt.unl.fct.di.apdc.sharencare.util;

public class JoinEventData {

	public String eventId;
	
	public JoinEventData() { 
	}
	
	public JoinEventData( String eventId) {

		this.eventId = eventId;
	}

	public boolean atLeastOneEmptyParameter() {
        return eventId.equals("")	;
	}
}
