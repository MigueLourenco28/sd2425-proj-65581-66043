package fctreddit.impl.server.java;


import fctreddit.api.Post;
import fctreddit.api.User;
import fctreddit.api.java.Content;
import fctreddit.api.java.Result;
import fctreddit.impl.server.persistence.Hibernate;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class JavaContent implements Content {

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
            post.setCreationTimestamp(System.currentTimeMillis());
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
        throw new UnsupportedOperationException("Unimplemented method 'getPosts'");
    }

    @Override
    public Result<Post> getPost(String postId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPost'");
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long maxTimeout) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPostAnswers'");
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePost'");
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