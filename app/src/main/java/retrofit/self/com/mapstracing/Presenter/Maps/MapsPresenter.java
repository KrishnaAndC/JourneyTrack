package retrofit.self.com.mapstracing.Presenter.Maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import retrofit.self.com.mapstracing.Contracts.MapsContract;
import retrofit.self.com.mapstracing.Presenter.BasePresenter;

/**
 * Created by iosdeveloper on 4/30/18.
 */

public class MapsPresenter extends BasePresenter<MapsContract.View> implements MapsContract.MapsPresenterInterface {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;

    LocationListener mLocationListener;

    @Inject
    public MapsPresenter() {
    }

    @Override
    public void startLocationUpdates(LocationManager mLocationManager) {
        if (ActivityCompat.checkSelfPermission(getView().getViewActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getView().getViewActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getView().getViewActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 56);
        }
        if (mLocationListener == null) {
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng currLatLng = new LatLng(latitude, longitude);
                    Log.d("Latitude", String.valueOf(latitude));
                    Log.d("Longitude", String.valueOf(longitude));
                    getView().addMarker(currLatLng);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
        }
    }

    @Override
    public void stopLocationUpdates(LocationManager mLocationManager) {
        if(mLocationManager != null || mLocationListener != null)
        {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager = null;
            mLocationListener = null;
            getView().completeRoute();
        }
    }

    @Override
    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getView().getViewActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        getView().getViewActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        getView().getViewActivity().finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
