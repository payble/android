package netleon.sansar.kent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPassword extends Activity {

	EditText edit_email;
	String str_forgot_email;
	TextView image_go;
	ProgressDialog dialog;
	String endpoint = "http://netleondev.com/kentapi/user/forgotpassword";
	boolean sucess = false;
	String msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);

		((TextView) findViewById(R.id.txt_cancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		edit_email = (EditText) findViewById(R.id.edit_forgot_email);

		image_go = (TextView) findViewById(R.id.rest_password);
		image_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						((LinearLayout) findViewById(R.id.lin_log))
								.getWindowToken(), 0);
				if (!edit_email.getText().toString().equalsIgnoreCase("")) {
					new SendData().execute();
				} else {
					edit_email.setError("Enter email.");
				}

			}
		});

	}

	private JSONObject margya(String email) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("email", email);
			// obj.put("nickname", null);
			System.out.print(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private class SendData extends AsyncTask<String, Integer, Void> {

		@Override
		protected void onPreExecute() {

			str_forgot_email = edit_email.getText().toString();

			dialog = new ProgressDialog(ForgotPassword.this);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait ... ");
			dialog.setIndeterminate(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient client = new DefaultHttpClient();
			String postURL = (endpoint);
			HttpPost post = new HttpPost(postURL);
			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
			try {
				// Add the data
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);

				pairs.add(new BasicNameValuePair("email", str_forgot_email));

				JSONObject json = margya(str_forgot_email);

				try {
					post.setEntity(new StringEntity(json.toString()));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Execute the HTTP Post Request
				HttpResponse response = client.execute(post);
				// Convert the response into a String
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					String sts = EntityUtils.toString(resEntity);
					Log.i("RESPONSE", sts);
					try {
						JSONObject jsonObject = new JSONObject(sts);

						if (jsonObject.has("success")) {
							sucess = true;
							msg = jsonObject.getString("success").toString();
						} else {
							msg = jsonObject.getString("error").toString();
							sucess = false;
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			} catch (ClientProtocolException cpe) {
				cpe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.cancel();

			if (sucess) {
				Toast.makeText(ForgotPassword.this, msg, Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(ForgotPassword.this, msg, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}