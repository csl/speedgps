package com.gpsspeed;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Environment;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class MyOverLay  extends Overlay {

	/**
	 * Stored as global instances as one time initialization is enough
	 */
    private Bitmap mBubbleIcon, mShadowIcon;
    
    private Bitmap mNowIcon;
    
    private MyGoogleMap mLocationViewers;
    
    private List<GeoPoint> gp;
	
    
    private ArrayList<GeoPoint> tracker; 

    private int mRadius=6;
    
	/**
	 * It is used to track the visibility of information window and clicked location is known location or not 
	 * of the currently selected Map Location
	 */
    
  //�غc�l, ��l��
	public MyOverLay(MyGoogleMap mLocationViewers) {
		
		this.mLocationViewers = mLocationViewers;
		
		mBubbleIcon = BitmapFactory.decodeResource(mLocationViewers.getResources(),R.drawable.bubble);
		mShadowIcon = BitmapFactory.decodeResource(mLocationViewers.getResources(),R.drawable.shadow);
		mNowIcon = BitmapFactory.decodeResource(mLocationViewers.getResources(),R.drawable.mappin_blue);
		
		tracker = new ArrayList<GeoPoint>();
	}
	
  public boolean addGeoPoint(GeoPoint p)
  {
      if (p != null)
      {
        tracker.add(p);
        return true;
      }     
      
      return false;
  }

  public GeoPoint getGeoPoint(int index)
  {
      return tracker.get(index);
  }

	@Override
  //�B�zdraw map�W���Ϯ�
	public boolean onTap(GeoPoint p, MapView mapView)  
	{

		/**
		 *   Return true if we handled this onTap()
		 */
		return true;
	}
	
  @Override
  //draw method  
	public void draw(Canvas canvas, MapView	mapView, boolean shadow) 
  {
      //�e�{�b��m
      drawNowGeoMap(canvas, mapView, shadow);
      //�e�a�Ϯy��
      drawTracker(canvas, mapView, shadow);
  }
    
  //�M��GPS Range�y��
  public void clearRange()
  {
    gp.clear();
  }

  private void drawNowGeoMap(Canvas canvas, MapView mapView, boolean shadow) 
  {
      //��ܲ{�b��m
      if (mLocationViewers.nowGeoPoint != null)
      {
        Paint paint = new Paint();
        Point myScreenCoords = new Point();
        
        //��ܲ{�b��m  
        mapView.getProjection().toPixels(mLocationViewers.nowGeoPoint, myScreenCoords);
        paint.setStrokeWidth(1);
        paint.setARGB(255, 255, 0, 0);
        paint.setStyle(Paint.Style.STROKE);
  
        canvas.drawBitmap(mNowIcon, myScreenCoords.x, myScreenCoords.y, paint);
        canvas.drawText("�{�b��m", myScreenCoords.x, myScreenCoords.y, paint);
      }
    }
    
    private void drawTracker(Canvas canvas, MapView mapView, boolean shadow)
    {
      if (tracker.size() == 0 ) return;
      
      if (shadow == false) 
      {      
        Projection projection = mapView.getProjection();
        
        Paint paint = new Paint(); 
        paint.setAntiAlias(true); 
        paint.setColor(Color.BLACK);
        
        //Start
        Point spoint = new Point(); 
        projection.toPixels(tracker.get(0), spoint);
        RectF startoval = new RectF(spoint.x - mRadius, spoint.y - mRadius,  
                               spoint.x + mRadius, spoint.y + mRadius); 
        canvas.drawOval(startoval, paint);
        
        //EndPoint
        Point endpoint = new Point(); 
        projection.toPixels(tracker.get(tracker.size()-1), endpoint);
        RectF endoval = new RectF(endpoint.x - mRadius, endpoint.y - mRadius,  
                          endpoint.x + mRadius, endpoint.y + mRadius); 
        paint.setAlpha(255);
        canvas.drawOval(endoval, paint);
        }      
    }

}