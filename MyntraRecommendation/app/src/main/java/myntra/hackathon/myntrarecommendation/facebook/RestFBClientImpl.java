package myntra.hackathon.myntrarecommendation.facebook;
import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Album;
import com.restfb.types.Photo;
import com.restfb.types.User;

public class RestFBClientImpl implements RestFBClient {
	private static final String FB_ME_FRIENDS_END_POINT = "me/friends";
	private static final String FB_ALBUMS_END_POINT = "/albums";
	private static final String FB_PHOTOS_END_POINT = "/photos";

	private static final String PARAMETER_KEY_FIELDS = "fields";

	private static final String PARAMETER_VALUE_TYPE = "type";
	private static final String PARAMATER_VALUE_IMAGES ="images";
	private static final String PARAMATER_VALUE_NAME ="name";
	private static final String PARAMATER_VALUE_GENDER ="gender";

	private static final String ALBUM_TYPE_PROFILE = "profile";

	private FacebookClient facebookClient;

	public RestFBClientImpl(String authToken) {
		facebookClient = new DefaultFacebookClient(authToken);
	}

	public List<User> getFriendList() {
		Connection<User> friends = facebookClient.fetchConnection(FB_ME_FRIENDS_END_POINT, User.class,
				Parameter.with(PARAMETER_KEY_FIELDS, PARAMATER_VALUE_NAME + "," + PARAMATER_VALUE_GENDER));
		List<User> friendList = friends.getData();

		return friendList;
	}

	public List<String> getProfilePhotosUrl(User user) {
		String fbAlbumRestEndPointPrefix = user.getId() + FB_ALBUMS_END_POINT;
		// Get all albums of a user
		Connection<Album> albums = facebookClient.fetchConnection(fbAlbumRestEndPointPrefix, Album.class, Parameter.with(PARAMETER_KEY_FIELDS, PARAMETER_VALUE_TYPE));
		List<Album> albumList = albums.getData();
		String profilePictureAlbumId = null;

		for(Album album : albumList) {
			// Find the photo album
			if (album.getType().equalsIgnoreCase(ALBUM_TYPE_PROFILE)) {
				profilePictureAlbumId = album.getId();
			}
		}

		String fbPhotoRestEndPointPrefix = profilePictureAlbumId + FB_PHOTOS_END_POINT;

		// Get all photos of the photo album
		Connection<Photo> photos = facebookClient.fetchConnection(fbPhotoRestEndPointPrefix, Photo.class, Parameter.with(PARAMETER_KEY_FIELDS, PARAMATER_VALUE_IMAGES));
		List<Photo> photoList = photos.getData();
		List<String> photosUrl = new ArrayList<String>(photoList.size());

		for(Photo photo : photoList) {
			// Pick up largest size link for a photo
			photosUrl.add(photo.getImages().get(0).getSource());
		}

		return photosUrl;
	}
}
