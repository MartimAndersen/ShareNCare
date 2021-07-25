package pt.unl.fct.di.example.sharencare.user.register;

import java.util.List;

public class ProfileDataTags {

    private String username;
    private List<Integer> tags;

    public ProfileDataTags(String username, List<Integer> tags){
        this.username = username;
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }
}
