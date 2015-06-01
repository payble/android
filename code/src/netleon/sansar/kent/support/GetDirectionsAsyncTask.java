package netleon.sansar.kent.support;

import java.util.ArrayList;
import java.util.Map;

import netleon.sansar.kent.Perspective;
import netleon.sansar.kent.base.DirectionResponse;
import netleon.sansar.kent.base.KentValues;

import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class GetDirectionsAsyncTask extends
		AsyncTask<Map<String, String>, Object, ArrayList<LatLng>> {
	public static final String USER_CURRENT_LAT = "user_current_lat";
	public static final String USER_CURRENT_LONG = "user_current_long";
	public static final String DESTINATION_LAT = "destination_lat";
	public static final String DESTINATION_LONG = "destination_long";
	public static final String DIRECTIONS_MODE = "directions_mode";
	ProgressDialog proDialog;

	private Exception exception;

	Context context;

	private Polyline newPolyline;

	public DirectionResponse response;

	public GetDirectionsAsyncTask(Context context) {
		this.context = context;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		proDialog = new ProgressDialog(context);
		proDialog.setMessage("Calculating directions");
		proDialog.setIndeterminate(false);
		proDialog.setCancelable(false);
		proDialog.show();
	}

	@Override
	public void onPostExecute(ArrayList result) {
		proDialog.dismiss();
		Log.d("", "res size is : " + result.size());
		if (exception == null) {
			try {
				handleGetDirectionsResult(result);
			} catch (Exception e) {
				Toast.makeText(context, "No routes found", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			processException();
		}
	}

	@Override
	protected ArrayList<LatLng> doInBackground(Map<String, String>... params) {
		Map<String, String> paramMap = params[0];
		try {
			LatLng fromPosition = new LatLng(Double.valueOf(paramMap
					.get(USER_CURRENT_LAT)), Double.valueOf(paramMap
					.get(USER_CURRENT_LONG)));
			LatLng toPosition = new LatLng(Double.valueOf(paramMap
					.get(DESTINATION_LAT)), Double.valueOf(paramMap
					.get(DESTINATION_LONG)));
			// Log.e("", "USER_CURRENT_LAT"+Double.valueOf(paramMap
			// .get(USER_CURRENT_LAT)));
			// Log.e("", "USER_CURRENT_LONG"+Double.valueOf(paramMap
			// .get(USER_CURRENT_LONG)));
			// Log.e("", "DESTINATION_LAT"+Double.valueOf(paramMap
			// .get(DESTINATION_LAT)));
			// Log.e("", "DESTINATION_LONG"+Double.valueOf(paramMap
			// .get(DESTINATION_LONG)));
			GMapV2Direction md = new GMapV2Direction();
			Document doc = md.getDocument(fromPosition, toPosition,
					paramMap.get(DIRECTIONS_MODE));
			ArrayList<LatLng> directionPoints = md.getDirection(doc);
			Log.d("", "back size is : " + directionPoints.size());
			return directionPoints;
		} catch (Exception e) {
			exception = e;
			Log.e("map error", ":" + e.toString());
			return null;
		}
	}

	private void processException() {
		Toast.makeText(context, "Connection not found", 3000).show();
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(10).color(
				Color.BLUE);

		Log.d("", "sise is : " + directionPoints.size());

		for (int i = 0; i < directionPoints.size(); i++) {
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null) {
			newPolyline.remove();
		}
		newPolyline = Perspective.map.addPolyline(rectLine);
		response.processFinish(newPolyline);
		LatLngBounds latlngBounds = createLatLngBoundsObject(
				directionPoints.get(0), directionPoints.get(1));
		Perspective.map.animateCamera(CameraUpdateFactory.newLatLngBounds(
				latlngBounds, KentValues.width, KentValues.height, 150));

	}

	private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
			LatLng secondLocation) {
		if (firstLocation != null && secondLocation != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(firstLocation).include(secondLocation);
			return builder.build();
		}
		return null;
	}

}