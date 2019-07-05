package bg.projectoria.appinspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class Repository {

    private final @NonNull Application application;
    private final @NonNull MutableLiveData<List<AppStub>> apps = new MutableLiveData<>();
    private boolean loadInitiated;

    @MainThread
    public Repository(@NonNull Application application) {
        this.application = application;
        this.apps.setValue(Collections.emptyList());
    }

    @MainThread
    @NonNull
    public LiveData<List<AppStub>> getApps() {
        if (!loadInitiated) {
            loadInitiated = true;
            load();
        }
        return apps;
    }

    @MainThread
    private void load() {
        new LoadTask(application, apps).execute();
    }

    private static class LoadTask extends AsyncTask<Void, Void, List<AppStub>> {

        private static final String TAG = "LoadTask";

        private final @NonNull Application application;

        private final @NonNull MutableLiveData<List<AppStub>> commitResult;

        LoadTask(
                @NonNull Application application,
                @NonNull MutableLiveData<List<AppStub>> commitResult) {

            this.application = application;
            this.commitResult = commitResult;
        }

        @Override
        @WorkerThread
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
        @MainThread
        protected void onPostExecute(List<AppStub> appStubs) {
            commitResult.setValue(appStubs);
        }
    }
}
