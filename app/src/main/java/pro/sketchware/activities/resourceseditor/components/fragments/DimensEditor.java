package pro.sketchware.activities.resourceseditor.components.fragments;

import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.*;

import java.util.ArrayList;
import java.util.HashMap;

import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.adapters.DimensAdapter;
import pro.sketchware.activities.resourceseditor.components.models.DimenModel;
import pro.sketchware.activities.resourceseditor.components.utils.DimensEditorManager;
import pro.sketchware.databinding.ResourcesEditorFragmentBinding;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.XmlUtil;

public class DimensEditor extends Fragment {

    public ArrayList<DimenModel> list = new ArrayList<>();
    public HashMap<Integer, String> notes = new HashMap<>();

    public DimensAdapter adapter;
    public DimensEditorManager manager = new DimensEditorManager();

    public boolean hasUnsavedChanges;
    public String path;

    private ResourcesEditorActivity activity;
    private ResourcesEditorFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ResourcesEditorActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ResourcesEditorFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // ✅ SAME STYLE AS OTHER EDITORS
    public void updateDimensList(String filePath, int mode, boolean unsaved) {
        this.path = filePath;
        this.hasUnsavedChanges = unsaved;

        list = manager.parse(FileUtil.readFileIfExist(filePath));
        notes = manager.notesMap;

        activity.runOnUiThread(() -> {
            adapter = new DimensAdapter(list, activity, notes);
            binding.recyclerView.setAdapter(adapter);
            updateNoContentLayout();
        });
    }

    private void updateNoContentLayout() {
        if (list.isEmpty()) {
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.noContentTitle.setText("No Dimens");
            binding.noContentBody.setText("Create a dimen resource");
        } else {
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }

    public void showAdd() {
        showEdit(-1);
    }

    public void showEdit(int position) {

        DimenModel model = position >= 0 ? list.get(position) : null;

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);

        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        int gap = (int) (12 * getResources().getDisplayMetrics().density);

        root.setPadding(pad, pad, pad, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, gap);

        // Name
        TextInputLayout nameLayout = new TextInputLayout(getContext(), null,
                com.google.android.material.R.attr.textInputStyle);
        nameLayout.setLayoutParams(params);
        nameLayout.setHint("Dimen name");

        TextInputEditText nameInput = new TextInputEditText(nameLayout.getContext());
        nameInput.setSingleLine(true);
        nameInput.setFilters(new InputFilter[]{
                (s, a, b, d, c, e) -> s.toString().matches("[a-zA-Z0-9_]*") ? null : ""
        });
        nameLayout.addView(nameInput);

        // Value
        TextInputLayout valueLayout = new TextInputLayout(getContext(), null,
                com.google.android.material.R.attr.textInputStyle);
        valueLayout.setLayoutParams(params);
        valueLayout.setHint("Value");

        TextInputEditText valueInput = new TextInputEditText(valueLayout.getContext());
        valueInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valueLayout.addView(valueInput);

        // Unit selector
        RadioGroup group = new RadioGroup(getContext());
        group.setLayoutParams(params);
        group.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton dp = new RadioButton(getContext());
        dp.setText("dp");

        RadioButton sp = new RadioButton(getContext());
        sp.setText("sp");

        group.addView(dp);
        group.addView(sp);

        // Live preview
        TextView preview = new TextView(getContext());
        preview.setLayoutParams(params);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}

            @Override
            public void afterTextChanged(Editable e) {
                String val = e.toString();
                String unit = sp.isChecked() ? "sp" : "dp";
                preview.setText(val.isEmpty() ? "" : val + unit);
            }
        };

        valueInput.addTextChangedListener(watcher);

        group.setOnCheckedChangeListener((g, id) -> {
            String val = valueInput.getText().toString();
            String unit = sp.isChecked() ? "sp" : "dp";
            preview.setText(val.isEmpty() ? "" : val + unit);
        });

        // Prefill
        if (model != null) {
            nameInput.setText(model.getDimenName());
            valueInput.setText(model.getDimenValue());

            if ("sp".equals(model.getDimenUnit())) {
                sp.setChecked(true);
            } else {
                dp.setChecked(true);
            }
        } else {
            dp.setChecked(true);
        }

        root.addView(nameLayout);
        root.addView(valueLayout);
        root.addView(group);
        root.addView(preview);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle(model == null ? "Create dimen" : "Edit dimen")
                .setView(root)
                .setPositiveButton("Save", (d, w) -> {

                    String name = nameInput.getText().toString().trim();
                    String value = valueInput.getText().toString().trim();
                    String unit = sp.isChecked() ? "sp" : "dp";

                    if (name.isEmpty() || value.isEmpty()) {
                        SketchwareUtil.toastError("Fill all fields");
                        return;
                    }

                    if (model != null) {
                        model.setDimenName(name);
                        model.setDimenValue(value);
                        model.setDimenUnit(unit);
                    } else {
                        list.add(new DimenModel(name, value, unit));
                    }

                    adapter.notifyDataSetChanged();
                    hasUnsavedChanges = true;
                    updateNoContentLayout();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void save() {
        if (hasUnsavedChanges) {
            XmlUtil.saveXml(path, manager.build(list, notes));
            hasUnsavedChanges = false;
        }
    }
}