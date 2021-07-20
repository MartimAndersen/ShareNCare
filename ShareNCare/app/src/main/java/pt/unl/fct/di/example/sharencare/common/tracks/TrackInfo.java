package pt.unl.fct.di.example.sharencare.common.tracks;

public class TrackInfo {


    public String comments;
    public String description;
    public int difficulty;
    public String distance;
    public String points;
    public String title;
    public String type;

    public TrackInfo(String comments, String description, int difficulty, String distance, String points, String title, String type) {
        this.comments = comments;
        this.description = description;
        this.difficulty = difficulty;
        this.distance = distance;
        this.points = points;
        this.title = title;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
