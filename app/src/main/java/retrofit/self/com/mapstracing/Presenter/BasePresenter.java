package retrofit.self.com.mapstracing.Presenter;

import retrofit.self.com.mapstracing.View.BaseViewInterface;

/**
 * Created by iosdeveloper on 4/30/18.
 */

public class BasePresenter <V extends BaseViewInterface> implements BasePresenterInterface<V> {

    private V mV;
    @Override
    public void attachView(V view) {
        mV = view;
    }

    @Override
    public void detachView() {
        mV = null;
    }

    @Override
    public boolean isViewAttached() {
        return mV != null;
    }

    public V getView()
    {
        return mV;
    }
}
