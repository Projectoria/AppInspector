package bg.projectoria.appinspector;

import android.app.Application;

public class App extends Application {

    private static Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new Repository(this);
    }

    public static Repository getRepository() {
        return repository;
    }
}
