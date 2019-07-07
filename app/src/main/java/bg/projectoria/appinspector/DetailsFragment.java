package bg.projectoria.appinspector;

import java.util.ArrayList;
import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 * A fragment representing a single App detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailsActivity}
 * on handsets.
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    public static final String PACKAGE_NAME = "package_name";

    private static class Model {
        ApplicationInfo app;
        PackageInfo pkg;
        String appLabel;
    }

    private ClipboardManager clipboard;
    private Context appCtx;
    private Model model;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailsFragment() {
    }

    public static @NonNull DetailsFragment make(@NonNull String packageName) {
        DetailsFragment result = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(
                DetailsFragment.PACKAGE_NAME,
                packageName);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(PACKAGE_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String packageName = getArguments().getString(PACKAGE_NAME);
            PackageManager pman = getActivity().getPackageManager();

            model = new Model();

            try {
                model.app = pman.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                model.pkg = pman.getPackageInfo(packageName, PackageManager.GET_META_DATA);
                model.appLabel = String.valueOf(pman.getApplicationLabel(model.app));
            }
            catch (PackageManager.NameNotFoundException e) {
                // FIXME
                Log.e(TAG, "error", e);
                return;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        appCtx = context.getApplicationContext();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(model.appLabel);
        }

        View rootView = inflater.inflate(R.layout.details_fragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.app_detail_list);
        setup(recyclerView);

        return rootView;
    }

    private void setup(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new DetailAdapter(clipboard, appCtx, model));
    }

    private static class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

        private final ClipboardManager clipboard;
        private final Context appCtx;

        private final List<Pair<String, String>> entries = new ArrayList<>();

        DetailAdapter(ClipboardManager clipboard, Context appCtx, Model model) {
            this.clipboard = clipboard;
            this.appCtx = appCtx;

            entry("Label", model.appLabel);
            entry("Package name", model.app.packageName);
            entry("Version name", model.pkg.versionName);
            entry("Version code", model.pkg.versionCode);
            entry("Target SDK version", model.app.targetSdkVersion);
            entry("Class name", model.app.className);
            entry("Source dir", model.app.sourceDir);
            entry("Data dir", model.app.dataDir);
        }

        private void entry(String label, Object value) {
            entries.add(Pair.create(label, String.valueOf(value)));
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            view.setBackgroundResource(R.drawable.potent_item_background);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pair<String, String> entry = entries.get(position);
            holder.label.setText(entry.first);
            holder.value.setText(entry.second);
            holder.root.setOnClickListener(view -> {
                clipboard.setPrimaryClip(ClipData.newPlainText(entry.first, entry.second));
                Toast
                        .makeText(appCtx, R.string.clipboard_copy, Toast.LENGTH_SHORT)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final View root;
            final TextView label;
            final TextView value;

            ViewHolder(View root) {
                super(root);
                this.root = root;
                this.label = root.findViewById(android.R.id.text1);
                this.value = root.findViewById(android.R.id.text2);
            }
        }
    }
}
