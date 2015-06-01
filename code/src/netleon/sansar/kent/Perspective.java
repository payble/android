package netleon.sansar.kent;

import static netleon.sansar.kent.base.KentValues.KEYS;
import static netleon.sansar.kent.base.KentValues.catgorizedRestos;
import static netleon.sansar.kent.base.KentValues.isLoading;
import static netleon.sansar.kent.base.KentValues.markerMap;
import static netleon.sansar.kent.base.KentValues.myLocation;
import static netleon.sansar.kent.base.KentValues.price_list;
import static netleon.sansar.kent.base.KentValues.restaurants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Restaurant;
import netleon.sansar.kent.base.SearchKey;
import netleon.sansar.kent.base.SearchList;
import netleon.sansar.kent.base.SearchVal;
import netleon.sansar.kent.fragments.DetailFragment;
import netleon.sansar.kent.fragments.FootFragment;
import netleon.sansar.kent.fragments.FootListFragment;
import netleon.sansar.kent.fragments.SearchFragment;
import netleon.sansar.kent.fragments.UserOptionFragment;
import netleon.sansar.kent.network.GetData;
import netleon.sansar.kent.network.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Perspective extends FragmentActivity implements LocationListener,
		OnMarkerClickListener {

	public final Context context = Perspective.this;
	public static GoogleMap map;

	boolean isVisible = false;
	boolean checkGPS = false;
	boolean isFirstBackPressed = false;

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	public Boolean hasFullyLoaded = false;
	AnimationListener animListen;

	public static TextView txt_back;
	String[] footerParameters;
	LinearLayout layFather;

	public LocationManager locationManager;
	public static SearchList searchList = new SearchList();;
	public static Marker previousMarker = null;

	FrameLayout container_footlist, container_foot;
	public FrameLayout container_top;
	FrameLayout container_profile;
	FrameLayout container_details;
	FrameLayout container_reviewlist;
	FrameLayout container_search;

	LinearLayout gabber;
	public LinearLayout handle;

	public String searchTag = "price";
	Boolean isError = false;

	String URL_Search = "http://netleondev.com/kentapi/restaurant/category";
	String URL_AllRestoData = "http://netleondev.com/kentapi/restaurant/all";

	ProgressDialog proDialog, SearchDialog;
	Typeface face1;

	public static ImageView imgpers_gps;
	ImageView userOptn;

	public FootFragment footFragment;
	public FootListFragment footListFragment;
	public SearchFragment searchFragment;
	public FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.active);

		if (savedInstanceState != null) {
			restaurants = savedInstanceState.getParcelableArrayList("sss");
		}

		map = null;
		setUpMapIfNeeded();

		if (map == null) {
			Toast.makeText(context, "Google play service is out of date",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			contentsInitialization();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("", "onResume");
		isLoading = true;
		if (!(restaurants.size() > 0)) {
			new LoadAllData().execute();
		}
		if (checkGPS) {
			if (myLocation == null && locationManager != null) {
				setLocationData();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("sss",
				(ArrayList<? extends Parcelable>) restaurants);
		super.onSaveInstanceState(outState);
	}

	public void drawer() {

		Log.e("", "isLoading :" + isLoading);

		if (fm.findFragmentByTag("FootFragment") != null && !isLoading) {
			txt_back.setVisibility(View.GONE);
			imgpers_gps.setVisibility(View.VISIBLE);
			if (fm.findFragmentByTag("UserOptionFragment") != null) {
				Fragment fragment = fm.findFragmentByTag("UserOptionFragment");
				if (fragment.isHidden()) {
					fm.beginTransaction().show(fragment).commit();
				}
			} else {
				if (fm.findFragmentByTag("DetailFragment") != null) {
					fm.popBackStackImmediate("DetailFragment",
							fm.POP_BACK_STACK_INCLUSIVE);
					footFragment.setDefaultView();
				}

				if (fm.findFragmentByTag("ReviewListFragment") != null) {
					fm.popBackStackImmediate("ReviewListFragment",
							fm.POP_BACK_STACK_INCLUSIVE);
				}

				if (fm.findFragmentByTag("FootListFragment") != null) {

					fm.beginTransaction()
							.hide(fm.findFragmentByTag("FootListFragment"))
							.commit();
				}

				isLoading = true;

				Animation anim = AnimationUtils.loadAnimation(context,
						R.anim.slide_in_top);
				Fragment fragment = new UserOptionFragment();
				fm.beginTransaction()
						.add(R.id.container_top, fragment, "UserOptionFragment")
						.addToBackStack("UserOptionFragment").commit();
				((FrameLayout) findViewById(R.id.container_top))
						.startAnimation(anim);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if ((fm.findFragmentByTag("UserOptionFragment") != null)
								&& (fm.findFragmentByTag("FootFragment")
										.isVisible())) {

							fm.beginTransaction()
									.hide(fm.findFragmentByTag("FootFragment"))
									.commit();
						}
					}
				});
			}

		} else {
			Toast.makeText(context, "check network connection",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void showFragment(Fragment fragment) {
		fm.beginTransaction().show(fragment).commit();
	}

	public void hideFragment(Fragment fragment) {
		fm.beginTransaction().hide(fragment).commit();
	}

	public void addFragment(int id, Fragment fragment, String tag) {
		fm.beginTransaction().add(id, fragment, tag).commit();
	}

	public void removeFragment(Fragment fragment) {
		fm.beginTransaction().remove(fragment).commit();
	}

	public void removeFragmentByTag(String tag) {
		Fragment fragment = fm.findFragmentByTag(tag);
		if (fragment != null) {
			fm.beginTransaction().remove(fragment).commit();
		}
	}

	public void replaceFragment(int id, Fragment fragment, String tag) {
		fm.beginTransaction().replace(id, fragment, tag).commit();
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.e("", "myLLocation got latitude : " + location.getLatitude()
				+ " and longotude : " + location.getLongitude());
		LatLng tryf = new LatLng(location.getLatitude(),
				location.getLongitude());

		myLocation = tryf;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

		Log.e("", "provider enabled");
		Toast.makeText(context, "GPS on", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.e("", "provider disabled");
		Toast.makeText(context, "GPS off", Toast.LENGTH_SHORT).show();
	}

	private void setUpMapIfNeeded() {

		if (map == null) {
			int status;
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			status = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getBaseContext());

			if (status != ConnectionResult.SUCCESS) {
				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
						this, requestCode);
				dialog.show();
			} else {

				map = fm.getMap();
				map.setMyLocationEnabled(true);
				setMyLocationbuttonPosition(fm);
				map.setOnMarkerClickListener(this);
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 20000, 10, this);
				setLocationData();
			}
		}
	}

	private void setLocationData() {
		if (isGPS_Enabled(locationManager)) {
			myLocation = getMyLocation(locationManager);
			showMe();
		} else {
			showGPSDisabledAlertToUser();
		}
	}

	private LatLng getMyLocation(LocationManager locationManager) {
		Criteria criteria = new Criteria();
		LatLng latLng = null;
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			onLocationChanged(location);
			latLng = new LatLng(location.getLatitude(), location.getLongitude());
		}
		return latLng;
	}

	public void setAllMarkers() {
		try {
			map.clear();
			previousMarker = null;
			for (int i = 0; i < restaurants.size(); i++) {

				Restaurant restaurant = restaurants.get(i);
				String t = String.valueOf(restaurants.get(i).getWaitTime());
				int tt = 0;
				try {
					tt = Integer.parseInt(t);
				} catch (Exception e) {
				}

				if (tt != 1) {
					t = t.concat(" minutes wait");
					Log.e("", "not 1 t : " + t);
				} else {
					t = t.concat(" minute wait");
					Log.e("", "is 1 t : " + t);
				}

				MarkerOptions options = new MarkerOptions()
						.position(restaurant.getLatLng())
						.title(t)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_marker_blue));

				Marker marker = map.addMarker(options);
				markerMap.put(marker, restaurants.get(i));

			}
		} catch (Exception e) {

		}
	}

	public boolean onMarkerClick(Marker mark) {
		if (!isLoading) {
			isLoading = true;
			if (previousMarker == null) {
				if (SearchFragment.isSearch) {
					publishMarker(mark, SearchFragment.filteredMarkers);
				} else {
					publishMarker(mark, markerMap);
				}
			} else {
				if (!previousMarker.equals(mark)) {
					if (Perspective.previousMarker != null) {
						Perspective.previousMarker
								.setIcon(BitmapDescriptorFactory
										.fromResource(R.drawable.ic_marker_blue));
					}
					if (SearchFragment.isSearch) {
						publishMarker(mark, SearchFragment.filteredMarkers);
					} else {
						publishMarker(mark, markerMap);
					}
				}
			}
			isLoading = false;
		}
		return true;
	}

	private void publishMarker(Marker mark, Map<Marker, Restaurant> markerMaps) {

		mark.setIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.ic_marker_grey));

		mark.showInfoWindow();
		Bundle bundle = new Bundle();
		Restaurant restaurant = markerMaps.get(mark);
		bundle.putParcelable("restaurant", restaurant);

		if (fm.findFragmentByTag("DetailFragment") != null) {
			fm.popBackStackImmediate();
			Fragment fragment = new DetailFragment();
			fragment.setArguments(bundle);

			fm.beginTransaction().hide(fm.findFragmentByTag("FootFragment"))
					.add(R.id.container_details, fragment, "DetailFragment")
					.addToBackStack("DetailFragment").commit();
		} else {
			Fragment fragment = new DetailFragment();
			fragment.setArguments(bundle);
			fm.beginTransaction().hide(fm.findFragmentByTag("FootFragment"))
					.add(R.id.container_details, fragment, "DetailFragment")
					.addToBackStack("DetailFragment").commit();
		}
		previousMarker = mark;
	}

	public String getDataFromWeb(String url) {
		JSONParser parser = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		return parser.makeHttpRequest(url, "GET", params);
	}

	public void getAllRestoData(String result) {

		if (result != null) {
			try {

				JSONArray array = new JSONArray(result);
				for (int i = 0; i < array.length(); i++) {
					System.out.println(i + " : " + array.getString(i));
				}

				for (int i = 0; i < array.length(); i++) {

					JSONObject c = array.getJSONObject(i);

					String id = c.getString("id");
					String name = c.getString("name");
					String latitude = c.getString("latitude");
					String longitude = c.getString("longitude");
					String phone = c.getString("phone_number");
					String review_count = c.getString("review_count");
					String waitTime = c.getString("wait_time");
					String rating = c.getString("rating");

					String state, address, zip_code, keywords, menu, message, more;

					int max_party = 0, min_party = 0;
					state = c.getString("state");
					address = c.getString("address");
					zip_code = c.getString("zip_code");
					keywords = c.getString("search_keywords");
					menu = c.getString("browse_the_menu");
					message = c.getString("message_the_business");
					more = c.getString("more_info");

					try {
						max_party = Integer.parseInt(c
								.getString("max_party_size"));
						min_party = Integer.parseInt(c
								.getString("min_party_size"));

					} catch (Exception e) {
						// TODO: handle exception
					}

					JSONArray jsonArray = c.getJSONArray("categories");
					int[] categoryList = new int[jsonArray.length()];

					for (int j = 0; j < jsonArray.length(); j++) {
						categoryList[j] = Integer.parseInt(jsonArray
								.getString(j));
					}

					Restaurant restaurant = new Restaurant(id, name, latitude,
							longitude, waitTime, categoryList, phone,
							review_count, rating, state, address, zip_code,
							keywords, max_party, min_party, menu, message, more);
					restaurants.add(restaurant);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								context,
								"Error retriving data.Please check network connection.",
								Toast.LENGTH_LONG).show();
					}
				};
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the server");
		}

	}

	class LoadAllData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.e("", "load onPreExecute");
			isError = false;
			TextView textView = new TextView(context);
			textView.setText(getResources().getString(R.string.loaddata));
			textView.setTypeface(face1);

			String string = textView.getText().toString();

			proDialog = new ProgressDialog(Perspective.this);
			proDialog.setMessage(string);
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
		}

		protected String doInBackground(String... args) {

			GetData data = new GetData();
			getAllRestoData(data.getDataFromServer(URL_AllRestoData));
			Log.e("", "load doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("", "load onPostExecute");
			new LoadSearchData().execute();
		}
	}

	class LoadSearchData extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			GetData data = new GetData();
			getSearchData(data.getDataFromServer(URL_Search));
			Log.e("", "search doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!isError) {
				Log.e("", "search onPostExecute");
				footerParameters = getFooterParameters();

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (footerParameters.length > 0) {
							addBasicFragment(footerParameters);
							setAllMarkers();
							getScreenDimensions();
							isLoading = false;
						} else {
							showMessage("Invalid data");
						}
					}
				}, 200);

			} else {
				showMessage("Please check network connection");
			}
			proDialog.dismiss();
		}

		private String[] getFooterParameters() {

			String[] footerParameters = null;

			SearchKey searchKey = null;
			for (int i = 0; i < KEYS.size(); i++) {
				if (KEYS.get(i).getName().equalsIgnoreCase(searchTag)) {
					searchKey = KEYS.get(i);
				}
			}

			price_list = searchList.getSearchList().get(searchKey);
			Log.e("", "price_list size : " + price_list.size());
			try {

				footerParameters = new String[price_list.size()];
				for (int i = 0; i < price_list.size(); i++) {
					footerParameters[i] = price_list.get(i).getName();
					Log.e("", "searchLis22t");
				}

				for (int i = 0; i < restaurants.size(); i++) {

					Restaurant restaurant = restaurants.get(i);
					int[] restCatList = restaurant.getCategoryList();

					for (int j = 0; j < price_list.size(); j++) {

						int temp = price_list.get(j).getId();

						for (int k = 0; k < restCatList.length; k++) {
							if (temp == restCatList[k]) {
								catgorizedRestos
										.addRestosToCat(price_list.get(j)
												.getName(), restaurant);
							}
						}
					}
				}
			} catch (Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(context,
								"Please check network connection",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			return footerParameters;
		}
	}

	private void addBasicFragment(final String[] footerParameters) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (!(restaurants.size() > 0)) {
					Toast.makeText(context, "No restaurants Available",
							Toast.LENGTH_LONG).show();
				} else {
					addFootFragment(footerParameters);
					addSearchFragment();
					addFootlistFragment();
				}
			}
		});
	}

	public void addFootFragment(String[] footerParameters) {
		footFragment = new FootFragment();
		addFragment(R.id.container_foot, footFragment, "FootFragment");
		footFragment.setFooterParameters(footerParameters);
	}

	public void addSearchFragment() {
		searchFragment = new SearchFragment();
		addFragment(R.id.container_search, searchFragment, "SearchFragment");
	}

	public void getSearchData(String result) {

		if (result != null) {
			try {
				JSONArray jObj = new JSONArray(result);

				for (int i = 0; i < jObj.length(); i++) {
					if (i == 0) {
						JSONArray jArray = jObj.getJSONArray(i);
						for (int j = 0; j < jArray.length(); j++) {
							JSONObject jObject = new JSONObject(
									jArray.getString(j));
							int id = Integer.parseInt(jObject.getString("id"));
							String name = jObject.getString("name");
							KEYS.add(new SearchKey(name, id));
						}
					} else {

						JSONArray jArray = jObj.getJSONArray(i);
						for (int j = 0; j < jArray.length(); j++) {

							JSONObject jObject = new JSONObject(
									jArray.getString(j));
							int id = Integer.parseInt(jObject.getString("id"));
							String name = jObject.getString("name");
							int parent_id = Integer.parseInt(jObject
									.getString("parent_id"));

							SearchKey keya = null;
							for (int a = 0; a < KEYS.size(); a++) {
								if (KEYS.get(a).getId() == parent_id) {
									keya = KEYS.get(a);
								}
							}
							searchList.addchakra(keya, new SearchVal(parent_id,
									name, id));
						}
					}
				}

				for (int i = 0; i < KEYS.size(); i++) {
					Log.e("", "Key " + i + " : " + KEYS.get(i).getName());
				}
			} catch (JSONException e) {
				e.printStackTrace();
				isError = true;
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}
	}

	@Override
	public void onBackPressed() {

		if (!isError) {
			Log.e("", "onBackPress 1");

			if (fm.findFragmentByTag("ReviewListFragment") != null) {
				Log.e("", "onBackPress ReviewListFragment");
				fm.beginTransaction()
						.remove(fm.findFragmentByTag("ReviewListFragment"))
						.commit();
				fm.popBackStackImmediate("ReviewListFragment",
						fm.POP_BACK_STACK_INCLUSIVE);
			} else if (footListFragment.isVisible()) {
				Log.e("", "onBackPress FootListFragment");
				fm.beginTransaction().hide(footListFragment).commit();
				footFragment.setDefaultView();
				txt_back.setVisibility(View.GONE);
				imgpers_gps.setVisibility(View.VISIBLE);
			} else if (fm.findFragmentByTag("DetailFragment") != null) {
				Log.e("", "onBackPressed in DetailFragment ");
				fm.popBackStackImmediate("DetailFragment",
						fm.POP_BACK_STACK_INCLUSIVE);
				footFragment.setDefaultView();
				txt_back.setVisibility(View.GONE);
				imgpers_gps.setVisibility(View.VISIBLE);
				Log.e("", "onBackPressed in DetailFragment ");
			} else if (fm.findFragmentByTag("ProfileFragment") != null) {
				Log.e("", "onBackPressed in ProfileFragment ");
				fm.popBackStackImmediate();
				footFragment.setDefaultView();
				txt_back.setVisibility(View.GONE);
				imgpers_gps.setVisibility(View.VISIBLE);
				Log.e("", "onBackPressed in DetailFragment ");
			} else if (searchFragment.lay_hideme.getVisibility() == View.VISIBLE) {
				Log.e("", "onBackPressed in getVisibility ");
				searchFragment.disableSearchList();
			} else {
				Log.e("", "onBackPressed in else ");
				if (SearchFragment.isSearch) {
					LatLng latLng = map.getCameraPosition().target;
					setAllMarkers();
					searchFragment.disableSeachBox();
					// searchFragment.disableSearchList();
					searchFragment.disableSearchValues();
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
							2));
					FootFragment fragment = (FootFragment) fm
							.findFragmentByTag("FootFragment");
					fragment.changeFootlistViewForStart();
				} else {
					closeApp();
				}
			}
		} else {
			closeApp();
		}
	}

	private void closeApp() {
		if (!(fm.getBackStackEntryCount() > 0) && !isFirstBackPressed) {
			isFirstBackPressed = true;
			Toast.makeText(context, "Press back again to exit",
					Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					isFirstBackPressed = false;
				}
			}, 1500);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		Log.e("", "onDestroy");
		catgorizedRestos.list.clear();
		map.clear();
		previousMarker = null;
		markerMap.clear();
		searchList.searchList.clear();
		searchList.getSearchList().clear();
		restaurants.clear();
		KEYS.clear();
		hasFullyLoaded = false;
		price_list.clear();
		searchFragment.clearAllData();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.e("", "onPause");
		super.onPause();
	}

	@Override
	protected void onStart() {
		Log.e("", "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.e("", "onStop");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		System.out.println("onRestart");
		super.onRestart();
	}

	private void getScreenDimensions() {
		KentValues.width = getResources().getDisplayMetrics().widthPixels;
		KentValues.height = getResources().getDisplayMetrics().heightPixels;
		KentValues.screenDensity = getResources().getDisplayMetrics().density;
	}

	public boolean isGPS_Enabled(LocationManager locationManager) {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setNeutralButton("Settings Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								checkGPS = true;
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (!isLoading) {
			Log.e("", "onConfigurationChanged");
			// configChanges(newConfig);
			int w = KentValues.width;
			KentValues.width = KentValues.height;
			KentValues.height = w;
			refreshData();
		} else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					onConfigurationChanged(newConfig);
				}
			}, 200);
		}

	}

	private void configChanges(Configuration newConfig) {

		Log.e("", "onConfigurationChanged");
		fm.beginTransaction().remove(footFragment).commit();
		addFootFragment(footerParameters);
		hasFullyLoaded = false;

		fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
		fm.beginTransaction().remove(footListFragment).commit();
		footListFragment = new FootListFragment();
		footListFragment.setOrientation(newConfig.orientation);
		fm.executePendingTransactions();
		fm.popBackStack("FootListFragment", R.id.container_footlist);
		fm.beginTransaction()
				.add(R.id.container_footlist, footListFragment,
						"FootListFragment").commit();

		footListFragment.setCategory(footerParameters[0]);

		LinearLayout layout = (LinearLayout) findViewById(R.id.handle);
		LinearLayout.LayoutParams params;

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (KentValues.height - 175 * (getResources()
							.getDisplayMetrics().density)));

		} else {
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (KentValues.width - 175 * (getResources()
							.getDisplayMetrics().density)));
		}

		layout.setLayoutParams(params);

		isLoading = false;
	}

	public void addFootlistFragment() {
		footListFragment = new FootListFragment();
		footListFragment
				.setOrientation(getResources().getConfiguration().orientation);
		fm.beginTransaction()
				.add(R.id.container_footlist, footListFragment,
						"FootListFragment").commit();
		footListFragment.setCategory(footerParameters[0]);
	}

	private void setMyLocationbuttonPosition(SupportMapFragment fm) {
		View myLocationButton;
		myLocationButton = fm.getView().findViewById(0x2);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLocationButton
				.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		final int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 75, getResources()
						.getDisplayMetrics());
		params.setMargins(0, margin, 20, 0);
		myLocationButton.setLayoutParams(params);
	}

	public void showMe() {
		if (map != null && myLocation != null) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 11));
			checkGPS = false;
		}
	}

	public void setDefaultMap() {
		setAllMarkers();
		previousMarker = null;
	}

	public void refreshData() {

		isLoading = true;
		catgorizedRestos.list.clear();
		map.clear();
		previousMarker = null;
		markerMap.clear();
		restaurants.clear();
		KEYS.clear();
		price_list.clear();
		hasFullyLoaded = false;
		footerParameters = null;

		footFragment.clearFooterData();
		searchFragment.clearAllData();

		fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);

		if (fm.findFragmentByTag("FootFragment") != null) {
			fm.beginTransaction().remove(fm.findFragmentByTag("FootFragment"))
					.commit();
		}
		if (fm.findFragmentByTag("SearchFragment") != null) {
			fm.beginTransaction()
					.remove(fm.findFragmentByTag("SearchFragment")).commit();
		}
		if (fm.findFragmentByTag("FootListFragment") != null) {
			fm.beginTransaction()
					.remove(fm.findFragmentByTag("FootListFragment")).commit();
		}

		new LoadAllData().execute();
	}

	public void refershDataGlass(View view) {
		if (!isLoading && (fm.findFragmentByTag("FootFragment") != null)) {
			refreshData();
		}
	}

	class PersonListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";
		private int swipe_Min_Distance = 20;
		private int swipe_Max_Distance = 350;
		private int swipe_Min_Velocity = 15;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.e(DEBUG_TAG, "onFling");

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
				if (e1.getY() < e2.getY()) // up to bottom
				{
					drawer();
				}
				result = true;
			}

			return result;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.e(DEBUG_TAG, "onSingleTapConfirmed");
			drawer();
			return super.onSingleTapConfirmed(e);
		}
	}

	private void showMessage(final String msg) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void contentsInitialization() {
		fm = getSupportFragmentManager();
		map.getUiSettings().setZoomControlsEnabled(false);
		layFather = (LinearLayout) findViewById(R.id.father);
		gabber = (LinearLayout) findViewById(R.id.gabber);
		container_footlist = (FrameLayout) findViewById(R.id.container_footlist);
		container_foot = (FrameLayout) findViewById(R.id.container_foot);
		container_top = (FrameLayout) findViewById(R.id.container_top);
		container_profile = (FrameLayout) findViewById(R.id.container_profile);
		container_details = (FrameLayout) findViewById(R.id.container_details);
		container_reviewlist = (FrameLayout) findViewById(R.id.container_reviewlist);
		container_search = (FrameLayout) findViewById(R.id.container_search);
		handle = (LinearLayout) findViewById(R.id.handle);
		txt_back = (TextView) findViewById(R.id.txtpers_back);
		userOptn = (ImageView) findViewById(R.id.img_user_pers);
		imgpers_gps = (ImageView) findViewById(R.id.imgpers_gps);
		imgpers_gps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkGPS = true;
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		});
		txt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		gestureDetector = new GestureDetector(context, new PersonListener());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		((ImageView) findViewById(R.id.img_user_pers))
				.setOnTouchListener(gestureListener);
	}

	public void hideFootList() {
		fm.beginTransaction().hide(footListFragment).commit();
	}

	public void removeDetailFragment() {
		Fragment fragment = fm.findFragmentByTag("DetailFragment");
		if (fragment != null) {
			fm.beginTransaction().remove(fragment).commit();
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}
}
