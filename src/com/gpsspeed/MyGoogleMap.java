package com.gpsspeed;

//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List; 


import android.app.AlertDialog;
import android.content.Context; 
import android.content.DialogInterface;
import android.content.Intent; 
//import android.graphics.drawable.Drawable;
import android.location.Address; 
import android.location.Criteria; 
import android.location.Geocoder; 
import android.location.Location; 
import android.location.LocationListener; 
import android.location.LocationManager; 
import android.os.Bundle; 
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View; 
import android.widget.Button; 
import android.widget.EditText; 
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import com.google.android.maps.GeoPoint; 
//import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity; 
import com.google.android.maps.MapController; 
import com.google.android.maps.MapView; 
//import com.google.android.maps.Overlay;
//import com.google.android.maps.OverlayItem;

public class MyGoogleMap extends MapActivity 
{ 
  private static final int MSG_DIALOG_SAFE = 1;  
  private static final int MSG_DIALOG_OVERRANGE = 2;  
  
  private static final int MENU_TRACK = Menu.FIRST;
  private static final int MENU_EXIT = Menu.FIRST +1 ;
  
  private String TAG = "mapspeed";
  
  static public MyGoogleMap my;
  
  private MyGoogleMap mMyGoogleMap = this;
  private Location mLocation01; 

  private Location preLocation;
  private long preTime;
  private static final int step=5000;
  private LocationManager locationManager;
  private boolean status=false;
  
  private MapController mMapController01; 
  private MapView mMapView; 
  
  private MyOverLay overlay;
  private List<MapLocation> mapLocations;
  private String strLocationProvider = ""; 

  private Button mButton01,mButton02,mButton03,mButton04,mButton05;
  private int intZoomLevel=0;//geoLatitude,geoLongitude; 
  public GeoPoint nowGeoPoint;
  
  private String speed;
  
  public static  MapLocation mSelectedMapLocation;  
  public boolean mshow;
   
  public TextView label;
  
