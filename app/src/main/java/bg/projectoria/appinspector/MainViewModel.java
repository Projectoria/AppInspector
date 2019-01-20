package bg.projectoria.appinspector;

import java.util.List;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
