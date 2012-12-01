package com.quardroiders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapMarkerActivity extends MapActivity{

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private int lat, lng;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		lng = (int) (Double.parseDouble(bundle.getString("lat")) * 1E6);
		lat = (int) (Double.parseDouble(bundle.getString("lng")) * 1E6);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		mapController = mapView.getController();
		mapController.setZoom(14);
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
		GeoPoint point = new GeoPoint(lat, lng);
		Point deviation = new Point(-15,-36); 
		Marker marker = new Marker(point,bmp,deviation);  
		mapView.getOverlays().add(marker);
		mapController.animateTo(point);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	class Marker extends Overlay {
		private GeoPoint point = null;
	    private Bitmap bmp = null;
	    private Point deviation = null; 
	    
		public Marker(GeoPoint point, Bitmap bmp, Point deviation) {  
	        this.point = point;  
	        this.bmp = bmp;  
	        this.deviation = deviation;  
	    } 
		
		@Override  
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) {  
	        if (!shadow) {
	            Projection projection = mapView.getProjection();  
	            if (point != null && bmp != null) {  
	                Point pos = projection.toPixels(point, null);  
	                canvas.drawBitmap(bmp, pos.x + deviation.x, pos.y + deviation.y, null);  
	            }     
	        }  
	    }
		
	}

}
