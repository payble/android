package netleon.sansar.kent.fragments;

import static netleon.sansar.kent.base.KentValues.isLoading;
import static netleon.sansar.kent.base.KentValues.restaurants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.base.CatgorizedRestos;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Restaurant;
import netleon.sansar.kent.base.SearchKey;
import netleon.sansar.kent.base.SearchList;
import netleon.sansar.kent.base.SearchVal;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchFragment extends Fragment {

	public LinearLayout lay_hideme;
	ImageView img_hider;
	ListView listView;
	String distance_selected;
	static EditText edit_search;
	public static boolean isSearch = false;
	Spinner[] spinners;
	Adapter_Searchfrag adapter;
	HashMap<SearchKey, SearchVal> filterMap = new HashMap<SearchKey, SearchVal>();
	public static Map<Marker, Restaurant> filteredMarkers = new HashMap<Marker, Restaurant>();
	public static CatgorizedRestos restos = new CatgorizedRestos();

	public void clearAllData() {
		filterMap.clear();
		filteredMarkers.clear();
		restos.getList().clear();
		isSearch = false;
		spinners = null;
		adapter.getSearchList().clear();
	}

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.searchfragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setContentsInitialization(view);
		setListviewAdapter();
	}

	private void setListviewAdapter() {
		SearchList list = addSelectTagInSearchList();
		adapter = new Adapter_Searchfrag(getActivity(), list);
		listView.setAdapter(adapter);
	}

	private void setContentsInitialization(final View view) {
		listView = (ListView) view.findViewById(R.id.listsearch);
		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");

		View footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.searchlist_footer, null, false);
		listView.addFooterView(footerView);

		edit_search = (EditText) view.findViewById(R.id.search_filters);
		edit_search.setTypeface(face1);
		((Button) footerView.findViewById(R.id.butsearch)).setTypeface(face1);

		lay_hideme = (LinearLayout) view.findViewById(R.id.lin_hideme);
		lay_hideme.setVisibility(View.GONE);

		img_hider = (ImageView) view.findViewById(R.id.imgSearch_handle);
		img_hider.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (lay_hideme.getVisibility() == View.VISIBLE) {
					disableSearchList();
				} else {
					enableSearchList();
					disableSeachBox();
				}
			}
		});

		((LinearLayout) view.findViewById(R.id.linsearch_hider))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (lay_hideme.getVisibility() == View.VISIBLE) {
							lay_hideme.setVisibility(View.GONE);
						} else {
							lay_hideme.setVisibility(View.VISIBLE);
						}
					}
				});

		((Button) footerView.findViewById(R.id.butsearch))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onButtonSearchClick();
					}
				});

		((ImageView) view.findViewById(R.id.find))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						disableSearchValues();
						closeKeyBoard();
						String searchTag = edit_search.getText().toString()
								.trim();

						Perspective.previousMarker = null;
						if (searchTag.length() > 0) {
							getFilterByTag(searchTag);
						} else {
							disableSeachBox();
							((Perspective) getActivity()).setAllMarkers();
						}

					}

				});

		edit_search.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edit_search.setHint("");
					edit_search.setGravity(Gravity.LEFT
							| Gravity.CENTER_VERTICAL);

					if (lay_hideme.getVisibility() == View.VISIBLE) {
						disableSearchList();
					}

				} else {

					disableSeachBox();
					edit_search.setHint("Tap to search");
					edit_search.setGravity(Gravity.CENTER);
				}
			}
		});

		edit_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edit_search.setFocusable(true);
			}
		});

		edit_search.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					((ImageView) view.findViewById(R.id.find)).performClick();
					return true;
				}
				return false;
			}
		});

		edit_search.clearFocus();
		edit_search.addTextChangedListener(new MyTextWatcher());
	}

	private void onButtonSearchClick() {
		((Perspective) getActivity()).removeDetailFragment();
		((Perspective) getActivity()).hideFootList();
		Perspective.previousMarker = null;
		if (filterMap.size() > 0) {
			disableSearchList();
			filterRestaurents(filterMap);
		} else {
			((Perspective) (getActivity())).setAllMarkers();
			disableSeachBox();
			disableSearchList();
			disableSearchValues();
			GoogleMap map = Perspective.map;
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(
					(map.getCameraPosition().target), 2));
		}
	}

	public class Adapter_Searchfrag extends BaseAdapter {

		Context context;
		HashMap<SearchKey, List<SearchVal>> searchList;
		LayoutInflater inflator;

		public Adapter_Searchfrag(Context context, SearchList searchList) {
			this.context = context;
			this.searchList = searchList.getSearchList();
			inflator = LayoutInflater.from(context);
			spinners = new Spinner[this.searchList.size()];
		}

		public HashMap<SearchKey, List<SearchVal>> getSearchList() {
			return searchList;
		}

		public int getCount() {
			return searchList.size();
		}

		@Override
		public Object getItem(int position) {
			return searchList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = inflator
					.inflate(R.layout.customlist_search, parent, false);

			SearchKey key = KentValues.KEYS.get(position);

			final TextView txt_name = (TextView) v
					.findViewById(R.id.txtcustomsearch);
			final Adapter_Spinner adptor = new Adapter_Spinner(context,
					searchList.get(key));

			final Spinner spinner = (Spinner) v
					.findViewById(R.id.spincustomsearch);
			spinners[position] = spinner;
			spinner.setTag(position);
			spinner.setAdapter(adptor);

			txt_name.setText(key.getName());

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int positions, long id) {

					Log.e("", "positions : " + positions);
					int tag = (Integer) parent.getTag();
					SearchKey key = KentValues.KEYS.get(tag);

					if (positions != 0) {
						filterMap.put(key, (SearchVal) spinner
								.getItemAtPosition(positions));
					} else {
						Log.e("", "removed key : " + key.getName());
						filterMap.remove(key);
					}
				}

				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			return v;
		}
	}

	public class Adapter_Spinner extends BaseAdapter {

		Context context;
		List<SearchVal> data;
		LayoutInflater mInflater;
		TextView txt_name;

		public Adapter_Spinner(Context context, List<SearchVal> data) {
			this.context = context;
			this.data = data;
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public SearchVal getItemOnPosition(int position) {
			return data.get(position);
		}

		class ViewHolder {
			TextView txt_name;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.horacius, parent,
						false);

				viewHolder = new ViewHolder();

				viewHolder.txt_name = (TextView) convertView
						.findViewById(R.id.txter);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.txt_name.setText(data.get(position).getName());

			return convertView;
		}

		public void setColor(String color) {
			txt_name.setTextColor(Color.parseColor(color));
		}

		public TextView getTxt_name() {
			return txt_name;
		}

	}

	private void filterRestaurents(HashMap<SearchKey, SearchVal> filterMap) {

		List<Restaurant> list = new ArrayList<Restaurant>();

		for (int i = 0; i < filterMap.size(); i++) {

			SearchVal searchVal = filterMap
					.get(filterMap.keySet().toArray()[i]);

			int id = searchVal.getId();
			if (i == 0) {
				for (int j = 0; j < restaurants.size(); j++) {

					Restaurant restaurant = restaurants.get(j);
					for (int k = 0; k < restaurant.getCategoryList().length; k++) {

						if (restaurant.getCategoryList()[k] == id) {
							list.add(restaurant);
						}
					}
				}
			} else {
				ArrayList<Restaurant> temp = new ArrayList<Restaurant>();
				for (int j = 0; j < list.size(); j++) {

					Restaurant restaurant = list.get(j);
					for (int k = 0; k < restaurant.getCategoryList().length; k++) {

						if (restaurant.getCategoryList()[k] == id) {
							temp.add(restaurant);
						}
					}
				}
				list = temp;
			}

		}

		letsGetCategorizedList(list);

		setFilteredMarkers(Perspective.map, list);

		isSearch = true;

		FootFragment fragment = (FootFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag("FootFragment");
		fragment.changeFootlistViewForStart();

		Toast.makeText(getActivity(), list.size() + " restaurants found",
				Toast.LENGTH_LONG).show();
	}

	private void letsGetCategorizedList(List<Restaurant> list) {

		if (restos.getList() != null) {
			restos.getList().clear();
		}

		for (int i = 0; i < list.size(); i++) {

			Restaurant restaurant = list.get(i);
			int[] restCatList = restaurant.getCategoryList();

			for (int j = 0; j < KentValues.price_list.size(); j++) {

				int temp = KentValues.price_list.get(j).getId();

				for (int k = 0; k < restCatList.length; k++) {
					if (temp == restCatList[k]) {
						Log.e("", "k : " + k);
						restos.addRestosToCat(KentValues.price_list.get(j)
								.getName(), restaurant);
					}
				}
			}
		}
	}

	private void setFilteredMarkers(GoogleMap map, List<Restaurant> list) {

		map.clear();

		for (int i = 0; i < list.size(); i++) {

			MarkerOptions options = new MarkerOptions()
					.position(list.get(i).getLatLng())
					.title(list.get(i).getName())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_marker_blue));

			Marker marker = map.addMarker(options);
			filteredMarkers.put(marker, list.get(i));
		}

		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		Object[] markers = filteredMarkers.keySet().toArray();
		int counter = 0;
		for (int i = 0; i < markers.length; i++) {
			counter++;
			Marker m = (Marker) markers[i];
			builder.include(m.getPosition());
		}
		if (counter > 0) {
			LatLngBounds bounds = builder.build();
			int padding = 50;
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
					padding);
			map.animateCamera(cu);
		}

	}

	public void getFilterByTag(String searchTag) {

		List<Restaurant> list = new ArrayList<Restaurant>();
		Log.e("", "got searchTag : " + searchTag);
		String[] parts = searchTag.split("\\s+");

		for (int i = 0; i < parts.length; i++) {
			Log.e("", "part " + i + " : " + parts[i]);
			getListByTag(list, parts[i]);
		}

		searchTag = searchTag.replace(" ", "");

		getListByTag(list, searchTag);

		if (list.size() > 0) {

			letsGetCategorizedList(list);
			setFilteredMarkers(Perspective.map, list);
			isSearch = true;
			FootFragment fragment = (FootFragment) getActivity()
					.getSupportFragmentManager().findFragmentByTag(
							"FootFragment");
			fragment.changeFootlistViewForStart();

			Toast.makeText(getActivity(), list.size() + " Restaurants found",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), "No restaurants found",
					Toast.LENGTH_LONG).show();
		}

	}

	private List<Restaurant> getListByTag(List<Restaurant> list,
			String searchTag) {
		HashMap<SearchKey, List<SearchVal>> AllSearches = Perspective.searchList
				.getSearchList();
		int search_id = 0007;

		String lowercase_searchTag = searchTag.toLowerCase(Locale.getDefault());

		for (int s = 0; s < restaurants.size(); s++) {

			Restaurant restaurant = restaurants.get(s);

			if (!restaurant.getName().matches("")) {
				if (restaurant.getName().toLowerCase(Locale.getDefault())
						.replace(" ", "").contains(lowercase_searchTag)) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("",
								"getName restaurent found : "
										+ restaurant.getName());
					}
				} else if (lowercase_searchTag.contains(restaurant.getName()
						.toLowerCase(Locale.getDefault()).replace(" ", ""))) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("",
								"getName restaurent found : "
										+ restaurant.getName());
					}
				}
			}
			if (!restaurant.getAddress().matches("")) {
				if (restaurant.getAddress().toLowerCase(Locale.getDefault())
						.replace(" ", "").contains(lowercase_searchTag)) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("",
								"getAddress restaurent found : "
										+ restaurant.getName());
					}
				} else if (lowercase_searchTag.contains(restaurant.getAddress()
						.toLowerCase(Locale.getDefault()).replace(" ", ""))) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("",
								"getAddress restaurent found : "
										+ restaurant.getName());
					}
				}
			}

			if (!restaurant.getState().matches("")) {
				if (restaurant.getState().toLowerCase(Locale.getDefault())
						.replace(" ", "").contains(lowercase_searchTag)) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("",
								"getState restaurent found : "
										+ restaurant.getName());
					}
				} else if (lowercase_searchTag.contains(restaurant.getState()
						.toLowerCase(Locale.getDefault()).replace(" ", ""))) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("",
								"getState restaurent found : "
										+ restaurant.getName());
					}
				}
			}
			if (!restaurant.getZip_code().matches("")) {
				if (restaurant.getZip_code().toLowerCase(Locale.getDefault())
						.replace(" ", "").contains(lowercase_searchTag)) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("", "getZip_code restaurent found : "
								+ restaurant.getName());
					}
				} else if (lowercase_searchTag.contains(restaurant
						.getZip_code().toLowerCase(Locale.getDefault())
						.replace(" ", ""))) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("", "getZip_code restaurent found : "
								+ restaurant.getName());
					}
				}
			}
			if (!restaurant.getKeywords().matches("")) {
				if (restaurant.getKeywords().toLowerCase(Locale.getDefault())
						.replace(" ", "").contains(lowercase_searchTag)) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("", "getKeywords restaurent found : "
								+ restaurant.getName());
					}
				}

				else if (lowercase_searchTag.contains(restaurant.getKeywords()
						.toLowerCase(Locale.getDefault()).replace(" ", ""))) {
					if (!list.contains(restaurant)) {
						list.add(restaurant);
						Log.e("", "getKeywords restaurent found : "
								+ restaurant.getName());
					}
				}
			}

		}

		if (AllSearches.size() > 0) {

			for (int i = 0; i < AllSearches.size(); i++) {

				List<SearchVal> searchVals = AllSearches.get(AllSearches
						.keySet().toArray()[i]);

				for (int j = 0; j < searchVals.size(); j++) {

					SearchVal val = searchVals.get(j);

					String tempo = val.getName().replace(" ", "")
							.toLowerCase(Locale.getDefault());

					if (tempo.contains(lowercase_searchTag)) {

						search_id = val.getId();
						Log.e("", "SearchVal : " + tempo);

						if (search_id != 0007) {

							for (int s = 0; s < restaurants.size(); s++) {

								Restaurant restaurant = restaurants.get(s);
								int[] listo = restaurant.getCategoryList();

								for (int e = 0; e < listo.length; e++) {

									int x = listo[e];

									if (x == search_id) {
										if (!list.contains(restaurant)) {
											list.add(restaurant);
											Log.e("", "restaurent found : "
													+ restaurant.getName());
										}
									}
								}
							}
						}
					} else if (lowercase_searchTag.contains(tempo)) {

						search_id = val.getId();
						Log.e("", "SearchVal : " + tempo);

						if (search_id != 0007) {

							for (int s = 0; s < restaurants.size(); s++) {

								Restaurant restaurant = restaurants.get(s);
								int[] listo = restaurant.getCategoryList();

								for (int e = 0; e < listo.length; e++) {

									int x = listo[e];

									if (x == search_id) {
										if (!list.contains(restaurant)) {
											list.add(restaurant);
											Log.e("", "restaurent found : "
													+ restaurant.getName());
										}
									}
								}
							}
						}

					}
				}
			}
		}

		return list;
	}

	public class MyTextWatcher implements TextWatcher {
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (count > 0) {
				if (getActivity().getSupportFragmentManager()
						.findFragmentByTag("DetailFragment") != null) {
					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.remove(getActivity().getSupportFragmentManager()
									.findFragmentByTag("DetailFragment"))
							.commit();
				}

				disableSearchValues();
			}
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			// edit_tip.getText().clear();
		}
	}

	public void disableSearchValues() {
		isSearch = false;
		filterMap.clear();
		filteredMarkers.clear();
		if (restos != null) {
			restos.getList().clear();
		}
	}

	public void disableSeachBox() {
		closeKeyBoard();
		edit_search.setText("");
		edit_search.clearFocus();
	}

	private void closeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	public void disableSearchList() {
		lay_hideme.setVisibility(View.GONE);
		img_hider.setImageResource(R.drawable.ic_action_expand);
		for (int v = 0; v < spinners.length; v++) {
			Log.d("", "spinner : " + v + " : " + spinners[v]);
			Spinner spinner = spinners[v];
			spinner.setSelection(0);
		}
		adapter.notifyDataSetChanged();
	}

	public void enableSearchList() {
		isLoading = true;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				isLoading = false;
			}
		}, 200);

		img_hider.setImageResource(R.drawable.ic_action_coll);
		lay_hideme.setVisibility(View.VISIBLE);

		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"FootListFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.hide(getActivity().getSupportFragmentManager()
							.findFragmentByTag("FootListFragment")).commit();
		}

		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"DetailFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.remove(getActivity().getSupportFragmentManager()
							.findFragmentByTag("DetailFragment")).commit();
		}
	}

	private SearchList addSelectTagInSearchList() {
		SearchList searchList = Perspective.searchList;
		HashMap<SearchKey, List<SearchVal>> list = searchList.getSearchList();
		for (int i = 0; i < list.size(); i++) {
			list.get(list.keySet().toArray()[i]).add(0,
					(new SearchVal(0, "Select", 0)));
		}
		return searchList;
	}

}
