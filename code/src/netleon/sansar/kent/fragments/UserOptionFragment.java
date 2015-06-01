package netleon.sansar.kent.fragments;

import static netleon.sansar.kent.base.KentValues.isLoading;
import static netleon.sansar.kent.facebook.FB_Session.fbUserId;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import netleon.sansar.kent.Home;
import netleon.sansar.kent.LogIn;
import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.facebook.FB_Session;
import netleon.sansar.kent.facebook.Helicopter;
import netleon.sansar.kent.network.GetData;
import netleon.sansar.kent.support.MemoryCache;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;

public class UserOptionFragment extends Fragment {

	Facebook facebook;
	MemoryCache memoryCache = new MemoryCache();
	String tag_json_obj = "json_obj_req";
	ImageView img_profile;
	TextView txtduck_username;

	SessionState state;
	Application mApp;
	UiLifecycleHelper helper;
	AsyncFacebookRunner facebookRunner;
	Helicopter helicopter;
	String user_full_name, user_id;

	SharedPreferences preferences;
	TextView txtduck_cancel;
	String url_counter_update;

	ProgressDialog proDialog;
	String counter_number, photo_name;
	RelativeLayout slideLayout;
	TextView txtduck_recnumber;

	Bitmap bitmap;
	String downloadImage_Url;

	int proImgWidth, proImgHeight;

	String fb_id;
	Rect cancelRect;

	private GestureDetector gestureDetector_1;
	View.OnTouchListener gestureListener_1;

