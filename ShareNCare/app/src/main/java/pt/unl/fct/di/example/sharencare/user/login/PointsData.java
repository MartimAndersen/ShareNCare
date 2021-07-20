package pt.unl.fct.di.example.sharencare.user.login;

public class PointsData {

    String username;
    public int events;
    int tracks;
    int comments;
    int quitEvents;
    int badComments;

    public PointsData(String username, int events, int tracks, int comments, int quitEvents, int badComments) {
        this.username = username;
        this.events = events;
        this.tracks = tracks;
        this.comments = comments;
        this.quitEvents = quitEvents;
        this.badComments = badComments;
    }
}
