package netleon.sansar.kent.fragments;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.R;
import netleon.sansar.kent.network.GetData;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddReviewFragment extends Fragment {

	TextView txt_cancel, txt_done, company_name;

	Button txt_submit;
	RatingBar ratingBar;
	float rating;

	EditText edit_review;
	String str_review, str_user_id, str_rating;
	String company, str_comment;
	String url_add_review = "http://netleondev.com/kentapi/user/review";
	String rest_id;
	SharedPreferences preferences;
	boolean succes = false;
	boolean isCheck = false;
	ProgressDialog proDialog;
	String error;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		return inflater.inflate(R.layout.addreviewfragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");
		Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/LetterGothicStd.otf");
		company = getArguments().getString("company");
		rest_id = getArguments().getString("restaurant_id");
		Log.e("", "rest id is : " + rest_id);
		Log.e("", "rest id is : " + company);
		txt_cancel = (TextView) view.findViewById(R.id.txt_review_cancel);
		txt_done = (TextView) view.findViewById(R.id.txt_review_done);
		ratingBar = (RatingBar) view.findViewById(R.id.review_ratingBar);
		txt_submit = (Button) view.findViewById(R.id.submit_review);
		company_name = (TextView) view.findViewById(R.id.txt_company_review);
		edit_review = (EditText) view.findViewById(R.id.edit_review);

		preferences = getActivity().getSharedPreferences("NESSIE",
				getActivity().MODE_PRIVATE);

		str_user_id = preferences.getString("ID", "00");

		company_name.setText(company);

		txt_cancel.setTypeface(face1);
		txt_done.setTypeface(face1);
		txt_submit.setTypeface(face1);
		company_name.setTypeface(face1);
		edit_review.setTypeface(face1);

		((TextView) view.findViewById(R.id.head_profle)).setTypeface(face2);
		((TextView) view.findViewById(R.id.revie_tag)).setTypeface(face1);
		((TextView) view.findViewById(R.id.company)).setTypeface(face1);
		((TextView) view.findViewById(R.id.txt_review)).setTypeface(face1);

		((TextView) view.findViewById(R.id.head_profle))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ratingBar.isFocused()) {
							ratingBar.clearFocus();
						}
					}
				});

		txt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.show(getActivity().getSupportFragmentManager()
								.findFragmentByTag("RecieptFragment")).commit();
				getActivity().getSupportFragmentManager()
						.popBackStackImmediate();

			}
		});

		// ratingBar.setOnRatingBarChangeListener(new
		// OnRatingBarChangeListener() {
		// public void onRatingChanged(RatingBar ratingBar, float rating,
		// boolean fromUser) {
		//
		// Toast.makeText(getActivity(),
		// "Your Selected Ratings  : " + String.valueOf(rating),
		// Toast.LENGTH_SHORT).show();
		// str_rating = String.valueOf(rating);
		//
		// }
		// });
		//

		txt_submit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				rating = ratingBar.getRating();
				str_comment = edit_review.getText().toString();
				JSONObject object = create_JSON(str_user_id, rest_id,
						Float.toString(rating), str_comment);
				String data = object.toString();

				new SendData(data).execute();

			}
		});

		super.onViewCreated(view, savedInstanceState);
	}

	private JSONObject create_JSON(String user_id, String rest_id,
			String rating, String comments) {

		JSONObject obj = new JSONObject();
		try {
			obj.put("user_id", user_id);
			obj.put("restaurant_id", rest_id);
			obj.put("rating", rating);
			obj.put("comment", comments);

			System.out.print(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private class SendData extends AsyncTask<String, Integer, Void> {

		String data;
		String response = null;

		public SendData(String data) {
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

		@Override
		protected Void doInBackground(String... params) {
			GetData data = new GetData();
			response = data.putDataToServer(url_add_review, this.data);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.e("", "response got is : " + response);

			if (response != null) {

				try {
					if (response.matches("[0-9]+") && response.length() > 1) {

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(getActivity(),
										"Thanks for review", Toast.LENGTH_LONG)
										.show();
							}
						});
					}

					proDialog.dismiss();
					
					getActivity()
					.getSupportFragmentManager()
					.popBackStack(
							null,
							getActivity().getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);

					((Perspective) getActivity()).refreshData();

					

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			super.onPostExecute(result);
		}
	}

}
