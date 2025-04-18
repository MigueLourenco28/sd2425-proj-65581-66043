package fctreddit.impl.server.java;

import fctreddit.Discovery;
import fctreddit.api.Post;
import fctreddit.api.User;
import fctreddit.api.java.Content;
import fctreddit.api.java.Result;
import fctreddit.impl.server.persistence.Hibernate;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class JavaContent implements Content {

    public static Discovery discovery;
    private Hibernate hibernate;

    private static Logger Log = Logger.getLogger(JavaContent.class.getName());

    public JavaContent() {
        hibernate = Hibernate.getInstance();
    }

    @Override
    public Result<String> createPost(Post post, String userPassword) {
        try {
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

            String postId = UUID.randomUUID().toString();
            post.setPostId(postId);
            post.setCreationTimestamp(System.currentTimeMillis());
            hibernate.persist(post);
            return Result.ok(postId);

        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public Result<List<String>> getPosts(long timestamp, String sortOrder) {
        return null;
    }

    @Override
    public Result<Post> getPost(String postId) {
        return null;
    }

    @Override
    public Result<List<String>> getPostAnswers(String postId, long maxTimeout) {
        return null;
    }

    @Override
    public Result<Post> updatePost(String postId, String userPassword, Post post) {
        return null;
    }

    @Override
    public Result<Void> deletePost(String postId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> upVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> removeUpVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> downVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Void> removeDownVotePost(String postId, String userId, String userPassword) {
        return null;
    }

    @Override
    public Result<Integer> getupVotes(String postId) {
        return null;
    }

    @Override
    public Result<Integer> getDownVotes(String postId) {
        return null;
    }
}
