package netleon.sansar.kent.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import netleon.sansar.kent.R;
import netleon.sansar.kent.base.Review;
import netleon.sansar.kent.network.GetData;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewListFragment extends ListFragment {

	ProgressDialog proDialog;
	String idd;
	DateTime now;
	ListView listView;
	FootListAdapter footListAdapter;
	ArrayList<Review> reviews = new ArrayList<Review>();
	String URL_Review;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		idd = getArguments().getString("id");
		URL_Review = "http://netleondev.com/kentapi/restaurant/reviews/restaurant_id/"
				+ idd;
		Log.e("", "url for review : " + URL_Review);
		now = new DateTime(Calendar.getInstance().getTimeInMillis());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		listView = getListView();
		getListView().setDividerHeight(0);
		super.onViewCreated(view, savedInstanceState);
		setListShown(true);
		new GetReviews().execute();
	}

	public class GetReviews extends AsyncTask<String, Integer, Void> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			proDialog = new ProgressDialog(getActivity());
			proDialog.setMessage("Please wait...");
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			GetData data = new GetData();
			response = data.getDataFromServer(URL_Review);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			proDialog.dismiss();

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

				footListAdapter = new FootListAdapter(getActivity(), reviews);
				listView.setAdapter(footListAdapter);
			} else {
				Log.e("", "i in else");
			}
		}
	}

	public class FootListAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		Context mContext;
		ArrayList<Review> reviewlist;

		public FootListAdapter(Context context, ArrayList<Review> rev) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mContext = context;
			this.reviewlist = rev;
		}

		@Override
		public int getCount() {
			return reviewlist.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {

			public final TextView txt_review, txt_time;
			final ImageView upview;
			final ImageView image;
			RatingBar ratingBar;

			public ViewHolder(TextView txt_review, TextView txt_time,
					ImageView upview, ImageView image, RatingBar ratingBar) {

				this.txt_review = txt_review;
				this.txt_time = txt_time;
				this.upview = upview;
				this.image = image;
				this.ratingBar = ratingBar;
			}
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final TextView txt_review, txt_time;
			final ImageView upview;
			final ImageView image;
			RatingBar ratingBar;
			Float temp = getResources().getDisplayMetrics().density;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.custom_list_reviewlist, parent, false);

				txt_review = (TextView) convertView
						.findViewById(R.id.txtcustomreviewlist_review);
				txt_time = (TextView) convertView
						.findViewById(R.id.txtcustomreviewlist_time);

				upview = (ImageView) convertView
						.findViewById(R.id.imgcustomreviewlist_up);

				image = (ImageView) convertView
						.findViewById(R.id.imgcustomreviewlist);
				ratingBar = (RatingBar) convertView
						.findViewById(R.id.ratingbar_default);

				convertView.setTag(new ViewHolder(txt_review, txt_time, upview,
						image, ratingBar));

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

			} else {

				ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				txt_review = viewHolder.txt_review;
				txt_time = viewHolder.txt_time;
				upview = viewHolder.upview;
				image = viewHolder.image;
				ratingBar = viewHolder.ratingBar;
			}

			ratingBar.setRating(Float.parseFloat(reviews.get(position)
					.getRating()));

			txt_review.setText(reviewlist.get(position).getComment());
			DateTime dategot = reviewlist.get(position).getDateTime();
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

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}
	}
}
