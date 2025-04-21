package fctreddit.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Votes implements Serializable {

    @Id
    private VotesId votesId;
    private String type;


    public Votes() {
    }

    public Votes(String userId, String postId,String type) {
        this.votesId = new VotesId(userId, postId);
        this.type = type;
    }

    // Getters e Setters
    public String getUserId() {
        return votesId.getUserId();
    }

    public void setUserId(String userId) {
        votesId.setUserId(userId);
    }

    public String getPostId() {
        return votesId.getPostId();
    }

    public void setPostId(String postId) {
        votesId.setPostId(postId);
    }



}
