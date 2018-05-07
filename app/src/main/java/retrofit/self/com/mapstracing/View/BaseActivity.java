package retrofit.self.com.mapstracing.View;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import retrofit.self.com.mapstracing.Presenter.BasePresenterInterface;
import retrofit.self.com.mapstracing.Utils.Dagger.ActivityComponent;
import retrofit.self.com.mapstracing.Utils.Dagger.ActivityModule;
import retrofit.self.com.mapstracing.Utils.Dagger.DaggerActivityComponent;

/**
 * Created by iosdeveloper on 4/30/18.
 */

public abstract class BaseActivity<T extends BasePresenterInterface> extends AppCompatActivity implements BaseViewInterface {


    @Inject
    T mPresenter;

    ActivityComponent mActivityComponent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);

        mActivityComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build();
        injectDependencies();
        mPresenter.attachView(this);
        init(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    /**
     * Getter for the presenter
     *
     * @return the present for the activity
     */
    public T getPresenter() {
        return mPresenter;
    }

    /**
     * Layout resource to be inflated
     *
     * @return layout resource
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    /**
     * Initializations
     */
    protected abstract void init(@Nullable Bundle state);

    /**
     * Injecting dependencies
     */
    protected abstract void injectDependencies();
}
