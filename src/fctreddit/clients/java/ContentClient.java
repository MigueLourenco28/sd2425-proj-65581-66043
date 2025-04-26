package fctreddit.clients.java;

import fctreddit.api.Post;
import fctreddit.api.java.Content;
import fctreddit.api.java.Image;
import fctreddit.api.java.Result;

import java.util.List;

public abstract class ContentClient extends Client implements Content {


    abstract public Result<String> createPost(Post post, String userPassword);

    abstract public Result<List<String>> getPosts(long timestamp, String sortOrder);

    abstract public Result<Post> getPost(String postId);

    abstract public Result<List<String>> getPostAnswers(String postId, long maxTimeout);

    abstract public Result<Post> updatePost(String postId, String userPassword, Post post);

    abstract public Result<Void> deletePost(String postId, String userPassword);

    abstract public Result<Void> upVotePost(String postId, String userId, String userPassword);

    abstract public Result<Void> removeUpVotePost(String postId, String userId, String userPassword);

    abstract public Result<Void> downVotePost(String postId, String userId, String userPassword);

    abstract public Result<Void> removeDownVotePost(String postId, String userId, String userPassword);

    abstract public Result<Integer> getupVotes(String postId);

    abstract public Result<Integer> getDownVotes(String postId);



}
