package fctreddit.impl.server.java;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import fctreddit.Discovery;
import fctreddit.api.java.Result;
import fctreddit.api.java.Result.ErrorCode;
import fctreddit.clients.java.ImageClient;
import fctreddit.clients.rest.ImageClients.RestImageClient;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import fctreddit.api.User;
import fctreddit.api.java.Users;
import fctreddit.impl.server.persistence.Hibernate;

public class JavaUsers implements Users {

	private Discovery discovery;

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	private Hibernate hibernate;
	
	public JavaUsers() {
		hibernate = Hibernate.getInstance();
	}

	@Override
	public Result<String> createUser(User user) {
		Log.info("createUser : " + user);

		// Check if user data is valid
		if (user.getUserId() == null || user.getUserId().isEmpty() ||
				user.getPassword() == null || user.getPassword().isEmpty() ||
				user.getFullName() == null || user.getFullName().isEmpty() ||
				user.getEmail() == null || user.getEmail().isEmpty()) {
			Log.info("User object invalid.");
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		try {
			hibernate.persist(user);
		} catch (Exception e) {
			e.printStackTrace(); //Most likely the exception is due to the user already existing...
			Log.info("User already exists.");
			throw new WebApplicationException(Status.CONFLICT);
		}
		
		return Result.ok(user.getUserId());
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		Log.info("getUser : user = " + userId + "; pwd = " + password);

		// Check if user is valid
		if (userId == null || userId.isEmpty() ||
				password == null || password.isEmpty()) {
			Log.info("UserId or password null.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		User user = null;
		try {
			user = hibernate.get(User.class, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// Check if user exists
		if (user == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// Check if the password is correct
		if (!user.getPassword().equals(password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; userData = " + user);
		//---------------Added code------------------//
		if (userId == null || userId.isEmpty() ||
			password == null || password.isEmpty() ||
			user == null) { // Check if userId, password or user is null
            Log.info("Invalid input.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        User existingUser = getUser(userId, password).value();

		if(existingUser == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		if(!password.equals(existingUser.getPassword())) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}


        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }

        try {
            hibernate.update(existingUser); // Update the user in the database
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

        return Result.ok(existingUser);
		//---------------End of added code------------------//
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);
		//---------------Added code------------------//
		if (userId == null || userId.isEmpty() ||
			password == null || password.isEmpty()) { // Check if userId or password is null
            Log.info("Invalid input.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        User user = getUser(userId, password).value();


		if(user == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		if(!password.equals(user.getPassword())) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

        try {
			if(user.getAvatarUrl() != null) {
				//TODO: delete the image from the image server
				URI[] uri = discovery.knownUrisOf("Image",1);
				ImageClient imageClient = new RestImageClient(uri[0]);
				String[] split = user.getAvatarUrl().split("/");
				String[] split2 = split[6].split("//.");
				String imageId = split2[0];
				imageClient.deleteImage(userId,imageId,password);
			}
            hibernate.delete(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

        return Result.ok(user); // Return the deleted user object
		//---------------End of added code------------------//
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
		
		try {
			List<User> list = hibernate.jpql("SELECT u FROM User u WHERE u.userId LIKE '%" + pattern +"%'", User.class);
			return Result.ok(list);
		} catch (Exception e) {
			return Result.error(ErrorCode.BAD_REQUEST);
		}
	}

	/**
	@Override
	public void associateAvatar(String userId, String password, byte[] avatar) {
		Log.info("associate an avatar : user = " + userId + "; pwd = " + password + "; avatarSize = " + avatar.length);

		if (avatar.length == 0) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		User usr = this.getUser(userId, password);

		Path pathToFile = Paths.get(AVATAR_DIRECTORY + File.separator + usr.getUserId() + ".png");

		try {
			Files.deleteIfExists(pathToFile); 
			Files.write(pathToFile, avatar);
		} catch (Exception e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void removeAvatar(String userId, String password) {
		Log.info("delete an avatar : user = " + userId + "; pwd = " + password);
		//---------------Added code------------------//
		getUser(userId, password);
        Path pathToFile = Paths.get(AVATAR_DIRECTORY, userId + ".png");
		
        try {
            Files.deleteIfExists(pathToFile);
        } catch (Exception e) {
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }
		//---------------End of added code------------------//
	}

	@Override
	public byte[] getAvatar(String userId) {
		if (users.get(userId) == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Path pathToFile = Paths.get(AVATAR_DIRECTORY + File.separator + userId + ".png");

		try {
			if (Files.exists(pathToFile)) {
				return Files.readAllBytes(pathToFile);
			} else {
				pathToFile = Paths.get(AVATAR_DIRECTORY + File.separator + DEFAULT_AVATAR_FILE);
				return Files.readAllBytes(pathToFile);
			}
		} catch (Exception e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

	}
	*/

}