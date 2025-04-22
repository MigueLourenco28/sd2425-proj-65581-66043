package fctreddit.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Votes  {

    @Id
    private String userId;
    @Id
    private String postId;
    private String type;


    public Votes() {
    }

    public Votes(String userId, String postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public Votes(String userId, String postId,String type) {
        this(userId, postId);
        this.type = type;
    }

    // Getters e Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {this.userId = userId;}

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) { this.postId = postId; }

    public String getType() {
        return type;
    }

    public void setType(String type) {this.type=type;}



}
