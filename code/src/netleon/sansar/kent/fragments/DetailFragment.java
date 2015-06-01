package netleon.sansar.kent.fragments;

import static netleon.sansar.kent.base.KentValues.myLocation;

import java.util.HashMap;
import java.util.Map;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.base.DirectionResponse;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Restaurant;
import netleon.sansar.kent.support.GetDirectionsAsyncTask;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class DetailFragment extends Fragment implements DirectionResponse {

	// ReserveTableFragment reserveTableFragment;
	public Polyline polyline;
	TextView txt_reviews;
	private GestureDetector gestureDetector11;
	View.OnTouchListener gestureListener11;
	Restaurant restaurant;
	LatLng latLng;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		// reserveTableFragment = new ReserveTableFragment();

		restaurant = getArguments().getParcelable("restaurant");
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.hide(getActivity().getSupportFragmentManager()
						.findFragmentByTag("FootFragment")).commit();
		getActivity().getSupportFragmentManager().beginTransaction()
				.hide(((Perspective) getActivity()).footListFragment).commit();
		return inflater.inflate(R.layout.detailfragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Perspective.txt_back.setVisibility(View.VISIBLE);
		if (Perspective.txt_back.getVisibility() == View.VISIBLE) {
			Perspective.imgpers_gps.setVisibility(View.INVISIBLE);
		} else {
			Perspective.imgpers_gps.setVisibility(View.VISIBLE);
		}

		latLng = new LatLng(Double.parseDouble(restaurant.getLatitude()),
				Double.parseDouble(restaurant.getLongitude()));
		Perspective.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
				15));

		((TextView) view.findViewById(R.id.txtdetail_name)).setText(restaurant
				.getName());
		((TextView) view.findViewById(R.id.txtdetails_phone_no))
				.setText(restaurant.getPhone());
		((TextView) view.findViewById(R.id.txtdetails_time)).setText(restaurant
				.getWaitTime());
		txt_reviews = (TextView) view.findViewById(R.id.txtdetails_reviews);

		int total_reviews = 0;
		String review = "";
		try {
			review = restaurant.getReview_count();
			total_reviews = Integer.parseInt(review);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (total_reviews == 1) {
			review = review.concat(" review");
		} else {
			review = review.concat(" reviews");
		}

		txt_reviews.setText(review);

		RatingBar bar = (RatingBar) view.findViewById(R.id.ratingdetails);

		Float rating = Float.parseFloat(restaurant.getRating());
		rating = (float) ((rating * 5) / 100);
		bar.setRating(rating);

		if (!(restaurant.getPhone().length() > 0)) {
			((ImageView) view.findViewById(R.id.call_mg))
					.setVisibility(View.INVISIBLE);
		}

		((TextView) view.findViewById(R.id.txtdetails_get_drection))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						LocationManager locationManager = ((Perspective) getActivity()).locationManager;
						boolean is = ((Perspective) getActivity())
								.isGPS_Enabled(locationManager);
						if (is) {
							if (myLocation != null) {
								if (getActivity()
										.getSupportFragmentManager()
										.findFragmentByTag("ReviewListFragment") != null) {
									removeReviewlist();
								}
								LatLng loc = myLocation;
								if (polyline == null) {
									findDirections(loc.latitude, loc.longitude,
											latLng.latitude, latLng.longitude,
											"driving");
								} else {
									polyline = null;
									findDirections(loc.latitude, loc.longitude,
											latLng.latitude, latLng.longitude,
											"driving");
								}
							} else {
								Toast.makeText(getActivity(),
										"GPS is searching location",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getActivity(), "Please turn on gps",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		((TextView) view.findViewById(R.id.txtdetails_reservetable))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Bundle bundle = new Bundle();
						bundle.putString("REST_ID", restaurant.getId());
						bundle.putInt("max_party_size",
								restaurant.getMax_party());
						bundle.putInt("min_party_size",
								restaurant.getMin_party());
						Fragment fragment = new ReserveTableFragment();

						fragment.setArguments(bundle);
						getActivity()
								.getSupportFragmentManager()
								.beginTransaction()
								.add(R.id.container_top, fragment,
										"ReserveTableFragment")
								.hide(getActivity().getSupportFragmentManager()
										.findFragmentByTag("DetailFragment"))
								.addToBackStack("ReserveTableFragment")
								.commit();
					}
				});

		((RelativeLayout) view.findViewById(R.id.make_call))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + restaurant.getPhone()));
						getActivity().startActivity(intent);
					}
				});
		gestureDetector11 = new GestureDetector(getActivity(),
				new RevListener());
		gestureListener11 = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector11.onTouchEvent(event);
			}
		};

		((LinearLayout) view.findViewById(R.id.grand))
				.setOnTouchListener(gestureListener11);
		KentValues.isLoading = false;
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

	public void removeReviewlist() {
		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"ReviewListFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.remove(getActivity().getSupportFragmentManager()
							.findFragmentByTag("ReviewListFragment")).commit();
		}
	}

	private void addReviewList() {

		// if (total_reviews > 0) {
		//
		// }

		Bundle bundle = new Bundle();
		bundle.putParcelable("restinpeace", restaurant);
		Fragment fragment = new ReviewTestFragment();
		fragment.setArguments(bundle);
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_bottom,
						R.anim.slide_topbottom, R.anim.slide_in_bottom,
						R.anim.slide_topbottom)
				.add(R.id.container_reviewlist, fragment, "ReviewListFragment")
				.addToBackStack("ReviewListFragment").commit();

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.show(getActivity().getSupportFragmentManager()
							.findFragmentByTag("FootFragment")).commit();

			((Perspective) getActivity()).footFragment.setDefaultView();
			Perspective.txt_back.setVisibility(View.GONE);
			Perspective.imgpers_gps.setVisibility(View.VISIBLE);
			Perspective.map.animateCamera(CameraUpdateFactory.newLatLngZoom(
					latLng, 1));

			if (Perspective.previousMarker != null) {
				Perspective.previousMarker.setIcon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_blue));
				Perspective.previousMarker = null;
				Log.e("", "previousMarker set  to null");
			}

			if (polyline != null) {
				polyline.remove();
			}

			if (getActivity().getSupportFragmentManager().findFragmentByTag(
					"ReviewListFragment") != null) {
				Fragment fragment = getActivity().getSupportFragmentManager()
						.findFragmentByTag("ReviewListFragment");
				getActivity().getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
			}

		} catch (Exception e) {
		}
	}

	@Override
	public void processFinish(Polyline polyline) {
		this.polyline = polyline;
	}

	class RevListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";
		private int swipe_Min_Distance = 20;
		private int swipe_Max_Distance = 350;
		private int swipe_Min_Velocity = 15;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			final float xDistance = Math.abs(e1.getX() - e2.getX());
			final float yDistance = Math.abs(e1.getY() - e2.getY());

			if (xDistance > this.swipe_Max_Distance
					|| yDistance > this.swipe_Max_Distance)
				return false;

			velocityX = Math.abs(velocityX);
			velocityY = Math.abs(velocityY);
			boolean result = false;

			if (velocityY > this.swipe_Min_Velocity
					&& yDistance > this.swipe_Min_Distance) {
				if (e1.getY() > e2.getY()) {
					addReviewList();
				} else {
					removeReviewlist();
				}
				result = true;
			}
			return result;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			addReviewList();
			return super.onSingleTapConfirmed(e);
		}
	}
}