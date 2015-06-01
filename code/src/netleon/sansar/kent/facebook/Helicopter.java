package netleon.sansar.kent.facebook;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

@SuppressWarnings("deprecation")
public class Helicopter {

	private static final String TAG = "MainFragment";
	private static String APP_ID = "779169015483457";

	private Facebook facebook = new Facebook(APP_ID);

	@SuppressWarnings("deprecation")
	AsyncFacebookRunner facebookRunner;

	public Helicopter() {

		facebookRunner = new AsyncFacebookRunner(facebook);
	}

	public void setFacebookRunner(AsyncFacebookRunner facebookRunner) {
		this.facebookRunner = facebookRunner;
	}

	public AsyncFacebookRunner getFacebookRunner() {
		return facebookRunner;
	}

}
