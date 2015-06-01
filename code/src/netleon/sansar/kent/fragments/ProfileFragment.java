package netleon.sansar.kent.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import netleon.sansar.kent.R;
import netleon.sansar.kent.R.id;
import netleon.sansar.kent.R.layout;
import netleon.sansar.kent.base.KentValues;
import netleon.sansar.kent.support.MemoryCache;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

	TextView txt_edit, txt_save, txt_back;
	EditText edit_name, edit_email, edit_mobile, edit_password;
	String str_name, str_email, str_mobile, str_password;
	String temp_name, temp_email, temp_phone, temp_image;

	MemoryCache memoryCache = new MemoryCache();
	String tag_json_obj = "json_obj_req";

	Button button_save;
	ImageView img_profile;
	boolean isImageCHanged = false;
	LinearLayout lay_show_password;

	String url_update = "http://netleondev.com/kentapi/user/update";

	String TABLE_NAME = "images";

	String profile_name, profile_email, profile_mobile, profile_password,
			profile_userId;

	SharedPreferences preferences;
	ProgressDialog dialog, dialog2;

	private String upLoadServerUri = null;
	private String imagepath = null;
	String ba1;

	int proImgWidth, proImgHeight;
	Typeface tf_edit;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.profilefragment, container, false);
	}

	@Override
	public void onViewCreated(final View view,
			@Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		preferences = getActivity().getSharedPreferences("NESSIE",
				Context.MODE_PRIVATE);

		tf_edit = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Avenir.ttc");

		upLoadServerUri = "http://netleondev.com/kentapi/user/updateUserImage";

		profile_name = preferences.getString("USER_FULL_NAME", "DefaultName");
		profile_email = preferences.getString("USER_EMAIL", "DefaultEmail");
		profile_mobile = preferences.getString("USER_PHONE", "0123456789");
		profile_userId = preferences.getString("ID", "00");

		txt_edit = (TextView) view.findViewById(R.id.txtprofile_edit);
		edit_name = (EditText) view.findViewById(R.id.editprofile_name);
		edit_email = (EditText) view.findViewById(R.id.editprofile_email);
		edit_mobile = (EditText) view.findViewById(R.id.editprofile_phone);
		edit_password = (EditText) view.findViewById(R.id.editprofile_pass);

		edit_name.setTypeface(tf_edit);
		edit_email.setTypeface(tf_edit);
		edit_mobile.setTypeface(tf_edit);
		edit_password.setTypeface(tf_edit);

		img_profile = (ImageView) view.findViewById(R.id.imgprofile_cover);

		SharedPreferences shre = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		String previouslyEncodedImage = shre.getString("image_data", "");

		if (!previouslyEncodedImage.equalsIgnoreCase("")) {
			byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			img_profile.setImageBitmap(bitmap);
		}

		lay_show_password = (LinearLayout) view
				.findViewById(R.id.layout_password);

		txt_back = (TextView) view.findViewById(R.id.txtprofile_back);
		button_save = ((Button) view.findViewById(R.id.butprofile_save));
		button_save.setTypeface(tf_edit);

		edit_function();
		txt_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (txt_back.getText().toString().equalsIgnoreCase("back")) {

					getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.show(getActivity().getSupportFragmentManager()
									.findFragmentByTag("UserOptionFragment"))
							.commit();
					getActivity().getSupportFragmentManager()
							.popBackStackImmediate();

				} else {
					txt_back.setText("BACK");
					txt_edit.setText("EDIT");
					button_save.setVisibility(View.GONE);
					lay_show_password.setVisibility(View.GONE);

					edit_function();
				}
			}
		});

		txt_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("", "boolen is " + isImageCHanged);
				if (txt_edit.getText().toString().equalsIgnoreCase("edit")) {

					done_function();

				} else if (txt_edit.getText().toString()
						.equalsIgnoreCase("done")) {

					// edit_function();
					// un_done_function();

					button_save.performClick();

				}
			}
		});
		img_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Complete action using"),
						1);
			}
		});

		button_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (button_save.getVisibility() == View.VISIBLE) {

					if (isChecked()) {
						un_done_function();
						edit_function();
						new SendData().execute();
					}

				}

			}
		});

		edit_name.setText(profile_name);
		edit_email.setText(profile_email);
		edit_mobile.setText(profile_mobile);
		float factor = getResources().getDisplayMetrics().density;
		proImgWidth = (int) (150 * factor);
		proImgHeight = (int) (150 * factor);

		if (preferences.contains("imgdata")) {

			img_profile.setImageBitmap(decodeBase64(preferences.getString(
					"imgdata", "")));
		}

		KentValues.isLoading = false;
	}

	@Override
	public void onDestroyView() {
		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"UserOptionFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.show(getActivity().getSupportFragmentManager()
							.findFragmentByTag("UserOptionFragment")).commit();
		}
		super.onDestroyView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {

			Uri selectedImageUri = data.getData();
			imagepath = getPath(selectedImageUri);
			img_profile.setImageBitmap(null);

			img_profile.setImageBitmap(decodeSampledBitmapFromResource(
					imagepath, proImgWidth, proImgHeight));

			isImageCHanged = true;
		}
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public Bitmap decodeSampledBitmapFromResource(String path, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	protected void un_done_function() {
		// TODO Auto-generated method stub
		txt_back.setText("BACK");
		txt_edit.setText("EDIT");
		button_save.setVisibility(View.GONE);
		lay_show_password.setVisibility(View.GONE);
		button_save.setVisibility(View.GONE);
	}

	protected void done_function() {
		// TODO Auto-generated method stub

		txt_back.setText("CANCEL");
		button_save.setVisibility(View.VISIBLE);
		txt_edit.setText("DONE");
		img_profile.setEnabled(true);

		lay_show_password.setVisibility(View.VISIBLE);
		button_save.setVisibility(View.VISIBLE);

		edit_name.setEnabled(true);
		edit_email.setEnabled(true);
		edit_mobile.setEnabled(true);
		edit_password.setEnabled(true);
	}

	private void edit_function() {
		// TODO Auto-generated method stub
		edit_name.setEnabled(false);
		img_profile.setEnabled(false);
		edit_email.setEnabled(false);
		edit_mobile.setEnabled(false);
		edit_password.setEnabled(false);

	}

	private JSONObject margya(String email, String password, String first_name,
			String phone_number, String user_id) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("email", profile_email);
			obj.put("password", profile_password);
			obj.put("first_name", profile_name.trim());
			obj.put("phone_number", profile_mobile);
			obj.put("user_id", profile_userId);

			System.out.print(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private class SendData extends AsyncTask<String, Integer, Void> {

		String res;

		@Override
		protected void onPreExecute() {

			profile_name = edit_name.getText().toString();
			profile_email = edit_email.getText().toString();
			profile_mobile = edit_mobile.getText().toString();
			profile_password = edit_password.getText().toString();

			dialog = new ProgressDialog(getActivity());
			dialog.setCancelable(false);
			dialog.setMessage("Please wait ... ");
			dialog.setIndeterminate(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			String postURL = (url_update);

			HttpPost post = new HttpPost(postURL);
			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
			try {

				JSONObject json = margya(profile_email, profile_password,
						profile_name, profile_mobile, profile_userId);

				Log.e("", "json : " + json.toString());

				try {
					post.setEntity(new StringEntity(json.toString()));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				HttpResponse response = client.execute(post);
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					res = EntityUtils.toString(resEntity);
					Log.i("RESPONSE", res);
					Log.e("", "this is : " + res);

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

			super.onPostExecute(result);

			if (res != null) {

				try {
					JSONObject jsonObject = new JSONObject(res);
					if (jsonObject.has("success")) {
						if (isImageCHanged) {
							isImageCHanged = false;
							try {

								Bitmap bm = decodeSampledBitmapFromResource(
										imagepath, proImgWidth, proImgHeight);
								ByteArrayOutputStream bao = new ByteArrayOutputStream();
								bm.compress(Bitmap.CompressFormat.PNG, 90, bao);
								byte[] ba = bao.toByteArray();
								bao = null;
								Log.e("", "ba length : " + ba.length);
								if (ba.length <= 2000000) {
									ba1 = Base64.encodeToString(ba, 0);
									ba = null;
									new uploadToServer().execute();
								} else {
									getActivity().runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(
													getActivity(),
													"Image size should not be larger then 2 MB",
													Toast.LENGTH_SHORT).show();
										}
									});
								}

							} catch (Exception f) {

								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(getActivity(),
												"Image size is too large",
												Toast.LENGTH_SHORT).show();
									}
								});
							} catch (OutOfMemoryError e) {
								// TODO: handle exception
								Log.e("", "OutOfMemoryError catched");
								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(getActivity(),
												"Image size is too large",
												Toast.LENGTH_SHORT).show();
									}
								});
							}

							Log.e("base64", "-----" + ba1);
						} else {
							closeProFrag();
						}
					}
				} catch (Exception exception) {
				}
			} else {
				isImageCHanged = false;
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), "connection error",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			dialog.cancel();

		}
	}

	boolean isChecked() {
		if (edit_email.getText().toString().equalsIgnoreCase("")) {
			edit_email.setError("Please fill email");
			return false;
		} else if (edit_password.getText().toString().equalsIgnoreCase("")) {
			edit_password.setError("Please fill password");
			return false;
		} else if (edit_name.getText().toString().equalsIgnoreCase("")) {
			edit_name.setError("Please fill name");
			return false;
		} else if (edit_mobile.getText().toString().equalsIgnoreCase("")) {
			edit_mobile.setError("Please fill phone number");
			return false;
		} else {
			return true;
		}

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public class Upload extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {
			dialog2 = new ProgressDialog(getActivity());
			dialog2.setCancelable(false);
			dialog2.setMessage("Please wait ... ");
			dialog2.setIndeterminate(false);
			dialog2.show();

			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... params) {

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog2.dismiss();
		}
	}

	public class uploadToServer extends AsyncTask<Void, Void, String> {

		String response;

		protected void onPreExecute() {
			super.onPreExecute();
			dialog2 = new ProgressDialog(getActivity());
			dialog2.setCancelable(false);
			dialog2.setMessage("Please wait ... ");
			dialog2.setIndeterminate(false);
			dialog2.show();
		}

		@Override
		protected String doInBackground(Void... params) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("base64", ba1));
			nameValuePairs
					.add(new BasicNameValuePair("user_id", profile_userId));
			nameValuePairs.add(new BasicNameValuePair("ImageName", System
					.currentTimeMillis() + ".png"));

			for (int i = 0; i < nameValuePairs.size(); i++) {
				Log.e("", "JJJJ " + i + " : " + nameValuePairs.get(i));
			}

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(upLoadServerUri);
				// httppost.addHeader("Content-type", "application/json");
				// httppost.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse httpResponse = httpclient.execute(httppost);
				int code = httpResponse.getStatusLine().getStatusCode();
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.v("log_tag", "Respons0e code : -" + code);
				Log.v("log_tag", "Response : -" + response);

			} catch (Exception e) {
				Log.v("log_tag", "Error in http connection " + e.toString());
			}
			return response;

		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog2.dismiss();

			if (response != null) {

				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.has("success")) {

						closeProFrag();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), "error in connection",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	public Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public void closeProFrag() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), "Profile updated",
						Toast.LENGTH_SHORT).show();
			}
		});

		if (getActivity().getSupportFragmentManager().findFragmentByTag(
				"FootFragment") != null) {
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.show(getActivity().getSupportFragmentManager()
							.findFragmentByTag("FootFragment")).commit();
		}

		getActivity()
				.getSupportFragmentManager()
				.popBackStack(
						null,
						getActivity().getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);

	}
}