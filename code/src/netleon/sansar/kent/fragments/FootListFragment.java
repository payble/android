package netleon.sansar.kent.fragments;

import java.util.ArrayList;
import java.util.List;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Restaurant;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FootListFragment extends ListFragment {

	Perspective perspective;
	String category;
	List<Restaurant> footlist = new ArrayList<Restaurant>();
	FootListAdapter adapter;
	int orientation;

	public void setFootlist(List<Restaurant> footlist) {
		this.footlist = footlist;
		adapter.setNewList(footlist);
	}

	public List<Restaurant> getFootlist() {
		return footlist;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getOrientation() {
		return orientation;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		setListShown(true);

		if (SearchFragment.isSearch) {
			Log.e("", "serch is ON ");
			footlist = SearchFragment.restos.getListByCategory(category);
		} else {
			Log.e("", "serch is OFF ");
			footlist = KentValues.catgorizedRestos.getListByCategory(category);
		}

		for (int i = 0; i < footlist.size(); i++) {
			Log.e("", "name : " + footlist.get(i).getName());
		}

		((Perspective) getActivity()).searchFragment.disableSeachBox();

		setListViewAppearance(orientation);
		setListViewAdapter();

		animateME();
		hide_search_bar();
		hide_profile();
		Log.e("", "footlist onViewCreated");
		super.onViewCreated(view, savedInstanceState);
	}

	private void animateME() {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_in_bottom);
		anim.setDuration(200);
		((Perspective) getActivity()).handle.startAnimation(anim);
	}

	private void setListViewAdapter() {
		if (footlist != null) {
			adapter = new FootListAdapter(getActivity(), footlist);
			getListView().setAdapter(adapter);
		} else {
			showMessage("No restaurants available");
		}
	}

	private void showMessage(String Message) {
		Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
	}

	public void setListViewAppearance(int Orientation) {
		if (Orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.e("", "orientaion portrait received setting setListViewDimensionsH...");
			setListViewDimensionsH();
		} else {
			Log.e("", "orientaion landscape received setting setListViewDimensionsW...");
			setListViewDimensionsW();
		}
		getListView()
				.setDivider(new ColorDrawable(Color.parseColor("#CCCCCC")));
		getListView().setDividerHeight(2);
		getListView().setBackgroundColor(Color.parseColor("#99000000"));
	}

	private void setListViewDimensionsH() {
		getListView().setLayoutParams(
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.FILL_PARENT,
						(int) (KentValues.height - 175 * (getActivity()
								.getResources().getDisplayMetrics().density))));
	}

	private void setListViewDimensionsW() {
		getListView().setLayoutParams(
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.FILL_PARENT,
						(int) (KentValues.height - 175 * (getActivity()
								.getResources().getDisplayMetrics().density))));
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			Perspective.txt_back.setVisibility(View.VISIBLE);
			Perspective.imgpers_gps.setVisibility(View.GONE);
			KentValues.isLoading = false;
		} else {
			Perspective.txt_back.setVisibility(View.INVISIBLE);
			Perspective.imgpers_gps.setVisibility(View.VISIBLE);
			try {
				((Perspective) getActivity()).footFragment.setDefaultView();

			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if ((!((Perspective) getActivity()).hasFullyLoaded)) {
			Log.e("", "hasFullyLoaded is false");
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.hide(getActivity().getSupportFragmentManager()
							.findFragmentByTag("FootListFragment")).commit();
			((Perspective) getActivity()).hasFullyLoaded = true;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Log.d("position", "" + position);
		Bundle temp = new Bundle();
		temp.putInt("pos", position);
		Fragment fragment = new Fragment();
		fragment.setArguments(temp);

		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.hide(getActivity().getSupportFragmentManager()
						.findFragmentByTag("FootFragment")).commit();

		getActivity().getSupportFragmentManager().beginTransaction()
				.add(R.id.container_details, fragment, "DetailFragment")
				.addToBackStack("DetailFragment").commit();

		super.onListItemClick(l, v, position, id);
	}

	public class FootListAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		Context mContext;
		List<Restaurant> footlist = new ArrayList<Restaurant>();

		public FootListAdapter(Context context, List<Restaurant> footlist) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mContext = context;
			this.footlist = footlist;
		}

		@Override
		public Object getItem(int position) {
			return footlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setNewList(List<Restaurant> footlist) {
			this.footlist = footlist;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (footlist == null) {
				return 0;
			} else {
				return footlist.size();
			}
		}

		public class ViewHolder {

			public final TextView txt_no, txt_name, txt_time;
			RatingBar ratingBar;

			public ViewHolder(TextView txt_no, TextView txt_name,
					TextView txt_time, RatingBar ratingBar) {

				this.txt_no = txt_no;
				this.txt_name = txt_name;
				this.txt_time = txt_time;
				this.ratingBar = ratingBar;
			}
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			final TextView txt_no, txt_name, txt_time;
			RatingBar ratingBar;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.custom_list_footlist,
						parent, false);
				txt_no = (TextView) convertView
						.findViewById(R.id.txtcustomfootlist_no);
				txt_name = (TextView) convertView
						.findViewById(R.id.txtcustomfootlist_name);
				txt_time = (TextView) convertView
						.findViewById(R.id.txtcustomfootlist_time);

				ratingBar = (RatingBar) convertView
						.findViewById(R.id.ratingfootlist);

				convertView.setTag(new ViewHolder(txt_no, txt_name, txt_time,
						ratingBar));

			} else {
				ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				txt_no = viewHolder.txt_no;
				txt_name = viewHolder.txt_name;
				txt_time = viewHolder.txt_time;
				ratingBar = viewHolder.ratingBar;
			}

			txt_no.setText(Integer.toString(position + 1));
			txt_name.setText(footlist.get(position).getName());

			String time = footlist.get(position).getWaitTime();

			if (Integer.parseInt(time) == 1) {
				time = time.concat(" minute");
			} else {
				time = time.concat(" minutes");
			}
			txt_time.setText(time);

			float dd = Float.parseFloat(footlist.get(position).getRating());
			float x = (float) ((dd * 5) / 100);
			ratingBar.setRating(x);

			convertView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					Bundle bundle = new Bundle();
					Restaurant restaurant = footlist.get(position);
					bundle.putParcelable("restaurant", restaurant);
					Fragment fragment = new DetailFragment();
					fragment.setArguments(bundle);
					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.hide(getActivity().getSupportFragmentManager()
									.findFragmentByTag("FootFragment"))
							.commit();

					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.container_details, fragment,
									"DetailFragment")
							.addToBackStack("DetailFragment").commit();
					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.hide(getActivity().getSupportFragmentManager()
									.findFragmentByTag("FootListFragment"))
							.commit();
				}
			});
			return convertView;
		}
	}

	protected void hide_search_bar() {
		Fragment fragment = getActivity().getSupportFragmentManager()
				.findFragmentByTag("SearchFragment");
		if (((SearchFragment) fragment).lay_hideme.getVisibility() == View.VISIBLE) {
			((SearchFragment) fragment).disableSearchList();
		}
	}

	protected void hide_profile() {

		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"UserOptionFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.remove(getActivity().getSupportFragmentManager()
							.findFragmentByTag("UserOptionFragment")).commit();
		}
	}
}