	FootFragment footFragment;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		footFragment = ((Perspective) getActivity()).footFragment;
		helicopter = new Helicopter();
		facebookRunner = helicopter.getFacebookRunner();
		float factor = getResources().getDisplayMetrics().density;
		proImgWidth = (int) (150 * factor);
		proImgHeight = (int) (150 * factor);
		return inflater.inflate(R.layout.useroptn_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		TextView txtduck_find_rest, txtduck_payment, txtduck_rec, txtduck_support, txtduck_logout, txtduck_reservation;
		TextView txt_view_acc;
		fb_id = fbUserId;
		preferences = getActivity().getSharedPreferences("NESSIE",
				Context.MODE_PRIVATE);

		user_full_name = preferences.getString("USER_FULL_NAME", "Default");
		user_id = preferences.getString("ID", "01");
		url_counter_update = "http://netleondev.com/kentapi/user/getUserInfo/userid/"
				+ user_id;

		Log.e("", "counter update url:" + url_counter_update);

		txtduck_find_rest = (TextView) view
				.findViewById(R.id.txtduck_find_rest);
		txtduck_payment = (TextView) view.findViewById(R.id.txtduck_payment);
		txtduck_rec = (TextView) view.findViewById(R.id.txtduck_rec);
		txtduck_recnumber = (TextView) view
				.findViewById(R.id.txtduck_recnumber);
		txtduck_support = (TextView) view.findViewById(R.id.txtduck_support);
		txtduck_cancel = (TextView) view.findViewById(R.id.txtduck_cancel);
		slideLayout = (RelativeLayout) view.findViewById(R.id.reluseropt_1);
		txtduck_logout = (TextView) view.findViewById(R.id.txtduck_logout);
		txtduck_username = (TextView) view.findViewById(R.id.txtduck_user);
		img_profile = (ImageView) view.findViewById(R.id.img_profile);
		txtduck_reservation = (TextView) view
				.findViewById(R.id.txtduck_reservation);

		txtduck_find_rest.setTypeface(face1);
		txtduck_payment.setTypeface(face1);
		txtduck_rec.setTypeface(face1);
		txtduck_recnumber.setTypeface(face1);
		txtduck_support.setTypeface(face1);
		txtduck_cancel.setTypeface(face1);
		txtduck_logout.setTypeface(face1);
		txtduck_username.setTypeface(face1);
		txtduck_reservation.setTypeface(face1);

		((TextView) view.findViewById(R.id.txtduck_pay_kent))
				.setTypeface(face1);

		txt_view_acc = (TextView) view.findViewById(R.id.txtduck_view_account);
		txt_view_acc.setTypeface(face1);

		txtduck_username.setText(user_full_name.trim());

		txtduck_find_rest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					getActivity()
							.getSupportFragmentManager()
							.popBackStack(
									null,
									getActivity().getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
					((Perspective) getActivity()).refreshData();
				}
			}
		});

		txt_view_acc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					addProfileFragment();
				}
			}
		});

		txtduck_rec.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					isLoading = true;
					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.container_top, new Recieptlistfrag(),
									"RecieptlistFrag")
							.addToBackStack("RecieptlistFrag").commit();
				}
			}
		});

		txtduck_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								logout();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage("Do you want to logout?")
							.setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();

				}
			}
		});

		txtduck_payment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					isLoading = true;
					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.container_top, new PaymentlistFrag(),
									"PaymentlistFrag")
							.addToBackStack("PaymentlistFrag").commit();
				}
			}
		});

		txtduck_reservation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					isLoading = true;
					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.container_top, new ReveserListFragment(),
									"ReveserListFragment")
							.addToBackStack("ReveserListFragment").commit();
				}
			}
		});

		new GetReceiptCounter().execute();
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {

		if (state.isOpened()) {
			Log.i("", "Logged in...");
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user,
						com.facebook.Response response) {
					if (user != null) {

					}
				}
			}).executeAsync();
		} else if (state.isClosed()) {
			Log.i("", "Logged out...");
			getActivity().finish();
		}
	}

	@Override
	public void onDestroyView() {

		try {
			getActivity().getSupportFragmentManager().beginTransaction()
					.show(footFragment).commit();

			footFragment.setDefaultView();
		} catch (Exception e) {
		}
		super.onDestroyView();
	}

	class GetReceiptCounter extends AsyncTask<String, String, String> {

		String data;
		String response = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			proDialog = new ProgressDialog(getActivity());
			proDialog.setMessage("Please wait...");
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
		}

		protected String doInBackground(String... args) {
			GetData data = new GetData();
			response = data.getDataFromServer(url_counter_update);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			proDialog.dismiss();
			if (response != null) {
				try {
					JSONArray jsonArray = new JSONArray(response);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						counter_number = object.getString("total_receipts");
						photo_name = object.getString("photo");
						Log.e("", "total reciptes is : " + counter_number);
						Log.e("", "photo name is : " + photo_name);
					}
					if (counter_number != null) {
						txtduck_recnumber.setText(counter_number);
					} else {
						txtduck_recnumber.setText("0");
					}

					String imgname = preferences.getString("imgName", "");

					if (photo_name.equals(imgname)) {
						try {
							img_profile.setImageBitmap(decodeBase64(preferences
									.getString("imgdata", "")));
						} catch (Exception e) {
							// TODO: handle exception
							downloadImage(photo_name);
						}

					} else {
						downloadImage(photo_name);
					}

				} catch (Exception e) {
				}
			}

			cancelRect = new Rect();
			int[] viewLocation = new int[2];
			txtduck_cancel.getLocationInWindow(viewLocation);
			cancelRect.bottom = viewLocation[0] + txtduck_cancel.getBottom();
			cancelRect.left = txtduck_cancel.getLeft();
			cancelRect.right = txtduck_cancel.getRight();
			cancelRect.top = viewLocation[0] + txtduck_cancel.getTop();
			gestureDetector_1 = new GestureDetector(getActivity(),
					new CancelListener());
			gestureListener_1 = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					return gestureDetector_1.onTouchEvent(event);
				}
			};
			slideLayout.setOnTouchListener(gestureListener_1);
			KentValues.isLoading = false;
		}
	}

	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Bitmap doInBackground(String... args) {
			try {

				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						args[0]).getContent());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] b = baos.toByteArray();
				bitmap = decodeSampledBitmapFromResource(b, proImgWidth,
						proImgHeight);
				baos.close();
				baos = null;
				ByteArrayOutputStream caos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, caos);
				b = caos.toByteArray();
				caos.close();
				caos = null;
				String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

				Editor edit = preferences.edit();
				edit.putString("imgdata", encodedImage);
				edit.putString("imgName", photo_name);
				edit.commit();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			if (image != null) {

				img_profile.setImageBitmap(image);
				bitmap.recycle();

			} else {
				proDialog.dismiss();

			}
		}
	}

	public Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public void downloadImage(String imgName) {
		downloadImage_Url = imgName;
		Log.e("", "download image url:" + downloadImage_Url);
		new LoadImage().execute(downloadImage_Url);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] data,
			int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public void addProfileFragment() {

		isLoading = true;

		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.hide(getActivity().getSupportFragmentManager()
						.findFragmentByTag("UserOptionFragment")).commit();
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container_profile, new ProfileFragment(),
						"ProfileFragment").addToBackStack("ProfileFragment")
				.commit();
	}

	public void logout() {

		KentValues.catgorizedRestos.list.clear();
		Perspective.map.clear();
		KentValues.markerMap.clear();
		KentValues.restaurants.clear();
		KentValues.KEYS.clear();
		footFragment.clearFooterData();
		try {
			if (FB_Session.currentSession.isOpened()) {
				Log.d("", "holaaaa we lose");
				FB_Session.currentSession.closeAndClearTokenInformation();
				getActivity()
						.getSharedPreferences("NESSIE",
								getActivity().MODE_PRIVATE).edit().clear()
						.commit();
				getActivity().finish();

			} else {
				Log.d("", "holaaaa we win");
				getActivity()
						.getSharedPreferences("NESSIE",
								getActivity().MODE_PRIVATE).edit()
						.putBoolean("isLogged", false).commit();
				getActivity()
						.getSharedPreferences("NESSIE",
								getActivity().MODE_PRIVATE).edit().clear()
						.commit();
				startActivity(new Intent(getActivity(), LogIn.class));
				getActivity().finish();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		getActivity()
				.getSharedPreferences("NESSIE", getActivity().MODE_PRIVATE)
				.edit().putBoolean("isLogged", false).commit();
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.remove(getActivity().getSupportFragmentManager()
						.findFragmentByTag("FootFragment")).commit();
		getActivity()
				.getSharedPreferences("NESSIE", getActivity().MODE_PRIVATE)
				.edit().clear().commit();
		startActivity(new Intent(getActivity(), Home.class));
		getActivity().finish();
	}

	public void closeMe() {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_out_top);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isLoading = false;
				getActivity().getSupportFragmentManager().beginTransaction()
						.show(footFragment).commit();
				getActivity().getSupportFragmentManager()
						.popBackStackImmediate();
			}
		});
		((Perspective) getActivity()).container_top.startAnimation(anim);
	}

	class CancelListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";
		private int swipe_Min_Distance = 20;
		private int swipe_Max_Distance = 350;
		private int swipe_Min_Velocity = 15;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			Log.e(DEBUG_TAG, "onFling");
			boolean result = false;

			if (!isLoading) {
				isLoading = true;
				final float xDistance = Math.abs(e1.getX() - e2.getX());
				final float yDistance = Math.abs(e1.getY() - e2.getY());

				if (xDistance > this.swipe_Max_Distance
						|| yDistance > this.swipe_Max_Distance)
					return false;

				velocityX = Math.abs(velocityX);
				velocityY = Math.abs(velocityY);

				if (velocityY > this.swipe_Min_Velocity
						&& yDistance > this.swipe_Min_Distance) {
					if (e1.getY() > e2.getY()) {
						closeMe();
					}
					result = true;
				}
			}

			return result;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.e(DEBUG_TAG, "onSingleTapConfirmed");
			int x = (int) e.getX();
			int y = (int) e.getY();
			if (cancelRect.contains(x, y)) {
				if (!isLoading) {
					isLoading = true;
					closeMe();
				}
			}
			return super.onSingleTapConfirmed(e);
		}
	}
}
