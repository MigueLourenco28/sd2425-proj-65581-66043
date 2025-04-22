package fctreddit.impl.server.grpc;

import fctreddit.api.User;
import fctreddit.api.java.Image;
import fctreddit.api.java.Result;
import fctreddit.impl.grpc.generated_java.ImageGrpc;
import fctreddit.impl.grpc.generated_java.ImageProtoBuf.*;
import fctreddit.impl.grpc.generated_java.UsersProtoBuf.GetUserResult;
import fctreddit.impl.grpc.util.UserDataModelAdaptor;
import io.grpc.stub.StreamObserver;
import fctreddit.impl.server.java.JavaImage;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;

public class GrpcImageServerStub implements ImageGrpc.AsyncService, BindableService {

    Image impl = new JavaImage();

    @Override
    public ServerServiceDefinition bindService() {
        return ImageGrpc.bindService(this);
    }

    @Override
    public void createImage(CreateImageArgs request, StreamObserver<CreateImageResult> responseObserver) {
        /**
        Result<String> res = impl.createImage(request.getUserId(), request.getImageContents().toByteArray(), request.getPassword());
		if( ! res.isOK() )
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( CreateImageResult.newBuilder().setImageId(res.value())).build();
			responseObserver.onCompleted();
		}
        */
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getImage'");
    }

    @Override
    public void getImage(GetImageArgs request, StreamObserver<GetImageResult> responseObserver) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getImage'");
    }

    @Override
    public void deleteImage(DeleteImageArgs request, StreamObserver<DeleteImageResult> responseObserver) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteImage'");
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