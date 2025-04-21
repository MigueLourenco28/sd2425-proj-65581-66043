package fctreddit.api;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.io.Serializable;

@Entity
public class Votes implements Serializable {

    @EmbeddedId
    private VotesId votesId;
    private String type;


    public Votes() {
    }

    public Votes(String userId, String postId) {
        this.votesId = new VotesId(userId, postId);
    }

    public Votes(String userId, String postId,String type) {
        this(userId, postId);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {this.type=type;}



}
