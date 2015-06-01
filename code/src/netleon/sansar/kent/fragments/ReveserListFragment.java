package netleon.sansar.kent.fragments;

import java.util.ArrayList;
import java.util.List;

import netleon.sansar.kent.R;
import netleon.sansar.kent.R.id;
import netleon.sansar.kent.R.layout;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Reservation;
import netleon.sansar.kent.network.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReveserListFragment extends Fragment {

	Button btn_back;
	ListView list_reserv;
	ProgressDialog proDialog;
	String URL_Reservation, user_id;
	SharedPreferences preferences;
	int size = 0;

	ArrayList<Reservation> reservationLists = new ArrayList<Reservation>();

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.reservation_list, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/LetterGothicStd.otf");
		preferences = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE);
		user_id = preferences.getString("ID", "00");

		URL_Reservation = "http://netleondev.com/kentapi/user/reservations/userid/"
				+ user_id;

		Log.e("", "reserver list url : " + URL_Reservation);
		btn_back = (Button) view.findViewById(R.id.butreservfrag_back);
		((TextView) view.findViewById(R.id.pay_head)).setTypeface(face2);

		((TextView) view.findViewById(R.id.txt_restname)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_reststart)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_restend)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_restparty)).setTypeface(face1);

		list_reserv = (ListView) view.findViewById(R.id.resverlist);
		btn_back.setTypeface(face1);
		new loadList().execute();

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().getSupportFragmentManager()
						.popBackStackImmediate();
			}
		});

	}

	public class ReservationAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		Context mContext;
		ArrayList<Reservation> reservationLists;

		public ReservationAdapter(Context context,
				ArrayList<Reservation> reservationLists) {
			// TODO Auto-generated constructor stub
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mContext = context;
			this.reservationLists = reservationLists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return reservationLists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.customeresverlist, parent,
					false);
			Typeface face3 = Typeface.createFromAsset(
					getActivity().getAssets(), "fonts/Avenir.ttc");

			TextView txt_restname, txt_start, txt_end, txt_party;

			txt_restname = (TextView) convertView
					.findViewById(R.id.txt_rest_name);
			txt_start = (TextView) convertView
					.findViewById(R.id.txt_rest_start);
			txt_end = (TextView) convertView.findViewById(R.id.txt_rest_end);
			txt_party = (TextView) convertView
					.findViewById(R.id.txt_rest_party);

			txt_restname.setTypeface(face3);
			txt_start.setTypeface(face3);
			txt_end.setTypeface(face3);
			txt_party.setTypeface(face3);

			Reservation myList = reservationLists.get(position);

			txt_restname.setText(myList.getRest_name());
			txt_start.setText(myList.getStrat_date());
			txt_end.setText(myList.getEnd_date());
			txt_party.setText(myList.getParty_size());

			return convertView;
		}

	}

	class loadList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDialog = new ProgressDialog(getActivity());
			proDialog.setMessage("Please wait...");
			proDialog.setIndeterminate(false);
			proDialog.setCancelable(false);
			proDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			JSONParser parser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Log.e("", "this is url : " + URL_Reservation);
			String result = parser.makeHttpRequest(URL_Reservation, "GET",
					params);

			Log.e("", "data is : " + result);

			if (result != null) {
				size = result.length();
				try {

					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						System.out.println(i + " : " + array.getString(i));
					}

					for (int i = 0; i < array.length(); i++) {

						// JSONArray array2 = array.getJSONArray(i);
						JSONObject c = array.getJSONObject(i);

						String rest_name = c.getString("restaurant_name");
						String strat_date = c.getString("start_date");
						String end_date = c.getString("end_date");
						String id = c.getString("id");
						String rest_id = c.getString("restaurant_id");
						String user_id = c.getString("user_id");
						String party_size = c.getString("party_size");
						String start_date_time = c.getString("start_datetime");
						String end_date_time = c.getString("end_datetime");
						String status = c.getString("status");

						Reservation list = new Reservation(rest_name,
								strat_date, end_date, id, rest_id, user_id,
								party_size, start_date_time, end_date_time,
								status);
						reservationLists.add(list);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
				size = 0;
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (size == 0) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "No Data Found..!!!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			proDialog.dismiss();
			list_reserv.setAdapter(new ReservationAdapter(getActivity(),
					reservationLists));
			KentValues.isLoading = false;
			super.onPostExecute(result);
		}
	}
}
