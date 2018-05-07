package retrofit.self.com.mapstracing.Contracts;

import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import retrofit.self.com.mapstracing.Presenter.BasePresenterInterface;
import retrofit.self.com.mapstracing.View.BaseViewInterface;

/**
 * Created by iosdeveloper on 4/30/18.
 */

public interface MapsContract {

    interface MapsPresenterInterface extends BasePresenterInterface<View> {

        void startLocationUpdates(LocationManager mLocationManager);

        void stopLocationUpdates(LocationManager mLocationManager);

        void buildAlertMessageNoGps();
    }

    interface View extends BaseViewInterface {
        void addMarker(LatLng point);

        void completeRoute();
    }
}
