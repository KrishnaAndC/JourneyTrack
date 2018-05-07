package retrofit.self.com.mapstracing;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by iosdeveloper on 5/7/18.
 */

public interface PermissionsHandler {

    boolean checkHasPermission(AppCompatActivity activity, String permission);
    void requestPermission(AppCompatActivity activity, String[] permissions, int requestCode);
}
