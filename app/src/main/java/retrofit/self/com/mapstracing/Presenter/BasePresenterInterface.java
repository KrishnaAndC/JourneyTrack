package retrofit.self.com.mapstracing.Presenter;

import retrofit.self.com.mapstracing.View.BaseViewInterface;

/**
 * Created by iosdeveloper on 4/30/18.
 */

public interface BasePresenterInterface <V extends BaseViewInterface> {

    void attachView(V view);

    void detachView();

    boolean isViewAttached();

}
