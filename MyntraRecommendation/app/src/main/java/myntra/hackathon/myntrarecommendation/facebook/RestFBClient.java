package myntra.hackathon.myntrarecommendation.facebook;

import java.util.List;

import com.restfb.types.User;

public interface RestFBClient {
	/**
	 * User has full name and id set
	 * @return
	 */
	public List<User> getFriendList();
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public List<String> getProfilePhotosUrl(User user);
}
