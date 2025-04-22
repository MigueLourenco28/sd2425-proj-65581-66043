package fctreddit.impl.server.java;


import fctreddit.Discovery;
import fctreddit.api.Post;
import fctreddit.api.User;
import fctreddit.api.Votes;
import fctreddit.api.java.Content;
import fctreddit.api.java.Image;
import fctreddit.api.java.Result;
import fctreddit.api.java.Result.ErrorCode;

import fctreddit.clients.java.ImageClient;
import fctreddit.api.java.Users;

import fctreddit.clients.java.UsersClient;
import fctreddit.clients.rest.ImageClients.RestImageClient;
import fctreddit.clients.rest.UserClients.RestUsersClient;
import fctreddit.impl.server.persistence.Hibernate;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class JavaContent implements Content {


    public static Hibernate hibernate;

    private static final String USERS = "Users";
    private static final String IMAGE = "Image";
    private static final String CONTENT = "Content";
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    private static Logger Log = Logger.getLogger(JavaContent.class.getName());

    public JavaContent() {
        hibernate = Hibernate.getInstance();
    }

    @Override
    public Result<String> createPost(Post post, String userPassword) {

        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users client = clientFactory.getUserClient();
            Result<User> userResult = client.getUser(post.getAuthorId(), userPassword);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }

        } catch (IOException e) {
            return Result.error(ErrorCode.NOT_FOUND);
        }

        if (post.getParentUrl() != null) {
            String[] splits = post.getParentUrl().split("/");
            String parentId = splits[splits.length - 1];

            Post parent = hibernate.get(Post.class, parentId);

            if (parent == null) {
                Log.info("Post " + post.getAuthorId() + " not found");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }
            parent.setNumReplies(parent.getNumReplies() + 1);
            hibernate.update(parent);
        }

        String postId = UUID.randomUUID().toString();
        post.setPostId(postId);

        try {

            hibernate.persist(post);
            return Result.ok(postId);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {
        // TODO Auto-generated method stub

        String query = "SELECT u.postId FROM Post u WHERE u.parentUrl IS NULL";

        if (timestamp > 0) {
            query += " AND u.creationTimestamp >= '%" + timestamp + "%'";
        }

        if (sortOrder != null) {
            if (sortOrder.equals("MOST_UP_VOTES")) {
                query += " ORDER BY u.upVote DESC, u.postId ASC";
            } else if (sortOrder.equals("MOST_REPLIES")) {
                query += " ORDER BY u.numReplies DESC, u.postId ASC";
            }
        }
        try {
            List<String> posts = hibernate.jpql(query, String.class);
            return Result.ok(posts);
        } catch (Exception e) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public Result<Post> getPost(String postId) {
        Log.info("getPost : post = " + postId);

        // Check if the post is valid
        if (postId == null || postId.isEmpty()) {
            Log.info("PostId null.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        Post post = null;
        try {
            post = hibernate.get(Post.class, postId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ErrorCode.NOT_FOUND);
        }

        // Check if user exists
        if (post == null) {
            Log.info("Post does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);
        }

        return Result.ok(post);
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long maxTimeout) {
        // TODO Auto-generated method stub

        Post post = getPost(postId).value();

        if (maxTimeout > 0) {
            //temos de adicionar um wait mas nao sei como se faz.
            return null;
        } else {
            List<String> responses = hibernate.jpql("SELECT u.postId FROM Post u WHERE u.parentUrl LIKE '%" + postId + "%'", String.class);
            return Result.ok(responses);
        }

    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        Log.info("updatePost : post = " + postId + "; userPassword = " + userPassword + " ; postData = " + post);

        if (postId == null || postId.isEmpty() ||
                userPassword == null || userPassword.isEmpty() ||
                post == null) { // Check if userId, password or user is null
            Log.info("Invalid input.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        Post existingPost = getPost(postId).value();

        if (existingPost == null) {
            Log.info("Post does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users userClient = clientFactory.getUserClient();
            Result<User> userResult = userClient.getUser(post.getAuthorId(), userPassword); // <---- ERROR
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }
        } catch (IOException e) {
            return Result.error(ErrorCode.NOT_FOUND);
        }

        if (post.getContent() != null) {
            existingPost.setContent(post.getContent());
        }

        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Image imageClient = clientFactory.getImageClient();
            Result<byte[]> imageResult = imageClient.getImage(existingPost.getAuthorId(), post.getMediaUrl()); //Check if the mediaUrl exists/has been created
            if (!imageResult.isOK()) {
                Log.warning("Image not authenticated: " + imageResult.error());
                return Result.error(imageResult.error());
            }
            existingPost.setMediaUrl(post.getMediaUrl()); // Update the mediaUrl with the new one (knwn that it exists)
        } catch (IOException e) {
            return Result.error(ErrorCode.NOT_FOUND);
        }

        try {
            hibernate.update(existingPost); // Update the user in the database
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

        return Result.ok(existingPost);
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {
        // TODO Auto-generated method stub

        Post post = getPost(postId).value();
        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users userClient = clientFactory.getUserClient();
            userClient.getUser(post.getAuthorId(), userPassword).value();
            Image imageClient = clientFactory.getImageClient();
            if (post.getMediaUrl() != null) {
                String[] split = post.getMediaUrl().split("/");
                String[] split2 = split[6].split("//.");
                String imageId = split2[0];
                imageClient.deleteImage(post.getAuthorId(), imageId, userPassword);
            }
            //adicionar o remover posts q deram a resposta ao post principal em cascata
            List<String> replies = getPostAnswers(postId, 100000).value();
            deleteCascade(replies);
            hibernate.delete(post);
            return Result.ok(null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteCascade(List<String> repliesIds) {
        for (String reply : repliesIds) {
            Post post = getPost(reply).value();
            hibernate.delete(post);
            if (!getPostAnswers(reply, 100000).value().isEmpty()) {
                deleteCascade(getPostAnswers(reply, 100000).value());
            }
        }
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {
        Post post;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        post = postResult.value();


        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users client = clientFactory.getUserClient();
            Result<User> userResult = client.getUser(userId, userPassword);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }
        } catch (IOException e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        try {
            Votes vote = new Votes(userId, postId, UP);
            Log.info(vote.getPostId() + "     " + vote.getUserId() + "       " + vote.getType());
            hibernate.persist(vote);
        } catch (Exception e) {
            Log.info(e.getMessage());
            return Result.error(ErrorCode.CONFLICT);
        }

        int x = post.getUpVote() + 1;
        post.setUpVote(x);

        try {
            hibernate.update(post);
            Log.info("VoteValue: " + post.getUpVote());
            return Result.ok(null);
        } catch (Exception e) {
            Log.info(e.getMessage());
            return Result.error(ErrorCode.CONFLICT);

        }


    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        Post post;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        post = postResult.value();


        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users client = clientFactory.getUserClient();
            Result<User> userResult = client.getUser(userId, userPassword);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }

        } catch (IOException e) {
            return Result.error(ErrorCode.NOT_FOUND);
        }

        Votes votes;
        try {
            Votes vote = new Votes(userId, postId);
            votes = hibernate.get(Votes.class, vote);
            if (!votes.getType().equals(UP)) {
                Log.warning("User did not vote up: " + postId);
                return Result.error(ErrorCode.CONFLICT);
            }
        } catch (Exception d) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        try{
            hibernate.delete(votes);
        }catch(Exception e){
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
        int x = post.getUpVote() - 1;
        post.setUpVote(x);
        try{
            hibernate.update(Post.class, post);
            return Result.ok(null);
        } catch (Exception d) {
            return Result.error(ErrorCode.INTERNAL_ERROR);

        }


    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        Post post;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        post = postResult.value();


        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users client = clientFactory.getUserClient();
            Result<User> userResult = client.getUser(userId, userPassword);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        try {
            Votes vote = new Votes(userId, postId, DOWN);
            Log.info(vote.getPostId() + "     " + vote.getUserId() + "       " + vote.getType());
            hibernate.persist(vote);
        } catch (Exception e) {
            Log.info(e.getMessage());
            return Result.error(ErrorCode.CONFLICT);
        }

        int x = post.getDownVote() + 1;
        post.setDownVote(x);
        try {
            hibernate.update(post);
            Log.info("VoteValue: " + post.getUpVote());
            return Result.ok(null);
        } catch (Exception e) {
            Log.info(e.getMessage());
            return Result.error(ErrorCode.CONFLICT);

        }


    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        Post post;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        post = postResult.value();


        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users client = clientFactory.getUserClient();
            Result<User> userResult = client.getUser(userId, userPassword);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }

        } catch (Exception e) {
            return Result.error(ErrorCode.NOT_FOUND);
        }

        Votes votes;
        try {
            Votes vote = new Votes(userId, postId);
            votes = hibernate.get(Votes.class, vote);
            if (!votes.getType().equals(DOWN)) {
                Log.warning("User did not vote up: " + postId);
                return Result.error(ErrorCode.CONFLICT);
            }
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        try{
            hibernate.delete(votes);
        }catch(Exception e){
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
        int x = post.getDownVote() - 1;
        post.setDownVote(x);
        try{
            hibernate.update(post);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);

        }



    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        Post post;
        try {
            post = hibernate.get(Post.class, postId);
        }catch (Exception e){
            Log.warning(e.getMessage());
            return Result.error(ErrorCode.NOT_FOUND);
        }

        int votes = post.getUpVote();

        Log.info("valor: " + post.getUpVote());
        return Result.ok(votes);
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        Post post;
        try {
            post = hibernate.get(Post.class, postId);
        }catch (Exception e){
            Log.warning(e.getMessage());
            return Result.error(ErrorCode.NOT_FOUND);
        }

        int votes = post.getDownVote();
        Log.info("valor: " + post.getDownVote());
        return Result.ok(votes);
    }

}