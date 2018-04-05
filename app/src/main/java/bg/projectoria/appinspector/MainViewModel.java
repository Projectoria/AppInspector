package bg.projectoria.appinspector;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private MutableLiveData<List<AppStub>> apps;

    public MainViewModel(Application application) {
        super(application);
    }

    @NonNull
    public LiveData<List<AppStub>> get() {
        if (apps == null) {
            apps = new MutableLiveData<>();
            load();
        }
        return apps;
    }

    private void load() {
        new LoadTask(this.getApplication(), apps).execute();
    }

    private static class LoadTask extends AsyncTask<Void, Void, List<AppStub>> {

        private static final String TAG = "LoadTask";

        private final Application application;

        private final MutableLiveData<List<AppStub>> commitResult;

        LoadTask(
                @NonNull Application application,
                @NonNull MutableLiveData<List<AppStub>> commitResult) {

            this.application = application;
            this.commitResult = commitResult;
        }

        @Override
        protected List<AppStub> doInBackground(Void... voids) {
            Log.d(TAG,"Start doInBackground...");

            List<AppStub> result = new ArrayList<>();

            PackageManager pman = application.getPackageManager();
            List<ApplicationInfo> appInfos = pman.getInstalledApplications(0);

            for (ApplicationInfo appInfo : appInfos) {
                CharSequence label = pman.getApplicationLabel(appInfo);
                Drawable icon = pman.getApplicationIcon(appInfo);

                result.add(new AppStub(appInfo.packageName, label, icon));
            }

            Collections.sort(result, AppStub.LABEL_COMPARATOR);

            Log.d(TAG, "Finish doInBackground");
            return Collections.unmodifiableList(result);
        }

        @Override
        protected void onPostExecute(List<AppStub> appStubs) {
            commitResult.setValue(appStubs);
        }
    }
}
