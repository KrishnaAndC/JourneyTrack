package retrofit.self.com.mapstracing.View.Maps;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit.self.com.mapstracing.Contracts.MapsContract;
import retrofit.self.com.mapstracing.Presenter.Maps.MapsPresenter;
import retrofit.self.com.mapstracing.R;
import retrofit.self.com.mapstracing.View.BaseActivity;

public class MapsActivity extends BaseActivity<MapsPresenter> implements OnMapReadyCallback, View.OnClickListener, MapsContract.View {

    private static final int GPS_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    @BindView(R.id.startbtn)
    Button startbtn;
    @BindView(R.id.stopbtn)
    Button stopbtn;
    Polyline line;
    SupportMapFragment mapFragment;
    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private GoogleMap mMap;
    private ArrayList<LatLng> points;
    private LocationManager mLocationManager;
    private Marker mPosition;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_maps;
    }

    @Override
    protected void init(@Nullable Bundle state) {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        points = new ArrayList<>();
        stopbtn.setEnabled(false);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkPermissions(true);
        checkEnableGPS();
    }

    private void checkEnableGPS() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getPresenter().buildAlertMessageNoGps();
        }
    }

    @OnClick({R.id.startbtn, R.id.stopbtn})
    //@Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startbtn:
                startbtn.setEnabled(false);
                stopbtn.setEnabled(true);
                if (checkPermissions(true)) {
                    mMap.clear();
                    getPresenter().startLocationUpdates(mLocationManager);
                }
                break;
            case R.id.stopbtn:
                if (checkPermissions(false)) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(false);
                    getPresenter().stopLocationUpdates(mLocationManager);
                }
                startbtn.setEnabled(true);
                stopbtn.setEnabled(false);
                break;
        }
    }

    @Override
    protected void injectDependencies() {
        getActivityComponent().inject(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_PERMISSION_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                //proceedAfterPermission();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, permissionsRequired[1])
                    ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startbtn.setEnabled(true);
                        stopbtn.setEnabled(false);
                        getPresenter().stopLocationUpdates(mLocationManager);
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng bangalore = new LatLng(12.9716, 77.5946);
        mPosition = mMap.addMarker(new MarkerOptions().position(bangalore).title("Bengaluru"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bangalore, 12.0f));
    }

    @Override
    public Activity getViewActivity() {
        return MapsActivity.this;
    }

    private boolean checkPermissions(boolean start) {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MapsActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, permissionsRequired[1])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Need Location Permissions");
                builder.setMessage("This app needs Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startbtn.setEnabled(true);
                        stopbtn.setEnabled(false);
                        getPresenter().stopLocationUpdates(mLocationManager);
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Need Location Permissions");
                builder.setMessage("This app needs Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startbtn.setEnabled(true);
                        stopbtn.setEnabled(false);
                        getPresenter().stopLocationUpdates(mLocationManager);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
            return false;
        } else {
            //You already have the permission, just go ahead.
            return true;
        }
    }


    @Override
    public void completeRoute() {
        mMap.clear();
        PolylineOptions options = new PolylineOptions().width(10).color(Color.GREEN).geodesic(true);
        LatLng point = null;
        setMarker(points.get(0));
        for (int i = 0; i < points.size(); i++) {
            point = points.get(i);
            options.add(point);
        }
        setMarker(points.get(points.size() - 1));
        line = mMap.addPolyline(options);
    }

    @Override
    public void addMarker(LatLng point) {
        if (point != null) {
            points.add(point);
            mMap.clear();
            setMarker(point);
        }
    }

    private void setMarker(LatLng point)
    {
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> addressList = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            String str = addressList.get(0).getLocality();
            str += " " + addressList.get(0).getCountryName();
            mMap.addMarker(new MarkerOptions().position(point).title(str));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 20.0f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
