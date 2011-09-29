package nl.uiterlinden.android.hunted.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HuntedLatLonLocation {

	private double x;

	private double y;

	private static Pattern p = Pattern
			.compile("[N|E]?(^\\d\\d?\\d?) (\\d\\d?)\\.(\\d\\d\\d)");

	private static final Random rnd = new Random(System.nanoTime());

	// random straal rondom coordinaat
	private static final double RANDOM_METERS = 250;
	
	public HuntedLatLonLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/*
	 * Convert a coordinate from a coordinate in degrees to a digital coordinate
	 * 
	 * The following formats are allowed, zeros may be omited at degrees and
	 * minutes, never at miliseconds:
	 * 
	 * dd mm.mmm (eg: 52 15.009) dd m.mmm (eg: 52 9.123) ddd mm.mmm (eg: 008
	 * 15.923) ddd m.mmm (eg: 006 8.879) d m.mmm (eg: 8 6.887) d mm.mmm (eg: 8
	 * 17.345)
	 * 
	 * @param aCoordinateInDegrees in the following format: "52 15.012" so "ddd
	 * mm.mmm" @return
	 */
	private static double convertDegreesToDigital(String aCoordinateInDegrees) {
		Matcher m = p.matcher(aCoordinateInDegrees.toUpperCase().trim());
		if (!m.matches()) {
			throw new IllegalArgumentException(aCoordinateInDegrees
					+ " is not a valid coordinate in degrees.");
		}

		double degrees = Double.parseDouble(m.group(1));
		double minutes = Double.parseDouble(m.group(2)) / 60;
		double miliseconds = (Double.parseDouble(m.group(3)) / 1000) / 60;

		double result = degrees + minutes + miliseconds;

		return Math.round(result * 1000000) / 1000000D;
	}

	private static Pattern latlonlocat = Pattern
			.compile("^N?(\\d\\d?\\d? \\d\\d?\\.\\d\\d\\d) E?(\\d\\d?\\d? \\d\\d?\\.\\d\\d\\d)$");

	public static HuntedLatLonLocation parseWGS84DegreesLatLon(String aDegreesLatLon) {
		Matcher m = latlonlocat.matcher(aDegreesLatLon.toUpperCase().trim());

		if (!m.matches()) {
			throw new HuntedException(aDegreesLatLon
					+ " is geen geldig coordinaat");
		}

		double x = HuntedLatLonLocation.convertDegreesToDigital(m.group(1));
		double y = HuntedLatLonLocation.convertDegreesToDigital(m.group(2));

		HuntedLatLonLocation loc = new HuntedLatLonLocation(x, y);

		return loc;
	}

	/**
	 * The following formats are allowed, zeros may be omited at degrees and
	 * minutes, never at miliseconds:
	 * 
	 * dd mm.mmm (eg: 52 15.009) 
	 * dd m.mmm (eg: 52 9.123) 
	 * ddd mm.mmm (eg: 008 15.923) 
	 * ddd m.mmm (eg: 006 8.879) 
	 * d m.mmm (eg: 8 6.887) 
	 * d mm.mmm (eg: 8 17.345)
	 */
	public static boolean isValidDegreesCoordinate(String aCoordinateInDegrees) {
		Matcher m = p.matcher(aCoordinateInDegrees);

		return m.matches();
	}

	public double distance(HuntedLatLonLocation aLocation) {

		double diff_x = aLocation.getX() - x;
		double diff_y = aLocation.getY() - y;
		
		// stelling van pythagoras
		return Math.sqrt(Math.pow(diff_x, 2) + Math.pow(diff_y, 2));
	}
	
	/*
	 * Radius in meters
	 */
	public boolean isLocationWithinRadius(HuntedLatLonLocation aLocation, double radius)
	{
		return distance(aLocation) <= radius;
	}

	public double bearing(HuntedLatLonLocation aLocation) {

		double diff_x = aLocation.getX() - x;
		double diff_y = aLocation.getY() - y;

		// prevent exception divide by zero
		if (diff_x == 0) {
			if (diff_y == 0) {
				return 0;
			}
			if (diff_y > 0) {
				return 0;
			}

			return 180;
		}

		if (diff_x > 0) {
			return 90 - Math
					.toDegrees(Math.atan((diff_y) / (Math.abs(diff_x))));
		} else {
			return 270 + Math.toDegrees(Math
					.atan((diff_y) / (Math.abs(diff_x))));
		}
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}