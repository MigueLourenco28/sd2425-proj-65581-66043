package fctreddit.impl.server.java;

import fctreddit.Discovery;
import fctreddit.api.User;
import fctreddit.api.java.Image;
import fctreddit.api.java.Result;
import fctreddit.api.java.Result.ErrorCode;
import fctreddit.api.java.Users;
import fctreddit.clients.java.Client;
import fctreddit.clients.java.UsersClient;
import fctreddit.clients.rest.UserClients.RestUsersClient;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaImage implements Image {

    private static final String USERS = "Users";
    private static final String IMAGE = "Image";
    private static final String CONTENT = "Content";

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
        User user = null;
        String URI;
        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            Users client = clientFactory.getUserClient();
            Result<User> userResult = client.getUser(userId, password);
            if (!userResult.isOK()) {
                Log.warning("User not authenticated: " + userResult.error());
                return Result.error(userResult.error());
            }
            user = userResult.value();
            URI = clientFactory.getURIClient(IMAGE).toString();

        } catch (IOException e) {
            return Result.error(ErrorCode.NOT_FOUND);
        }// Checks if user exists and if password is correct

        String imageId = UUID.randomUUID().toString();

        Path baseDir = Paths.get("/home/sd/image");

        try {

            if (!Files.exists(baseDir)) {

                Files.createDirectories(baseDir);

                Log.info("Diretório criado: " + baseDir.toString());
            }
        } catch (IOException e) {

            Log.severe("Erro ao verificar/criar diretório base: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
        String userPath = baseDir.toString() + File.separator + userId;
        String imagePath = userPath + File.separator + (imageId + ".jpg");
        Path path = Paths.get(imagePath);

        try {

            File f = path.getParent().toFile();
            if (!f.exists()) {

                f.mkdirs();
                Log.info("Diretório do utilizador criado: " + f.getAbsolutePath());
            }
            Files.write(path, imageContents);

            Log.info("Imagem salva em: " + imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.severe("Error saving image: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }

        String imageUrl = String.format("%s/image/%s/%s.jpg", URI, user.getUserId(), imageId);

        Log.info("Imagem salva em: " + imageUrl);
        return Result.ok(imageUrl);
    }

    @Override
    public Result<byte[]> getImage(String userId, String imageId) {

        Log.info("Imagem recuperado: " + imageId);
        String path ="/home/sd/image/" + userId + File.separator + imageId;
        Path imageDir = Paths.get(path);
        Log.info("estou aqui: 1");
        File f = imageDir.toFile();
        Log.info(String.valueOf(f.exists()));

        if (f.exists()) {
            try {
                Log.info("estou aqui: 2");
                byte[] imageData = Files.readAllBytes(imageDir);
                Log.info("estou aqui: 3");
                return Result.ok(imageData);
            } catch (IOException e) {
                Log.severe("Error getting the  image: " + e.getMessage());
                return Result.error(ErrorCode.INTERNAL_ERROR);
            }
        } else {
            Log.info("estou aqui: 4");
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

        Path imageDir = Paths.get("fctreddit/image/" + userId + "/" + imageId + ".jpg");


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