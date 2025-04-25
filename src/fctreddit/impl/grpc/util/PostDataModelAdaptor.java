package fctreddit.impl.grpc.util;

import fctreddit.api.Post;
import fctreddit.impl.grpc.generated_java.ContentProtoBuf.GrpcPost;
import fctreddit.impl.grpc.generated_java.ContentProtoBuf.GrpcPost.Builder;

public class PostDataModelAdaptor {

	//Notice that optional values in a Message might not have an
	//assigned value (although for Strings default value is "") so
	//before assigning we check if the field has a value, if not
	//we assign null.
	public static Post GrpPost_to_Post( GrpcPost from )  {
		return new Post( 
				from.hasPostId() ? from.getPostId() : null, 
				from.hasAuthorId() ? from.getAuthorId() : null,
				from.hasCreationTimestamp() ? from.getCreationTimestamp() : null, 
				from.hasContent() ? from.getContent() : null, 
				from.hasMediaUrl() ? from.getMediaUrl() : null,
                from.hasParentUrl() ? from.getParentUrl() : null,
                from.hasUpVote() ? from.getUpVote() : null,
                from.hasDownVote() ? from.getDownVote() : null);
	}

	//Notice that optional values might not have a value, and 
	//you should never assign null to a field in a Message
	public static GrpcPost Post_to_GrpcPost( Post from )  {
		Builder b = GrpcPost.newBuilder();
		
		if(from.getPostId() != null)
			b.setPostId( from.getPostId());
		
		if(from.getAuthorId() != null)
			b.setAuthorId( from.getAuthorId());
		
		if(from.getCreationTimestamp() != 0)
			b.setCreationTimestamp( from.getCreationTimestamp());
		
		if(from.getContent() != null)
			b.setContent( from.getContent());
		
		if(from.getMediaUrl() != null)
			b.setMediaUrl( from.getMediaUrl());

        if(from.getParentUrl() != null)
            b.setParentUrl( from.getParentUrl());

        if(from.getUpVote() != 0)
            b.setUpVote( from.getUpVote());

        if(from.getDownVote() != 0)
            b.setDownVote( from.getDownVote());
		
		return b.build();
	}

}
