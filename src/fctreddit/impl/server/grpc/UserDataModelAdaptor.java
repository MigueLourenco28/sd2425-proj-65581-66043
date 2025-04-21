package fctreddit.impl.server.grpc;

public class UserDataModelAdaptor {
    
    // This class is used to adapt the User data model to the gRPC data model.
    // It is used to convert between the two data models.
    
    public static fctreddit.api.User adaptUser(fctreddit.impl.server.grpc.UserProto.User user) {
        return new fctreddit.api.User(user.getUserId(), user.getPassword(), user.getFullName(), user.getEmail());
    }
    
    public static fctreddit.impl.server.grpc.UserProto.User adaptUser(fctreddit.api.User user) {
        return fctreddit.impl.server.grpc.UserProto.User.newBuilder()
                .setUserId(user.getUserId())
                .setPassword(user.getPassword())
                .setFullName(user.getFullName())
                .setEmail(user.getEmail())
                .build();
    }
}
