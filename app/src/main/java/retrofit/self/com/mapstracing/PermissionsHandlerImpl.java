package retrofit.self.com.mapstracing;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

/**
 * Created by iosdeveloper on 5/7/18.
 */

public class PermissionsHandlerImpl implements PermissionsHandler {

    @Inject
    public PermissionsHandlerImpl() {
    }

    @Override
    public boolean checkHasPermission(AppCompatActivity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermission(AppCompatActivity activity, String[] permissions, int requestCode){
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
