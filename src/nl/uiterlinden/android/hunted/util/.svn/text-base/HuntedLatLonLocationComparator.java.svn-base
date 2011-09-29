package nl.uiterlinden.android.hunted.util;

import java.util.Comparator;

public class HuntedLatLonLocationComparator implements Comparator<HuntedLatLonLocation> {
	private HuntedLatLonLocation center;
	public HuntedLatLonLocationComparator(HuntedLatLonLocation aCenterLocation) {
		center = aCenterLocation;
	}
	
	public int compare(HuntedLatLonLocation o1, HuntedLatLonLocation o2) {
		double distanceo1 = center.distance(o1);
		double distanceo2 = center.distance(o2);
		
		if (distanceo1 == distanceo2) {
			return 0;
		}
		
		if (distanceo1 > distanceo2) {
			return 1;
		}
		
		return -1;
	}	
}
