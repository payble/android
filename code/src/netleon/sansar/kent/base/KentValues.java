package netleon.sansar.kent.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class KentValues {

	public static int width;
	public static int height;
	public static float screenDensity;
	public static Boolean isLoading = false;
	
	public static LatLng myLocation;

	public static Map<Marker, Restaurant> markerMap = new HashMap<Marker, Restaurant>();
	public static CatgorizedRestos catgorizedRestos = new CatgorizedRestos();
	public static ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	public static ArrayList<SearchKey> KEYS = new ArrayList<SearchKey>();
	public static List<SearchVal> price_list = new ArrayList<SearchVal>();

}
