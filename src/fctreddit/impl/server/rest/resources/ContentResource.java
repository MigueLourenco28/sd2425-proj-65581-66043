package fctreddit.impl.server.rest.resources;

import fctreddit.api.Post;
import fctreddit.api.java.Content;
import fctreddit.api.java.Result;
import fctreddit.api.rest.RestContent;
import fctreddit.impl.server.java.JavaContent;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;
import java.util.logging.Logger;

public class ContentResource implements RestContent {

    private static Logger Log = Logger.getLogger(ContentResource.class.getName());

    private Content cont;

    public ContentResource() {
        cont = new JavaContent();
    }

    @Override
    public String createPost(Post post, String userPassword) {
        Log.info("createPost : " + post);
		
		Result<String> res = cont.createPost(post, userPassword);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }

    @Override
    public List<String> getPosts(long timestamp, String sortOrder) {
        Log.info("getPosts : timestamp = " + timestamp + "; sortOrder = " + sortOrder);

		Result<List<String>> res = cont.getPosts(timestamp, sortOrder);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }

    @Override
    public Post getPost(String postId) {
        Log.info("getPost : postId = " + postId );

		Result<Post> res = cont.getPost(postId);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }

    @Override
    public List<String> getPostAnswers(String postId, long timeout) {
        Log.info("getPostAnswers : postId = " + postId + "; timeout = " + timeout);

		Result<List<String>> res = cont.getPostAnswers(postId, timeout);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }

    @Override
    public Post updatePost(String postId, String userPassword, Post post) {
        Log.info("updatePost : post = " + postId + "; userPassword = " + userPassword + " ; postData = " + post);
		
		Result<Post> res = cont.updatePost(postId, userPassword, post);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }

    @Override
    public void deletePost(String postId, String userPassword) {
        Log.info("deletePost : post = " + postId + "; userPassword = " + userPassword);
		
		Result<Void> res = cont.deletePost(postId, userPassword);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
    }

    @Override
    public void upVotePost(String postId, String userId, String userPassword) {
        Log.info("upVotePost : post = " + postId + "; userId = " + userId + "; userPassword = " + userPassword);
		
		Result<Void> res = cont.upVotePost(postId, userId, userPassword);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
    }

    @Override
    public void removeUpVotePost(String postId, String userId, String userPassword) {
        Log.info("removeUpVotePost : post = " + postId + "; userId = " + userId + "; userPassword = " + userPassword);
		
		Result<Void> res = cont.removeUpVotePost(postId, userId, userPassword);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
    }

    @Override
    public void downVotePost(String postId, String userId, String userPassword) {
        Log.info("downVotePost : post = " + postId + "; userId = " + userId + "; userPassword = " + userPassword);
		
		Result<Void> res = cont.downVotePost(postId, userId, userPassword);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
    }

    @Override
    public void removeDownVotePost(String postId, String userId, String userPassword) {
        Log.info("removeDownVotePost : post = " + postId + "; userId = " + userId + "; userPassword = " + userPassword);
		
		Result<Void> res = cont.removeDownVotePost(postId, userId, userPassword);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
    }

    @Override
    public Integer getupVotes(String postId) {
        Log.info("getUpVotes : postId = " + postId );

		Result<Integer> res = cont.getupVotes(postId);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }

    @Override
    public Integer getDownVotes(String postId) {
        Log.info("getDownVotes : postId = " + postId );

		Result<Integer> res = cont.getDownVotes(postId);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}
		return res.value();
    }



	@Override
	public int deletedUser(String userId, String password) {
		Log.info("deletedUser : userId = " + userId);

		Result<Integer> res = cont.deletedUser(userId, password);
		if(!res.isOK()) {
			throw new WebApplicationException(errorCodeToStatus(res.error()));
		}

		return res.value();
	}




	protected static Status errorCodeToStatus( Result.ErrorCode error ) {
    	Status status =  switch( error) {
    	case NOT_FOUND -> Status.NOT_FOUND; 
    	case CONFLICT -> Status.CONFLICT;
    	case FORBIDDEN -> Status.FORBIDDEN;
    	case NOT_IMPLEMENTED -> Status.NOT_IMPLEMENTED;
    	case BAD_REQUEST -> Status.BAD_REQUEST;
    	default -> Status.INTERNAL_SERVER_ERROR;
    	};
    	
    	return status;
    }

}
