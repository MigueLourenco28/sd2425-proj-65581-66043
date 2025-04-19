package fctreddit.impl.server.java;

import fctreddit.Discovery;
import fctreddit.api.User;
import fctreddit.api.java.Image;
import fctreddit.api.java.Result;
import fctreddit.api.java.Result.ErrorCode;
import fctreddit.clients.java.UsersClient;
import fctreddit.clients.rest.UserClients.RestUsersClient;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaImage implements Image {

    public static Discovery discovery;

    private static Logger Log = Logger.getLogger(JavaImage.class.getName());

    public JavaImage() {
    }

    @Override
    public Result<String> createImage(String userId, byte[] imageContents, String password) {

        if (imageContents.length == 0 ||
                password == null || password.isEmpty()) {
            Log.info("Image or password null.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        URI[] uri = discovery.knownUrisOf("Users", 1);
        UsersClient client = new RestUsersClient(uri[0]);
        User user = client.getUser(userId, password).value();

        if (user == null) {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);
        }

        String pwd = user.getPassword();

        if (!pwd.equals(password)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        String imageId = UUID.randomUUID().toString();

        Path imagePath = Paths.get("fctreddit", "images", user.getUserId(), imageId + ".jpg");

        try {
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageContents);
        } catch (IOException e) {
            Log.severe("Error saving image: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        String imageUrl = uri[0].toString() +  "/" + imagePath.toString();

        return Result.ok(imageUrl);
    }

    @Override
    public Result<byte[]> getImage(String userId, String imageId) {

        Path imageDir = Paths.get("fctreddit/images/" + userId + "/" + imageId + ".jpg");


        if (Files.exists(imageDir)) {
            try {
                byte[] imageData = Files.readAllBytes(imageDir);
                return Result.ok(imageData);
            } catch (IOException e) {
                Log.severe("Error getting the  image: " + e.getMessage());
                return Result.error(ErrorCode.INTERNAL_ERROR);
            }
        } else {
            Log.info("The image with the id " + imageId + "and userId " +
                    userId + " does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);
        }

    }

    @Override
    public Result<Void> deleteImage(String userId, String imageId, String password) {

        if (password == null || password.isEmpty()) {
            Log.info("Password null.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        Path imageDir = Paths.get("fctreddit/images/" + userId + "/" + imageId + ".jpg");


        if (!Files.exists(imageDir)) {
            Log.info("Image doesn't exist: " + imageId);
            return Result.error(ErrorCode.NOT_FOUND);
        }

        URI[] uri = discovery.knownUrisOf("Users", 1);
        UsersClient client = new RestUsersClient(uri[0]);
        User user = client.getUser(userId, password).value();

        if (user == null) {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);
        }

        String pwd = user.getPassword();

        if (!pwd.equals(password)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        try {
            Files.delete(imageDir);
            Log.info("Image deleted successfully: " + imageId);
        } catch (IOException e) {
            Log.severe("Error deleting image: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }


        return Result.ok();
    }
}