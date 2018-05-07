package retrofit.self.com.mapstracing.Utils.Dagger;

import javax.inject.Singleton;

import dagger.Component;
import retrofit.self.com.mapstracing.View.Maps.MapsActivity;

/**
 * Created by iosdeveloper on 4/30/18.
 */
@Singleton
@Component(modules = {ActivityModule.class})
public interface ActivityComponent {


    void inject(MapsActivity obj);
}


