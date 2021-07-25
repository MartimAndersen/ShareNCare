package pt.unl.fct.di.example.sharencare.common.events;

import java.util.List;

public class GetEventsByCoordinates {

    public List<String> coordinates;

    public GetEventsByCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }
}
