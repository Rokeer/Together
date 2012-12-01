public class Distance {

	/**
	 * @param args
	 */

	private double lat1, lng1, lat2, lng2;

	public Distance(double lat1, double lng1, double lat2, double lng2) {
		this.lat1 = lat1 * Math.PI / 180;
		this.lng1 = lng1 * Math.PI / 180;
		this.lat2 = lat2 * Math.PI / 180;
		this.lng2 = lng2 * Math.PI / 180;
	}

	public double getDistance() {
		// double delta = Math.abs(lng2 - lng1);
		// double R = 6378137; // Radius of equator
		double R = 6371004; // The average radius for a spherical approximation
							// of the figure of the Earth
		double delta = lng2 - lng1;
		double cosLat1 = Math.cos(lat1);
		double cosLat2 = Math.cos(lat2);
		double cosDelta = Math.cos(delta);
		double sinLat1 = Math.sin(lat1);
		double sinLat2 = Math.sin(lat2);
		double sinDelta = Math.sin(delta);

		double x = Math.pow(cosLat2 * sinDelta, 2);
		double y = Math
				.pow(cosLat1 * sinLat2 - sinLat1 * cosLat2 * cosDelta, 2);
		double z = sinLat1 * sinLat2 + cosLat1 * cosLat2 * cosDelta;

		return Math.atan(Math.sqrt(x + y) / z) * R;

		// return Math.acos(sinLat1*sinLat2 + cosLat1*cosLat2*cosDelta) * R;
	}

	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Distance distance = new Distance(47.272691666666666666666666666667,
				132.02044611111111111111111111111,
				47.272693888888888888888888888889,
				132.02041916666666666666666666667);
		double d = distance.getDistance();
		System.out.println(d);
	}
	*/

}