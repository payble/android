package netleon.sansar.kent.fragments;

import static netleon.sansar.kent.base.KentValues.myLocation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.base.DirectionResponse;
import netleon.sansar.kent.base.Restaurant;
import netleon.sansar.kent.base.Review;
import netleon.sansar.kent.network.GetData;
import netleon.sansar.kent.support.GetDirectionsAsyncTask;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class ReviewTestFragment extends Fragment implements DirectionResponse {

	private ProgressDialog dialog;
	String URL_Review;
	DateTime now;
	ArrayList<Review> reviews = new ArrayList<Review>();
	LinearLayout layoutDirection, layoutCall, layoutMenu, layoutMsg,
			layoutMore, layoutReview;
	public Polyline polyline;
	String TAG = "ReviewTestFragment";
	Restaurant restaurant;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		restaurant = getArguments().getParcelable("restinpeace");
		URL_Review = "http://netleondev.com/kentapi/restaurant/reviews/restaurant_id/"
				+ restaurant.getId();

		Log.e("", "url for review : " + URL_Review);

		now = new DateTime(Calendar.getInstance().getTimeInMillis());

		Perspective.map.animateCamera(CameraUpdateFactory.newLatLngZoom(
				restaurant.getLatLng(), 15));
		return inflater.inflate(R.layout.fragment_reviewlist, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		layoutDirection = (LinearLayout) view
				.findViewById(R.id.linReview_direction);
		layoutCall = (LinearLayout) view.findViewById(R.id.linReview_call);
		layoutMenu = (LinearLayout) view.findViewById(R.id.linReview_menu);
		layoutMsg = (LinearLayout) view.findViewById(R.id.linReview_bussiness);
		layoutMore = (LinearLayout) view.findViewById(R.id.linReview_info);
		layoutReview = (LinearLayout) view.findViewById(R.id.linReviewLayout);
		((TextView) view.findViewById(R.id.txt_call_number)).setText(restaurant
				.getPhone());

		if (!restaurant.getMenu().equalsIgnoreCase("")) {
			Log.e("", "VISIBLE");
			layoutMenu.setVisibility(View.VISIBLE);
		} else {
			Log.e("", "INVISIBLE");
			layoutMenu.setVisibility(View.GONE);
		}
		if (!restaurant.getMessage().equalsIgnoreCase("")) {
			Log.e("", "VISIBLE");
			layoutMsg.setVisibility(View.VISIBLE);
		} else {
			Log.e("", "INVISIBLE");
			layoutMsg.setVisibility(View.GONE);
		}
		if (!restaurant.getMore().equalsIgnoreCase("")) {
			Log.e("", "VISIBLE");
			layoutMore.setVisibility(View.VISIBLE);
		} else {
			Log.e("", "INVISIBLE");
			layoutMore.setVisibility(View.GONE);
		}

		int total_reviews = 0;
		String review = "";
		try {
			total_reviews = Integer.parseInt(restaurant.getReview_count());
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (total_reviews > 0) {
			Log.e("", "VISIBLE");
			layoutReview.setVisibility(View.VISIBLE);
		} else {
			Log.e("", "GONE");
			Toast.makeText(getActivity(), "No review available.",
					Toast.LENGTH_SHORT).show();
			layoutReview.setVisibility(View.GONE);
		}

		layoutDirection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDirection();
			}
		});
		layoutCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeCall();
			}
		});

		layoutMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String str = restaurant.getMenu();
				if (!str.contains("http://")) {
					str = "http://" + str;
				}
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(str));
				// startActivityForResult(browserIntent, 0);
				startActivity(browserIntent);
			}
		});

		layoutMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = restaurant.getMessage();
				if (!str.contains("http://")) {
					str = "http://" + str;
				}
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(str));
				startActivity(browserIntent);
			}
		});

		layoutMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMsg();
			}
		});

		new GetReviews().execute();
	}

	protected void showMsg() {

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setPositiveButton("OK", null).create();
		alertDialog.setTitle("Payable");
		alertDialog.setMessage(restaurant.getMore());
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.show();
	}

	private void addCustomViewsToScroll() {
		LinearLayout layout = (LinearLayout) getView().findViewById(
				R.id.linrootlist_review);
		Review review;
		Float temp = getResources().getDisplayMetrics().density;
		for (int i = 0; i < reviews.size(); i++) {
			View convertView = getActivity().getLayoutInflater().inflate(
					R.layout.custom_list_reviewlist, layout, false);
			review = reviews.get(i);

			TextView txt_review = (TextView) convertView
					.findViewById(R.id.txtcustomreviewlist_review);
			TextView txt_time = (TextView) convertView
					.findViewById(R.id.txtcustomreviewlist_time);

			ImageView upview = (ImageView) convertView
					.findViewById(R.id.imgcustomreviewlist_up);

			ImageView image = (ImageView) convertView
					.findViewById(R.id.imgcustomreviewlist);
			RatingBar ratingBar = (RatingBar) convertView
					.findViewById(R.id.ratingbar_default);

			Bitmap bit = Bitmap.createBitmap((int) (24 * temp),
					(int) (24 * temp), Config.ARGB_8888);
			Canvas canvas = new Canvas(bit);
			canvas.drawColor(Color.parseColor("#F5F5F5"));

			upview.setImageBitmap(bit);
			Paint paint = new Paint();
			paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			int val = (int) (12 * temp);
			canvas.drawCircle(val, val, val, paint);
			canvas.drawBitmap(bit, 0, 0, null);
			upview.setImageBitmap(bit);

			ratingBar.setRating(Float.parseFloat(review.getRating()));

			txt_review.setText(review.getComment());
			DateTime dategot = review.getDateTime();
			Period period = new Period(dategot, now);

			PeriodFormatter formatter;

			if (period.getYears() != 0) {
				formatter = new PeriodFormatterBuilder().appendYears()
						.appendSuffix(" years ago").printZeroNever()
						.toFormatter();
			} else if (period.getYears() == 0 && period.getMonths() != 0) {
				formatter = new PeriodFormatterBuilder().appendMonths()
						.appendSuffix(" months ago").printZeroNever()
						.toFormatter();
			} else if (period.getYears() == 0 && period.getMonths() == 0
					&& period.getWeeks() != 0) {
				formatter = new PeriodFormatterBuilder().appendWeeks()
						.appendSuffix(" weeks ago").printZeroNever()
						.toFormatter();
			}

			else if (period.getYears() == 0 && period.getMonths() == 0
					&& period.getWeeks() == 0 && period.getDays() != 0) {
				formatter = new PeriodFormatterBuilder().appendDays()
						.appendSuffix(" days ago").printZeroNever()
						.toFormatter();
			} else if (period.getYears() == 0 && period.getMonths() == 0
					&& period.getWeeks() == 0 && period.getDays() == 0
					&& period.getHours() != 0) {
				formatter = new PeriodFormatterBuilder().appendHours()
						.appendSuffix(" hours ago").printZeroNever()
						.toFormatter();
			} else if (period.getYears() == 0 && period.getMonths() == 0
					&& period.getWeeks() == 0 && period.getDays() == 0
					&& period.getHours() == 0 && period.getMinutes() != 0) {
				formatter = new PeriodFormatterBuilder().appendMinutes()
						.appendSuffix(" minutes ago").printZeroNever()
						.toFormatter();
			} else {
				formatter = new PeriodFormatterBuilder().appendSeconds()
						.appendSuffix(" seconds ago").printZeroNever()
						.toFormatter();
			}

			String elapsed = formatter.print(period);
			txt_time.setText(elapsed);
			layout.addView(convertView);
		}
	}

	protected void makeCall() {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + restaurant.getPhone()));
		getActivity().startActivity(intent);
	}

	protected void getDirection() {
		LocationManager locationManager = ((Perspective) getActivity()).locationManager;
		boolean is = ((Perspective) getActivity())
				.isGPS_Enabled(locationManager);
		if (is) {
			if (myLocation != null) {
				if (getActivity().getSupportFragmentManager()
						.findFragmentByTag("ReviewListFragment") != null) {
					removeReviewlist();
				}
				LatLng loc = myLocation;
				if (polyline == null) {
					findDirections(loc.latitude, loc.longitude,
							restaurant.getLatLng().latitude,
							restaurant.getLatLng().longitude, "driving");
				} else {
					polyline = null;
					findDirections(loc.latitude, loc.longitude,
							restaurant.getLatLng().latitude,
							restaurant.getLatLng().longitude, "driving");
				}
			} else {
				Toast.makeText(getActivity(), "GPS is searching location",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(), "Please turn on gps",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void removeReviewlist() {
		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"ReviewListFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.remove(getActivity().getSupportFragmentManager()
							.findFragmentByTag("ReviewListFragment")).commit();
		}
	}

	public void findDirections(double fromPositionDoubleLat,
			double fromPositionDoubleLong, double toPositionDoubleLat,
			double toPositionDoubleLong, String mode) {
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,
				String.valueOf(fromPositionDoubleLat));
		map1.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,
				String.valueOf(fromPositionDoubleLong));
		map1.put(GetDirectionsAsyncTask.DESTINATION_LAT,
				String.valueOf(toPositionDoubleLat));
		map1.put(GetDirectionsAsyncTask.DESTINATION_LONG,
				String.valueOf(toPositionDoubleLong));
		map1.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(
				getActivity());
		asyncTask.response = this;
		asyncTask.execute(map1);
	}

	public class GetReviews extends AsyncTask<String, Integer, Void> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Please wait...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			GetData data = new GetData();
			response = data.getDataFromServer(URL_Review);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (response != null) {
				try {
					JSONArray array = new JSONArray(response);

					for (int i = 0; i < array.length(); i++) {
						JSONObject c = array.getJSONObject(i);

						String id = c.getString("id");
						String rest_id = c.getString("restaurant_id");
						String rating = c.getString("rating");
						String comment = c.getString("comment");
						String time = c.getString("created_at");
						String user_id = c.getString("user_id");

						Review review = new Review(id, user_id, rest_id,
								rating, comment, time);
						reviews.add(review);
					}
				} catch (Exception e) {
					Log.e("", "cptured");
				}
				addCustomViewsToScroll();
			} else {
				Log.e("", "i in else");
			}
			dialog.dismiss();
		}
	}

	private void showMessage(String str) {
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (polyline != null) {
			polyline.remove();
		}
	}

	@Override
	public void processFinish(Polyline polyline) {
		// TODO Auto-generated method stub
		this.polyline = polyline;
	}

}
