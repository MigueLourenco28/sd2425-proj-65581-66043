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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaContent implements Content {


    public static Hibernate hibernate;

    private static final String USERS = "Users";
    private static final String IMAGE = "Image";
    private static final String CONTENT = "Content";
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    private static Logger Log = Logger.getLogger(JavaContent.class.getName());

    private static Map<String, Object> postLocks = new ConcurrentHashMap<>();


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

        Object lock = null;
        if (post.getParentUrl() != null) {
            String[] splits = post.getParentUrl().split("/");
            String parentId = splits[splits.length - 1];
            Post parent = hibernate.get(Post.class, parentId);

            if (parent == null) {
                Log.info("Post " + post.getAuthorId() + " not found");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }
            postLocks.putIfAbsent(parentId, new Object());
            lock = postLocks.get(parentId);
        }


        String postId = UUID.randomUUID().toString();
        post.setPostId(postId);


        try {

            if (lock != null) {
                synchronized (lock) {
                    hibernate.persist(post);
                    Log.info("Notificar");
                    lock.notifyAll();
                    Log.info("Já notificou");
                }
            } else {
                hibernate.persist(post);
            }
            return Result.ok(postId);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {

        StringBuilder query = new StringBuilder("SELECT u.postId FROM Post u WHERE u.parentUrl IS NULL");

        if (timestamp > 0) {
            query.append(" AND u.creationTimestamp >= ").append(timestamp);
        }

        Log.info("Sort oder: " + sortOrder);

        if (MOST_UP_VOTES.equals(sortOrder)) {
            query.append(" ORDER BY u.upVote DESC, u.postId ASC");
        } else if (MOST_REPLIES.equals(sortOrder)) {
            query.append(" ORDER BY (SELECT COUNT(r) FROM Post r WHERE r.parentUrl LIKE CONCAT('%', u.postId, '%')) DESC, u.postId ASC");
        }


        String result = query.toString();
        try {
            List<String> posts = hibernate.jpql(result, String.class);
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

        Result<Post> result = getPost(postId);
        if (!result.isOK()) return Result.error(result.error());

        if (maxTimeout > 0) {
            Object lock = postLocks.computeIfAbsent(postId, k -> new Object());
            synchronized (lock) {
                try {
                    Log.info("Esperar no lock do postId: " + postId);
                    lock.wait(maxTimeout);
                    Log.info("Parou de esperar");
                } catch (InterruptedException e) {

                }
            }
        }

        List<String> responses = hibernate.jpql("SELECT u.postId FROM Post u WHERE u.parentUrl LIKE '%" + postId + "%'", String.class);
        return Result.ok(responses);
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        Log.info("updatePost : post = " + postId + "; userPassword = " + userPassword + " ; postData = " + post);

        if (postId == null || postId.isEmpty() ||
                userPassword == null || userPassword.isEmpty() ||
                post == null) { // Check if userId, password or user is null
            Log.info("Invalid input.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        Post existingPost;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        existingPost = postResult.value();


        List<String> posts = hibernate.jpql("SELECT u.postId FROM Post u WHERE u.parentUrl LIKE '%" + postId + "%'", String.class);

        if (!posts.isEmpty()) {
            Log.info("Post already has replies.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        if (existingPost.getUpVote() > 0 || existingPost.getDownVote() > 0) {
            Log.info("Post has up votes and/or down votes.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }


        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users userClient = clientFactory.getUserClient();
            Result<User> userResult = userClient.getUser(existingPost.getAuthorId(), userPassword);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }
        } catch (IOException e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }


        if (post.getContent() != null) {
            existingPost.setContent(post.getContent());
        }


        existingPost.setMediaUrl(post.getMediaUrl());
        try {
            hibernate.update(existingPost); // Update the user in the database
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        return Result.ok(existingPost);
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {
        // TODO Auto-generated method stub

        Result<Post> response = getPost(postId);
        if (!response.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(response.error());
        }
        Post post = response.value();
        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users userClient = clientFactory.getUserClient();
            userClient.getUser(post.getAuthorId(), userPassword).value();
            Image imageClient = clientFactory.getImageClient();
            if (post.getMediaUrl() != null) {
                Log.info("Post has media url: " + post.getMediaUrl());
                String[] split = post.getMediaUrl().split("/");
                String imageId = split[split.length - 1];
                Log.info("Image id: " + imageId);
                Log.info("Post author: " + post.getAuthorId());
                Log.info("Image author: " + split[split.length - 2]);
                //Result<Void> xpto = imageClient.deleteImage(post.getAuthorId(), imageId, userPassword);
                //Log.info("Erro aqui: " + xpto.error().toString());
                imageClient.deleteImage(post.getAuthorId(), imageId, userPassword);
            }
            hibernate.jpqlExecute("DELETE FROM Votes u WHERE u.postId = '" + postId + "'");

            List<String> replies = getPostAnswers(postId, 0).value();
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
            List<String> replies = getPostAnswers(reply, 0).value();
            if (!replies.isEmpty()) {
                deleteCascade(replies);
            }
            try {
                hibernate.delete(post);
            } catch (Exception e) {
                e.printStackTrace(); // ou logar com logger
                throw new WebApplicationException("Erro ao apagar post em cascade: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
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

        try {
            hibernate.delete(votes);
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
        int x = post.getUpVote() - 1;
        post.setUpVote(x);
        try {
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

        try {
            hibernate.delete(votes);
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
        int x = post.getDownVote() - 1;
        post.setDownVote(x);
        try {
            hibernate.update(post);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.error(ErrorCode.INTERNAL_ERROR);

        }


    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        Post post;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        post = postResult.value();

        int votes = post.getUpVote();

        Log.info("valor: " + post.getUpVote());
        return Result.ok(votes);
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        Post post;
        Result<Post> postResult = getPost(postId);
        if (!postResult.isOK()) {
            Log.info("Post not authenticated: " + postId);
            return Result.error(postResult.error());
        }
        post = postResult.value();

        int votes = post.getDownVote();
        Log.info("valor: " + post.getDownVote());
        return Result.ok(votes);
    }

    @Override
    public Result<Integer> deletedUser(String userId, String userPassword) {
        List<Votes> votes = hibernate.jpql("SELECT u FROM Votes u WHERE u.userId = '" + userId + "'", Votes.class);
        List<Post> posts = new ArrayList<Post>();
        for (Votes vote : votes) {
            Post post = getPost(vote.getPostId()).value();
            if (vote.getType().equals(UP)) {
                post.setUpVote(post.getUpVote() - 1);
            } else {
                post.setDownVote(post.getDownVote() - 1);
            }
            posts.add(post);
        }
        try {
            hibernate.update(posts.toArray());
        } catch (Exception e) {
            return Result.error(ErrorCode.CONFLICT);
        }
        int num = hibernate.jpqlExecute("UPDATE Post u SET u.authorId = NULL WHERE u.authorId = '" + userId + "'");
        hibernate.delete(votes.toArray());
        Log.info("Linhas alteradas: " + num);
        return Result.ok(num);
    }


}