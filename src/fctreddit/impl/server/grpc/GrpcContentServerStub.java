package fctreddit.impl.server.grpc;

import java.util.List;

import fctreddit.impl.grpc.generated_java.ContentProtoBuf;
import org.hibernate.sql.Update;

import fctreddit.api.Post;
import fctreddit.api.User;
import fctreddit.api.java.Content;
import fctreddit.api.java.Result;
import fctreddit.impl.grpc.generated_java.ContentGrpc;
import fctreddit.impl.grpc.generated_java.ContentProtoBuf.*;
import fctreddit.impl.grpc.generated_java.ImageProtoBuf.CreateImageResult;
import fctreddit.impl.grpc.generated_java.UsersProtoBuf.CreateUserResult;
import fctreddit.impl.grpc.generated_java.UsersProtoBuf.GetUserResult;
import fctreddit.impl.grpc.util.PostDataModelAdaptor;
import fctreddit.impl.grpc.util.UserDataModelAdaptor;
import io.grpc.stub.StreamObserver;
import fctreddit.impl.server.java.JavaContent;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;

public class GrpcContentServerStub implements ContentGrpc.AsyncService, BindableService {
    
    Content impl = new JavaContent();

    @Override
    public ServerServiceDefinition bindService() {
        return ContentGrpc.bindService(this);
    }

    @Override
    public void createPost(CreatePostArgs request, StreamObserver<CreatePostResult> responseObserver) {
        Result<String> res = impl.createPost( PostDataModelAdaptor.GrpPost_to_Post(request.getPost()), request.getPassword());	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
			responseObserver.onNext( CreatePostResult.newBuilder().setPostId( res.value() ).build());
			responseObserver.onCompleted();
    	}
    }

    @Override
    public void getPosts(GetPostsArgs request, StreamObserver<GetPostsResult> responseObserver) {
        Result<List<String>> res = impl.getPosts(request.getTimestamp(), request.getSortOrder());
		if( ! res.isOK() )
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
            responseObserver.onNext(ContentProtoBuf.GetPostsResult.newBuilder().addAllPostId(res.value()).build());
            responseObserver.onCompleted();
		}
    }

    @Override
    public void getPost(GetPostArgs request, StreamObserver<GrpcPost> responseObserver) {
        Result<Post> res = impl.getPost(request.getPostId());
		if( ! res.isOK() )
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext(PostDataModelAdaptor.Post_to_GrpcPost(res.value()));
			responseObserver.onCompleted();
		}
    }

    @Override
    public void getPostAnswers(GetPostAnswersArgs request, StreamObserver<GetPostsResult> responseObserver) {
        Result<List<String>> res = impl.getPostAnswers(request.getPostId(), request.getTimeout());
		if( ! res.isOK() )
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
            responseObserver.onNext(ContentProtoBuf.GetPostsResult.newBuilder().addAllPostId(res.value()).build());
			responseObserver.onCompleted();
		}
    }

    @Override
    public void updatePost(UpdatePostArgs request, StreamObserver<  GrpcPost> responseObserver) {
        Result<Post> res = impl.updatePost( request.getPostId() , request.getPassword(), PostDataModelAdaptor.GrpPost_to_Post(request.getPost()));	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
			responseObserver.onNext( PostDataModelAdaptor.Post_to_GrpcPost(res.value()));
			responseObserver.onCompleted();
    	}
    }
    
    @Override
    public void deletePost(DeletePostArgs request, StreamObserver<EmptyMessage> responseObserver) {
        Result<Void> res = impl.deletePost( request.getPostId() , request.getPassword());	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
			responseObserver.onNext( EmptyMessage.newBuilder().build());
			responseObserver.onCompleted();
    	}
    }

    @Override
    public void upVotePost(ChangeVoteArgs request, StreamObserver<EmptyMessage> responseObserver) {
        Result<Void> res = impl.upVotePost( request.getPostId() , request.getUserId(), request.getPassword());	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
			responseObserver.onNext(EmptyMessage.newBuilder().build());
			responseObserver.onCompleted();
    	}
    }
    @Override
    public void removeUpVotePost(ChangeVoteArgs request, StreamObserver<EmptyMessage> responseObserver) {
        Result<Void> res = impl.removeUpVotePost( request.getPostId() , request.getUserId(), request.getPassword());	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
			responseObserver.onNext( EmptyMessage.newBuilder().build());
			responseObserver.onCompleted();
    	}
    }

    @Override
    public void downVotePost(ChangeVoteArgs request, StreamObserver<EmptyMessage> responseObserver) {
        Result<Void> res = impl.downVotePost( request.getPostId() , request.getUserId(), request.getPassword());	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
            responseObserver.onNext( EmptyMessage.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void removeDownVotePost(ChangeVoteArgs request, StreamObserver<EmptyMessage> responseObserver) {
        Result<Void> res = impl.removeDownVotePost( request.getPostId() , request.getUserId(), request.getPassword());	
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
            responseObserver.onNext( EmptyMessage.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUpVotes(GetPostArgs request, StreamObserver<VoteCountResult> responseObserver) {
        Result<Integer> res = impl.getupVotes(request.getPostId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(VoteCountResult.newBuilder().setCount(res.value()).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getDownVotes(GetPostArgs request, StreamObserver<VoteCountResult> responseObserver) {
        Result<Integer> res = impl.getDownVotes(request.getPostId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(VoteCountResult.newBuilder().setCount(res.value()).build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void deletedUser(DeletedUserArgs request, StreamObserver<DeletedUserResult> responseObserver) {
        Result<Integer> res = impl.deletedUser(request.getUserId(), request.getUserPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(DeletedUserResult.newBuilder().setNum(res.value()).build());
            responseObserver.onCompleted();
        }
    }

    protected static Throwable errorCodeToStatus( Result.ErrorCode error ) {
    	var status =  switch( error) {
    	case NOT_FOUND -> io.grpc.Status.NOT_FOUND; 
    	case CONFLICT -> io.grpc.Status.ALREADY_EXISTS;
    	case FORBIDDEN -> io.grpc.Status.PERMISSION_DENIED;
    	case NOT_IMPLEMENTED -> io.grpc.Status.UNIMPLEMENTED;
    	case BAD_REQUEST -> io.grpc.Status.INVALID_ARGUMENT;
    	default -> io.grpc.Status.INTERNAL;
    	};
    	
    	return status.asException();
    }

}
