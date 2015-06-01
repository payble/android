package netleon.sansar.kent.network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class GetData {

	public String postDataToServer(String url, String data) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.addHeader("Content-type", "application/json");
		post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
		Log.e("", "Posting data is : " + data);

		String response = null;

		try {
			post.setEntity(new StringEntity(data));
			HttpResponse httpResponse = httpClient.execute(post);
			HttpEntity httpEntity = httpResponse.getEntity();

			if (httpEntity != null) {
				response = EntityUtils.toString(httpEntity);
				Log.i("RESPONSE : ", response);
			}

		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return response;
	}

	public String getDataFromServer(String url) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-type", "application/json");
		get.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
		String response = null;

		try {
			HttpResponse httpResponse = httpClient.execute(get);
			HttpEntity httpEntity = httpResponse.getEntity();

			if (httpEntity != null) {
				response = EntityUtils.toString(httpEntity);
				Log.i("RESPONSE : ", response);
			}
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return response;
	}

	public String putDataToServer(String url, String data) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPut post = new HttpPut(url);
		post.addHeader("Content-type", "application/json");
		post.addHeader("Authorization", "aDRF@F#JG_a34-n3d");
		Log.e("", "Posting data is : " + data);

		String response = null;

		try {
			post.setEntity(new StringEntity(data));
			HttpResponse httpResponse = httpClient.execute(post);
			HttpEntity httpEntity = httpResponse.getEntity();

			if (httpEntity != null) {
				response = EntityUtils.toString(httpEntity);
				Log.i("RESPONSE : ", response);
			}

		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return response;
	}

	public int uploadFile(String sourceFileUri, String URL) {

		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);
		int response_code = 007;

		if (!sourceFile.isFile()) {

			return 0;

		} else {
			try {

				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(URL);

				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				conn.setRequestProperty("Content-type", "application/json");
				conn.setRequestProperty("Authorization", "aDRF@F#JG_a34-n3d");

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ "uploaded_file" + "\"" + lineEnd);

				System.out.println("this is file name" + fileName);

				dos.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseMessage);

				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				// dialog2.dismiss();
				// ex.printStackTrace();
				//
				// getActivity().runOnUiThread(new Runnable() {
				// public void run() {
				// Toast.makeText(getActivity(), "MalformedURLException",
				// Toast.LENGTH_SHORT).show();
				// }
				// });

				// Log.e("Upload file to server", "error: " + ex.getMessage(),
				// ex);
			} catch (Exception e) {

				e.printStackTrace();

				// getActivity().runOnUiThread(new Runnable() {
				// public void run() {
				// Toast.makeText(
				// getActivity(),
				// "Internal Server Error... Please Try Aftersome Time ",
				// Toast.LENGTH_SHORT).show();
				// }
				// });
				// Log.e("Upload file to server Exception",
				// "Exception : " + e.getMessage(), e);
			}
			try {
				response_code = conn.getResponseCode();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response_code;
		}
	}
}
