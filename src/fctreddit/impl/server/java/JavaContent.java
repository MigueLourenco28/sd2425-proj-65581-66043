package fctreddit.impl.server.java;


import fctreddit.Discovery;
import fctreddit.api.Post;
import fctreddit.api.User;
import fctreddit.api.java.Content;
import fctreddit.api.java.Result;
import fctreddit.api.java.Result.ErrorCode;
import fctreddit.clients.java.ImageClient;
import fctreddit.clients.java.UsersClient;
import fctreddit.clients.rest.ImageClients.RestImageClient;
import fctreddit.clients.rest.UserClients.RestUsersClient;
import fctreddit.impl.server.persistence.Hibernate;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class JavaContent implements Content {

    public static Discovery discovery;

    public static Hibernate hibernate;

    private static Logger Log = Logger.getLogger(JavaContent.class.getName());

    public JavaContent() {
        hibernate = Hibernate.getInstance();
    }

    @Override
    public Result<String> createPost(Post post, String userPassword) {

        User user = hibernate.get(User.class, post.getAuthorId());
        if (user == null) {
            Log.info("User " + post.getAuthorId() + " not found");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (post.getParentUrl() != null && hibernate.get(Post.class, post.getParentUrl()) == null) {
            Log.info("Post " + post.getAuthorId() + " not found");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!user.getPassword().equals(userPassword)) {
            Log.info("User " + post.getAuthorId() + " password does not match");
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }

        try {
            String postId = UUID.randomUUID().toString();
            post.setPostId(postId);
            if(post.getParentUrl() != null){
              //fazer aqui a atualizaçao do numReplies;
            }
            hibernate.persist(post);
            return Result.ok(postId);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.ErrorCode.CONFLICT);
        }
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {
        // TODO Auto-generated method stub

        String query = "SELECT u.postId FROM Post u WHERE u.parentUrl IS NULL";

        if(timestamp > 0){
            query += " AND u.creationTimestamp >= '%" + timestamp +"%'";
        }

        if(sortOrder != null){
            if(sortOrder.equals("MOST_UP_VOTES")){
                query += " ORDER BY u.upVote DESC, u.postId ASC";
            }else if(sortOrder.equals("MOST_REPLIES")){
                query += " ORDER BY u.numReplies DESC, u.postId ASC";
            }
        }
        try {
            List<String> posts = hibernate.jpql(query, String.class);
            return Result.ok(posts);
        }catch (Exception e) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public Result<Post> getPost(String postId) {
        Log.info("getPost : post = " + postId);

		// Check if user is valid
		if (postId == null || postId.isEmpty()) {
			Log.info("PostId null.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		Post post = null;
		try {
			post = hibernate.get(Post.class, postId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// Check if user exists
		if (post == null) {
			Log.info("Post does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return Result.ok(post);
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long maxTimeout) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPostAnswers'");
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

		if(existingPost == null) {
			Log.info("Post does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

        URI[] userUri = discovery.knownUrisOf("Users", 1);
        UsersClient userClient = new RestUsersClient(userUri[0]);
        User author = userClient.getUser(existingPost.getAuthorId(), userPassword).value();

		if(author == Result.error(ErrorCode.FORBIDDEN)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

        if (post.getContent() != null) {
            existingPost.setContent(post.getContent());
        }

        URI[] imageUri = discovery.knownUrisOf("Image", 1);
        ImageClient imageClient = new RestImageClient(imageUri[0]);

        if (post.getMediaUrl() != null) {
            byte[] imageUrl = imageClient.getImage(existingPost.getAuthorId(), post.getMediaUrl()).value();//Check if the mediaUrl exists/has been created
            if(imageUrl == Result.error(ErrorCode.NOT_FOUND).value()) {
                Log.info("Image does not exist.");
                throw new WebApplicationException(Status.NOT_FOUND);
            }
            existingPost.setMediaUrl(post.getMediaUrl());
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
        throw new UnsupportedOperationException("Unimplemented method 'deletePost'");
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upVotePost'");
    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeUpVotePost'");
    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'downVotePost'");
    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeDownVotePost'");
    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getupVotes'");
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDownVotes'");
    }

}