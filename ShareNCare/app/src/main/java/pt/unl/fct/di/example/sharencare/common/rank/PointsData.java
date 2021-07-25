package pt.unl.fct.di.example.sharencare.common.rank;

public class PointsData {

    int badComments;
    int comments;
    int commentsRank;
    int dislikedComents;
    public int events;
    int likedComents;
    byte[] pic;
    int quitEvents;
    int total;
    int tracks;
    String username;

    public PointsData(int badComments, int comments, int commentsRank, int dislikedComents, int events, int likedComents, byte[] pic, int quitEvents, int total, int tracks, String username) {
        this.badComments = badComments;
        this.comments = comments;
        this.commentsRank = commentsRank;
        this.dislikedComents = dislikedComents;
        this.events = events;
        this.likedComents = likedComents;
        this.pic = pic;
        this.quitEvents = quitEvents;
        this.total = total;
        this.tracks = tracks;
        this.username = username;
    }

    public int getBadComments() {
        return badComments;
    }

    public void setBadComments(int badComments) {
        this.badComments = badComments;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getCommentsRank() {
        return commentsRank;
    }

    public void setCommentsRank(int commentsRank) {
        this.commentsRank = commentsRank;
    }

    public int getDislikedComents() {
        return dislikedComents;
    }

    public void setDislikedComents(int dislikedComents) {
        this.dislikedComents = dislikedComents;
    }

    public int getEvents() {
        return events;
    }

    public void setEvents(int events) {
        this.events = events;
    }

    public int getLikedComents() {
        return likedComents;
    }

    public void setLikedComents(int likedComents) {
        this.likedComents = likedComents;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public int getQuitEvents() {
        return quitEvents;
    }

    public void setQuitEvents(int quitEvents) {
        this.quitEvents = quitEvents;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
