package pro.sketchware.activities.resourceseditor.components.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.adapters.IntsAdapter;
import pro.sketchware.activities.resourceseditor.components.models.IntModel;
import pro.sketchware.activities.resourceseditor.components.utils.IntsEditorManager;
import pro.sketchware.databinding.ResourcesEditorFragmentBinding;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.XmlUtil;

public class IntsEditor extends Fragment {

    private ResourcesEditorFragmentBinding binding;
    private ResourcesEditorActivity activity;

    public IntsAdapter adapter;
    public final ArrayList<IntModel> intsList = new ArrayList<>();
    private HashMap<Integer, String> notesMap = new HashMap<>();

    public final IntsEditorManager intsEditorManager = new IntsEditorManager();

    public boolean hasUnsavedChanges;
    private String filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ResourcesEditorActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResourcesEditorFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void updateIntsList(String filePath, int updateMode, boolean hasUnsavedChangesStatus) {
        hasUnsavedChanges = hasUnsavedChangesStatus;
        this.filePath = filePath;

        boolean isSkippingMode = updateMode == 1;
        boolean isMergeAndReplace = updateMode == 2;

        ArrayList<IntModel> defaultInts = intsEditorManager.parseIntsFile(FileUtil.readFileIfExist(filePath));
        notesMap = new HashMap<>(intsEditorManager.notesMap);

        if (isSkippingMode) {
            HashSet<String> existingNames = new HashSet<>();
            for (IntModel existing : intsList) {
                existingNames.add(existing.getIntName());
            }

            for (IntModel model : defaultInts) {
                if (existingNames.add(model.getIntName())) {
                    intsList.add(model);
                }
            }
        } else {
            if (isMergeAndReplace) {
                HashSet<String> newNames = new HashSet<>();
                for (IntModel model : defaultInts) {
                    newNames.add(model.getIntName());
                }
                intsList.removeIf(existing -> newNames.contains(existing.getIntName()));
            } else {
                intsList.clear();
            }
            intsList.addAll(defaultInts);
        }

        activity.runOnUiThread(() -> {
            adapter = new IntsAdapter(intsList, activity, notesMap);
            binding.recyclerView.setAdapter(adapter);
            activity.checkForInvalidResources();
            updateNoContentLayout();
            if (hasUnsavedChanges) {
                this.filePath = activity.integersFilePath;
            }
        });
    }

    private void updateNoContentLayout() {
        if (intsList.isEmpty()) {
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.noContentTitle.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_title), "Integers"));
            binding.noContentBody.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_body), "integer"));
        } else {
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }

    public void showAddIntDialog() {
        showIntDialog(null, -1);
    }

    public void showEditIntDialog(int position) {
        if (position < 0 || position >= intsList.size()) {
            return;
        }
        showIntDialog(intsList.get(position), position);
    }

    private void showIntDialog(IntModel intModel, int position) {
        boolean isEditing = intModel != null;

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity());
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        int pad = (int) (16 * requireContext().getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Integer name");

        EditText valueInput = new EditText(requireContext());
        valueInput.setHint("Integer value");
        valueInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        EditText headerInput = new EditText(requireContext());
        headerInput.setHint("Header note");

        root.addView(nameInput);
        root.addView(valueInput);
        root.addView(headerInput);

        if (isEditing) {
            nameInput.setText(intModel.getIntName());
            valueInput.setText(intModel.getIntValue());
            headerInput.setText(notesMap.getOrDefault(position, ""));
        }

        dialog.setTitle(isEditing ? "Edit integer" : "Create new integer");
        dialog.setView(root);

        dialog.setPositiveButton(isEditing ? "Save" : "Create", (d, which) -> {
            String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
            String value = Objects.requireNonNull(valueInput.getText()).toString().trim();
            String header = Objects.requireNonNull(headerInput.getText()).toString().trim();

            if (name.isEmpty() || value.isEmpty()) {
                SketchwareUtil.toastError("Please fill in all fields");
                return;
            }

            if (!isInteger(value)) {
                SketchwareUtil.toastError("Please enter a valid integer");
                return;
            }

            if (isEditing) {
                if (!name.equals(intModel.getIntName()) && isDuplicateName(name, position)) {
                    SketchwareUtil.toastError("\"" + name + "\" is already exist");
                    return;
                }

                intModel.setIntName(name);
                intModel.setIntValue(value);

                if (header.isEmpty()) {
                    notesMap.remove(position);
                } else {
                    notesMap.put(position, header);
                }

                adapter.notifyItemChanged(position);
            } else {
                if (isDuplicateName(name, -1)) {
                    SketchwareUtil.toastError("\"" + name + "\" is already exist");
                    return;
                }

                IntModel model = new IntModel(name, value);
                intsList.add(model);
                int notifyPosition = intsList.size() - 1;

                if (!header.isEmpty()) {
                    notesMap.put(notifyPosition, header);
                }

                adapter.notifyItemInserted(notifyPosition);
            }

            updateNoContentLayout();
            hasUnsavedChanges = true;
        });

        if (isEditing) {
            dialog.setNeutralButton(Helper.getResString(R.string.common_word_delete), (d, which) ->
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Warning")
                            .setMessage("Are you sure you want to delete " + intModel.getIntName() + "?")
                            .setPositiveButton(R.string.common_word_yes, (d2, w) -> {
                                intsList.remove(position);
                                notesMap.remove(position);
                                adapter.notifyItemRemoved(position);
                                updateNoContentLayout();
                                hasUnsavedChanges = true;
                            })
                            .setNegativeButton("Cancel", null)
                            .show());
        }

        dialog.setNegativeButton(getString(R.string.cancel), null);
        dialog.show();
    }

    private boolean isDuplicateName(String name, int ignorePosition) {
        for (int i = 0; i < intsList.size(); i++) {
            if (i == ignorePosition) continue;
            if (intsList.get(i).getIntName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void saveIntsFile() {
        if (hasUnsavedChanges) {
            XmlUtil.saveXml(filePath, intsEditorManager.convertIntsToXML(intsList, notesMap));
            hasUnsavedChanges = false;
        }
    }
}