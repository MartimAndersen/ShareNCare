package pt.unl.fct.di.example.sharencare.institution.settings;

public class FinishEvent {

    public String name;

    public FinishEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
