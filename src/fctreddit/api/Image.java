package fctreddit.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Represents a User in the system
 */
@Entity
public class Image {
	@Id
	private String userId;
	private byte[] imageContents;
	
	public Image(){	
	}
	
	public Image(String userId, byte[] imageContents) {
		super();
        this.userId = userId;
        this.imageContents = imageContents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imageContents == null) ? 0 : imageContents.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Image other = (Image) obj;
        if (imageContents == null) {
            if (other.imageContents != null)
                return false;
        } else if (!imageContents.equals(other.imageContents))
            return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

    public byte[] getImageContents() {
        return imageContents;
    }

    public void setImageContents(byte[] imageContents) {
        this.imageContents = imageContents;
    }

	@Override
	public String toString() {
		return "Image [userId=" + userId + ", imageContents=" + imageContents + "]";
	}	
}