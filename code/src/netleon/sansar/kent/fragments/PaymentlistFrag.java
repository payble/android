package netleon.sansar.kent.fragments;

import java.util.ArrayList;
import java.util.List;

import netleon.sansar.kent.R;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.base.Payment;
import netleon.sansar.kent.network.GetData;
import netleon.sansar.kent.network.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentlistFrag extends Fragment {

	ListView listView;
	ProgressDialog proDialog;

	String URL_Payments, user_id;
	ArrayList<Payment> payments = new ArrayList<Payment>();
	PaymentAdapter adapter;
	Button button_addpayment;
	SharedPreferences preferences;
	int nowpos;

	String Url_setDefaultCard = "http://netleondev.com/kentapi/user/markcardasdefault";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		proDialog = new ProgressDialog(getActivity());
		proDialog.setMessage("Please wait...");
		proDialog.setIndeterminate(false);
		proDialog.setCancelable(false);
		proDialog.show();
		return inflater.inflate(R.layout.paymentlistfrag, container, false);
	}

	@Override
	public void onDetach() {
		payments.clear();
//		adapter.notifyDataSetChanged();
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		payments.clear();
		super.onAttach(activity);
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		payments.clear();
		listView = (ListView) view.findViewById(R.id.listpay);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View footerView = inflater.inflate(R.layout.footer_paymentlist, null,
				false);
		preferences = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE);

		user_id = preferences.getString("ID", "00");

		URL_Payments = "http://netleondev.com/kentapi/user/creditcards/userid/"
				+ user_id;

		button_addpayment = (Button) footerView
				.findViewById(R.id.butpay_addpayment);
		listView.addFooterView(footerView);

		Log.e("", " this is url:" + URL_Payments);

		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/LetterGothicStd.otf");

		((TextView) view.findViewById(R.id.txt_pay)).setTypeface(face2);
		((Button) view.findViewById(R.id.butpay_back)).setTypeface(face1);

		((Button) view.findViewById(R.id.butpay_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().getSupportFragmentManager()
								.popBackStackImmediate();

						payments.clear();
					}
				});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Fragment fragment = new PaymentFrag();
				Bundle bundle = new Bundle();

				bundle.putString("id", payments.get(position).getId());
				bundle.putInt("pos", position);
				bundle.putString("cc_type", payments.get(position)
						.getCard_type());
				bundle.putString("cc_name", payments.get(position).getName());
				bundle.putString("cc_number", payments.get(position).getCc_no());
				bundle.putString("cc_expiration_month", payments.get(position)
						.getExpiration_month());
				bundle.putString("cc_expiration_year", payments.get(position)
						.getExpiration_year());
				bundle.putString("cc_cvv", payments.get(position).getCcv());
				bundle.putBoolean("isDefault", payments.get(position)
						.isIs_default());

				fragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction()
						.add(R.id.container_top, fragment, "PaymentFrag")
						.addToBackStack("PaymentFrag").commit();
			}
		});
		button_addpayment.setTypeface(face1);
		button_addpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.container_top, new AddPaymentFrag(),
								"AddPaymentFrag")
						.addToBackStack("AddPaymentFrag").commit();
			}
		});

		new LoadAllData().execute();
	}

	public class PaymentAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		Context mContext;
		ArrayList<Payment> payments;

		public PaymentAdapter(Context context, ArrayList<Payment> payments) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mContext = context;
			this.payments = payments;
		}

		@Override
		public Object getItem(int position) {
			return payments.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return payments.size();
		}

		public class ViewHolder {

			TextView txt_title, txt_cardno;
			ImageView img_card, img_star;

			public ViewHolder(TextView txt_title, TextView txt_cardno,
					ImageView img_card, ImageView img_star) {

				this.txt_title = txt_title;
				this.txt_cardno = txt_cardno;
				this.img_card = img_card;
				this.img_star = img_star;
			}
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			TextView txt_title, txt_cardno;
			ImageView img_card, img_star;

			Typeface face3 = Typeface.createFromAsset(
					getActivity().getAssets(), "fonts/Avenir.ttc");

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.custom_paymentlist,
						parent, false);

				txt_title = (TextView) convertView
						.findViewById(R.id.txtcustompay_title);
				img_card = (ImageView) convertView
						.findViewById(R.id.imgcustompay_card);
				txt_cardno = (TextView) convertView
						.findViewById(R.id.txtcustompay_number);
				img_star = (ImageView) convertView
						.findViewById(R.id.imgcustompay_star);

				txt_title.setTypeface(face3);
				txt_cardno.setTypeface(face3);
				if (position == 0) {

					img_card.setBackground(getResources().getDrawable(
							R.drawable.profile_name_corner));
					img_star.setBackground(getResources().getDrawable(
							R.drawable.profile_name_show_corner));
				} else {
					img_card.setBackgroundColor(getResources().getColor(
							R.color.background));
					img_star.setBackgroundColor(getResources().getColor(
							R.color.background));
				}

				convertView.setTag(new ViewHolder(txt_title, txt_cardno,
						img_card, img_star));

			} else {

				ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				txt_title = viewHolder.txt_title;
				txt_cardno = viewHolder.txt_cardno;
				img_card = viewHolder.img_card;
				img_star = viewHolder.img_star;
				if (position == 0) {

					viewHolder.img_card.setBackground(getResources()
							.getDrawable(R.drawable.profile_name_corner));
					viewHolder.img_star.setBackground(getResources()
							.getDrawable(R.drawable.profile_name_show_corner));
				} else {
					viewHolder.img_card.setBackgroundColor(getResources()
							.getColor(R.color.background));
					viewHolder.img_star.setBackgroundColor(getResources()
							.getColor(R.color.background));
				}
			}

			if (payments.get(position).isIs_default()) {
				img_card.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_card_enabled));
				img_star.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_star_blue));
				txt_title.setTextColor(Color.BLACK);
				txt_cardno.setTextColor(Color.BLACK);
			} else {
				img_card.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_card_disabled));
				img_star.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_star_grey));
				txt_title.setTextColor(Color.parseColor("#949494"));
				txt_cardno.setTextColor(Color.parseColor("#949494"));
			}

			txt_title.setText(payments.get(position).getCard_type());

			Log.e("", "CARD TYPE : " + payments.get(position).getCard_type());

			String cardno = payments.get(position).getCc_no();
			txt_cardno.setText("....".concat(cardno.substring(
					cardno.length() - 4, cardno.length())));

			img_star.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					nowpos = position;

					String user_card_id = payments.get(position).getId();
					JSONObject object = create_JSON(user_id, user_card_id);
					String data = object.toString();

					new SetDeaultCard(data).execute();
				}
			});

			return convertView;
		}
	}

	class LoadAllData extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			JSONParser parser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String result = parser.makeHttpRequest(URL_Payments, "GET", params);

			if (result != null) {
				try {

					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						System.out.println(i + " : " + array.getString(i));
					}

					for (int i = 0; i < array.length(); i++) {

						// JSONArray array2 = array.getJSONArray(i);
						JSONObject c = array.getJSONObject(i);

						String id = c.getString("id");
						String card_typee = c.getString("cc_type");
						Log.e("", "card type : : " + card_typee);
						String name = c.getString("cc_name");
						String cc_no = c.getString("cc_number");
						String expiration_month = c
								.getString("cc_expiration_month");
						String expiration_year = c
								.getString("cc_expiration_year");
						String ccv = c.getString("cc_cvv");
						Boolean is_default;

						if (c.getString("is_default").equalsIgnoreCase("1")) {
							is_default = true;
						} else {
							is_default = false;
						}

						Payment payment = new Payment(id, card_typee, name,
								cc_no, expiration_year, expiration_month, ccv,
								is_default);
						payments.add(payment);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			for (int i = 0; i < payments.size(); i++) {
				Log.e("", " rec name : " + payments.get(i).getName());
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			proDialog.dismiss();
			adapter = new PaymentAdapter(getActivity(), payments);
			listView.setAdapter(adapter);
			KentValues.isLoading = false;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Log.d("", "CANCELED");
			proDialog.dismiss();

		}

	}

	class SetDeaultCard extends AsyncTask<String, String, String> {

		String data;
		String response = null;

		public SetDeaultCard(String data) {
			this.data = data;
		}

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
			response = data.postDataToServer(Url_setDefaultCard, this.data);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			proDialog.dismiss();
			if (response != null) {
				try {
					JSONObject object = new JSONObject(response);
					if (object.has("error")) {
						final String error = object.getString("error");
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(getActivity(), error,
										Toast.LENGTH_LONG).show();
							}
						});

					} else {

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								for (int i = 0; i < payments.size(); i++) {
									if (payments.get(i).isIs_default()) {
										payments.get(i).setIs_default(false);
									}
								}
								Log.e("", "On click pos : " + nowpos);
								payments.get(nowpos).setIs_default(true);
								adapter.notifyDataSetChanged();

								// Toast.makeText(getActivity(),
								// "Marked as default!", Toast.LENGTH_LONG)
								// .show();
							}
						});
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private JSONObject create_JSON(String userid, String usercardid) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("user_id", userid);
			obj.put("user_card_id", usercardid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public void updateList(int pos, String id, String str_card,
			String str_name, String str_num, String str_ccv,
			String str_exp_year, String str_exp_month, Boolean isDefau) {
		Log.e("", "Update called");
		Payment payment = new Payment(id, str_card, str_name, str_num,
				str_exp_year, str_exp_month, str_ccv, isDefau);

		adapter.payments.set(pos, payment);
		adapter.notifyDataSetInvalidated();
	}
}