  @Override 
  protected void onCreate(Bundle icicle) 
  { 
    // TODO Auto-generated method stub 
    super.onCreate(icicle); 
    setContentView(R.layout.main2); 

    my = this;

    //googleMAP
    mMapView = (MapView)findViewById(R.id.myMapView1); 
    mMapController01 = mMapView.getController(); 

    //訊息顯示
    label = (TextView) findViewById(R.id.cstaus);
    
    //參數設定 
    mMapView.setSatellite(false);
    mMapView.setStreetView(true);
    mMapView.setEnabled(true);
    mMapView.setClickable(true);
     
    intZoomLevel = 15; 
    mMapController01.setZoom(intZoomLevel); 

    speed = "30";
    mshow = false;
    
    //顯示輸入IP的windows
    final EditText input = new EditText(mMyGoogleMap);
    input.setText(speed);
    AlertDialog.Builder alert = new AlertDialog.Builder(mMyGoogleMap);

    //openOptionsDialog(getLocalIpAddress());
    
    alert.setTitle("設定speed");
    alert.setMessage("請輸入speed");
    
    // Set an EditText view to get user input 
    alert.setView(input);
    
    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int whichButton) 
    {
      try
      {
        speed = input.getText().toString();  
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      //mMapController01.setCenter(getMapLocations(true).get(0).getPoint());
    }
    });

    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        // Canceled.
      }
    });

    alert.show();      
    
    //mLocationManager01.requestLocationUpdates 
    //(strLocationProvider, 2000, 10, mLocationListener01); 

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
    
    getLocationProvider();     
    nowGeoPoint = getGeoByLocation(mLocation01); 
    
    if (nowGeoPoint != null)
    {
      refreshMapViewByGeoPoint(nowGeoPoint, 
          mMapView, intZoomLevel); 
      updateWithNewLocation(mLocation01);
    }

    locationManager.requestLocationUpdates(strLocationProvider, 0, 0, locationListener); 
    
    preTime = System.currentTimeMillis();
    getMapLocations(true);
    //建構畫在GoogleMap的overlay
    overlay = new MyOverLay(this);
    mMapView.getOverlays().add(overlay);
    //mMapController01.setCenter(getMapLocations(true).get(0).getPoint());    

   
    //放大地圖
    mButton02 = (Button)findViewById(R.id.myButton2); 
    mButton02.setOnClickListener(new Button.OnClickListener() 
    { 
      public void onClick(View v) 
      { 
        // TODO Auto-generated method stub 
        intZoomLevel++; 
        if(intZoomLevel>mMapView.getMaxZoomLevel()) 
        { 
          intZoomLevel = mMapView.getMaxZoomLevel(); 
        } 
        mMapController01.setZoom(intZoomLevel); 
      } 
    }); 
     
    //縮小地圖
    mButton03 = (Button)findViewById(R.id.myButton3); 
    mButton03.setOnClickListener(new Button.OnClickListener() 
    { 
      public void onClick(View v) 
      { 
        // TODO Auto-generated method stub 
        intZoomLevel--; 
        if(intZoomLevel<1) 
        { 
          intZoomLevel = 1; 
        } 
        mMapController01.setZoom(intZoomLevel); 
      } 
    });

    //Satellite或街道
    mButton04 = (Button)findViewById(R.id.myButton4); 
    mButton04.setOnClickListener(new Button.OnClickListener() 
    { 
      public void onClick(View v) 
      { 
        // TODO Auto-generated method stub
       String str = mButton04.getText().toString();
        
       if (str.equals("衛星"))
       {
        mButton04.setText("街道");
        mMapView.setStreetView(false);
        mMapView.setSatellite(true);
        mMapView.setTraffic(false);
       }
       else
       {
         mButton04.setText("衛星");
         mMapView.setStreetView(true);
         mMapView.setSatellite(false);
         mMapView.setTraffic(false);
       }
      } 
    }); 


    Log.v("speed", speed);
    
  }
  
  private GeoPoint getGeoByLocation(Location location) 
  { 
    GeoPoint gp = null; 
    try 
    { 
      if (location != null) 
      { 
        double geoLatitude = location.getLatitude()*1E6; 
        double geoLongitude = location.getLongitude()*1E6; 
        gp = new GeoPoint((int) geoLatitude, (int) geoLongitude); 
      } 
    } 
    catch(Exception e) 
    { 
      e.printStackTrace(); 
    } 
    return gp; 
  } 
  
  private final LocationListener locationListener = new LocationListener() {

    public void onLocationChanged(Location location) 
    {
      mLocation01 = location; 
      nowGeoPoint = getGeoByLocation(location); 

      if (nowGeoPoint != null)
      {
        refreshMapViewByGeoPoint(nowGeoPoint, 
            mMapView, intZoomLevel); 
      }      
      
      updateWithNewLocation(location);
    }
    
    public void onProviderDisabled(String provider) 
    { 
      // TODO Auto-generated method stub 
    } 
     
    public void onProviderEnabled(String provider) 
    { 
      // TODO Auto-generated method stub 
       
    } 
     
    public void onStatusChanged(String provider, 
                int status, Bundle extras) 
    { 
      // TODO Auto-generated method stub 
       
    } 
  }; 
    
  public List<MapLocation> getMapLocations(boolean doit) 
  {
    if (mapLocations == null || doit == true) 
    {
      mapLocations = new ArrayList<MapLocation>();
    }
    return mapLocations;
  }
  
  private void updateWithNewLocation(Location location) 
  {
    String latLongString;
    if (location != null) 
    {
      long subTime=(System.currentTimeMillis()-preTime)/1000;
      
      //float v= (subTime==0 || preLocation==null)?0:(preLocation.distanceTo(location)/subTime);
      
      latLongString = location.getSpeed() * 3.6 + " km/h";

      preLocation=location;
      preTime=System.currentTimeMillis();
    } 
    else {
      latLongString = "nGPS";
    }
    
    Log.v(TAG, latLongString);
    label.setText(latLongString);
}
 
  //更新現在位置
  public static void refreshMapViewByGeoPoint 
  (GeoPoint gp, MapView mapview, int zoomLevel) 
  { 
    try 
    { 
      mapview.displayZoomControls(true); 
      MapController myMC = mapview.getController(); 
      myMC.animateTo(gp); 
      myMC.setZoom(zoomLevel); 
      //mapview.setSatellite(false);
      
    } 
    catch(Exception e) 
    { 
      e.printStackTrace(); 
    } 
  }
  
  
  public void getLocationProvider() 
  { 
    try 
    { 
      Criteria mCriteria01 = new Criteria(); 
      mCriteria01.setAccuracy(Criteria.ACCURACY_FINE); 
      mCriteria01.setAltitudeRequired(false); 
      mCriteria01.setBearingRequired(false); 
      mCriteria01.setCostAllowed(true); 
      mCriteria01.setPowerRequirement(Criteria.POWER_LOW); 
      strLocationProvider = locationManager.getBestProvider(mCriteria01, true); 
       
      mLocation01 = locationManager.getLastKnownLocation (strLocationProvider);
    } 
    catch(Exception e) 
    { 
      //mTextView01.setText(e.toString()); 
      e.printStackTrace(); 
    } 
  }
  
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    
    menu.add(0 , MENU_TRACK, 0 ,R.string.msg_tracker).setIcon(R.drawable.mappin_red)
    .setAlphabeticShortcut('S');
    menu.add(0 , MENU_EXIT, 0 ,R.string.msg_exit).setIcon(R.drawable.exit)
    .setAlphabeticShortcut('S');
    
    return true;  
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
      { 
        case MENU_TRACK:
          return true;
        case MENU_EXIT:
          locationManager.removeUpdates(locationListener);
          finish();     
          return true;
      }
    
  return true ;
  }
  
  
   
  @Override 
  protected boolean isRouteDisplayed() 
  { 
    // TODO Auto-generated method stub 
    return false; 
  } 
  
  public Handler myHandler = new Handler(){
    public void handleMessage(Message msg) {
        switch(msg.what)
        {
          case MSG_DIALOG_SAFE:
                break;
          case MSG_DIALOG_OVERRANGE:
                break;
          default:
                label.setText(Integer.toString(msg.what));
        }
        super.handleMessage(msg);
    }
};  

  //show message
  public void openOptionsDialog(String info)
  {
    new AlertDialog.Builder(this)
    .setTitle("message")
    .setMessage(info)
    .setPositiveButton("OK",
        new DialogInterface.OnClickListener()
        {
         public void onClick(DialogInterface dialoginterface, int i)
         {
           mshow = false;
         }
         }
        )
    .show();
  }
}
