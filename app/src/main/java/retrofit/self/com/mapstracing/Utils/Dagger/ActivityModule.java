package retrofit.self.com.mapstracing.Utils.Dagger;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import retrofit.self.com.mapstracing.Contracts.MapsContract;
import retrofit.self.com.mapstracing.Presenter.Maps.MapsPresenter;

/**
 * Created by iosdeveloper on 4/30/18.
 */
@Module
public class ActivityModule {

    private Context mContext;

    public ActivityModule(Context context) {
        mContext = context;
    }

    @Provides
    public MapsContract.MapsPresenterInterface providesMainPresenter() {
        return new MapsPresenter();
    }

}
