package pt.unl.fct.di.apdc.sharencare.util;

public class JoinEvent {

	public String eventId;
	
	public JoinEvent() { 
	}
	
	public JoinEvent( String eventId) {

		this.eventId = eventId;
	}

	public boolean atLeastOneEmptyParameter() {
        return eventId.equals("")	;
	}
}
