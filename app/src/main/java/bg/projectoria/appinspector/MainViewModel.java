package bg.projectoria.appinspector;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    public MainViewModel(Application application) {
        super(application);
    }

    @NonNull
    public LiveData<List<AppStub>> getApps() {
        return App.getRepository().getApps();
    }

}
