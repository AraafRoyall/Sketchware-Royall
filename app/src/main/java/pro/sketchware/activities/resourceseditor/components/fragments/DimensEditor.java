package pro.sketchware.activities.resourceseditor.components.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Objects;

import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.adapters.DimensAdapter;
import pro.sketchware.activities.resourceseditor.components.models.DimenModel;
import pro.sketchware.activities.resourceseditor.components.utils.DimensEditorManager;
import pro.sketchware.databinding.ResourcesEditorFragmentBinding;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.XmlUtil;

public class DimensEditor extends Fragment {

    public String contentPath;
    public final ArrayList<DimenModel> dimenList = new ArrayList<>();
    public DimensAdapter adapter;
    public boolean hasUnsavedChanges;
    public DimensEditorManager dimensEditorManager;
    private ResourcesEditorActivity activity;
    private ResourcesEditorFragmentBinding binding;
    private HashMap<Integer, String> notesMap = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ResourcesEditorActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResourcesEditorFragmentBinding.inflate(inflater, container, false);
        dimensEditorManager = new DimensEditorManager();
        return binding.getRoot();
    }

    public void updateDimensList(String filePath, int updateMode, boolean hasUnsavedChangesStatus) {
        hasUnsavedChanges = hasUnsavedChangesStatus;
        contentPath = filePath;
        dimensEditorManager.contentPath = contentPath;

        ArrayList<DimenModel> existingDimens = new ArrayList<>(dimenList);
        HashMap<Integer, String> existingNotes = new HashMap<>(notesMap);

        ArrayList<DimenModel> parsedDimens = new ArrayList<>();

        if (FileUtil.isExistFile(contentPath)) {
            dimensEditorManager.parseDimensXML(parsedDimens, FileUtil.readFileIfExist(contentPath));
        } else {
            dimensEditorManager.notesMap.clear();
        }

        HashMap<Integer, String> parsedNotes = new HashMap<>(dimensEditorManager.notesMap);

        boolean isSkippingMode = updateMode == 1;
        boolean isMergeAndReplace = updateMode == 2;

        if (isSkippingMode) {
            java.util.HashSet<String> existingNames = new java.util.HashSet<>();
            for (DimenModel existing : dimenList) {
                existingNames.add(existing.getDimenName());
            }
            for (DimenModel model : parsedDimens) {
                if (!existingNames.contains(model.getDimenName())) {
                    dimenList.add(model);
                }
            }
        } else {
            if (isMergeAndReplace) {
                java.util.HashSet<String> newNames = new java.util.HashSet<>();
                for (DimenModel d : parsedDimens) {
                    newNames.add(d.getDimenName());
                }
                dimenList.removeIf(existing -> newNames.contains(existing.getDimenName()));
            } else {
                dimenList.clear();
            }
            dimenList.addAll(parsedDimens);
        }

        activity.runOnUiThread(() -> {
            if (!isAdded() || getActivity() == null || binding == null || activity.isFinishing() || activity.isDestroyed()) return;
            notesMap = rebuildNotesMap(existingDimens, existingNotes, parsedDimens, parsedNotes, dimenList);
            adapter = new DimensAdapter(dimenList, activity, notesMap);
            binding.recyclerView.setAdapter(adapter);
            activity.checkForInvalidResources();
            updateNoContentLayout();
            if (hasUnsavedChanges) {
                contentPath = activity.dimensFilePath;
            }
        });
    }

    private void updateNoContentLayout() {
        if (dimenList.isEmpty()) {
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.noContentTitle.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_title), Helper.getResString(R.string.resource_type_dimen)));
            binding.noContentBody.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_body), Helper.getResString(R.string.resource_type_dimen_lower)));
        } else {
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }

    public void showAddDimenDialog() {
        showDimenEditDialog(null, -1);
    }

    public void showDimenEditDialog(DimenModel dimenModel, int position) {
        if (!isAdded() || activity == null) return;

        boolean isEditing = dimenModel != null;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        int gap = (int) (12 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, 0);

        LinearLayout.LayoutParams fieldParams = new LinearLayout.LayoutParams(-1, -2);
        fieldParams.setMargins(0, 0, 0, gap);

        TextInputLayout nameLayout = new TextInputLayout(requireContext(), null, com.google.android.material.R.attr.textInputStyle);
        nameLayout.setLayoutParams(fieldParams);
        nameLayout.setHint("Dimen name");

        TextInputEditText nameInput = new TextInputEditText(nameLayout.getContext());
        nameInput.setSingleLine(true);
        nameInput.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> source.toString().matches("[a-zA-Z0-9_]*") ? null : ""
        });
        nameLayout.addView(nameInput);

        TextInputLayout valueLayout = new TextInputLayout(requireContext(), null, com.google.android.material.R.attr.textInputStyle);
        valueLayout.setLayoutParams(fieldParams);
        valueLayout.setHint("Value");

        TextInputEditText valueInput = new TextInputEditText(valueLayout.getContext());
        valueInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valueInput.setSingleLine(true);
        valueInput.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    String newValue = dest.subSequence(0, dstart).toString()
                            + source.subSequence(start, end).toString()
                            + dest.subSequence(dend, dest.length()).toString();
                    return newValue.matches("\\d*(\\.\\d*)?") ? null : "";
                }
        });
        valueLayout.addView(valueInput);

        RadioGroup unitGroup = new RadioGroup(requireContext());
        unitGroup.setOrientation(LinearLayout.HORIZONTAL);
        unitGroup.setLayoutParams(fieldParams);

        MaterialRadioButton dpButton = new MaterialRadioButton(requireContext());
        dpButton.setId(View.generateViewId());
        dpButton.setText("dp");

        MaterialRadioButton spButton = new MaterialRadioButton(requireContext());
        spButton.setId(View.generateViewId());
        spButton.setText("sp");

        unitGroup.addView(dpButton);
        unitGroup.addView(spButton);

        if (isEditing) {
            nameInput.setText(dimenModel.getDimenName());
            valueInput.setText(dimenModel.getDimenValue());

            if ("sp".equalsIgnoreCase(dimenModel.getDimenUnit())) {
                spButton.setChecked(true);
            } else {
                dpButton.setChecked(true);
            }
        } else {
            dpButton.setChecked(true);
        }

        TextInputLayout headerLayout = new TextInputLayout(requireContext(), null, com.google.android.material.R.attr.textInputStyle);
        headerLayout.setLayoutParams(fieldParams);
        headerLayout.setHint("Header note");

        TextInputEditText headerInput = new TextInputEditText(headerLayout.getContext());
        headerInput.setSingleLine(false);
        headerLayout.addView(headerInput);

        root.addView(nameLayout);
        root.addView(valueLayout);
        root.addView(unitGroup);
        root.addView(headerLayout);

        if (isEditing) {
            int originalIndex = dimenList.indexOf(dimenModel);
            headerInput.setText(originalIndex >= 0 ? notesMap.getOrDefault(originalIndex, "") : "");
            builder.setTitle(R.string.dimen_title_edit);
        } else {
            builder.setTitle(R.string.dimen_title_create);
        }

        builder.setPositiveButton(R.string.common_word_save, null);

        if (isEditing) {
            builder.setNeutralButton(R.string.common_word_delete, null);
        }

        builder.setNegativeButton(Helper.getResString(R.string.cancel), null);
        builder.setView(root);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                String key = Objects.requireNonNull(nameInput.getText()).toString().trim();
                String value = Objects.requireNonNull(valueInput.getText()).toString().trim();
                String note = Objects.requireNonNull(headerInput.getText()).toString().trim();
                String unit = spButton.isChecked() ? "sp" : "dp";

                if (key.isEmpty() || value.isEmpty()) {
                    SketchwareUtil.toastError(Helper.getResString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT);
                    return;
                }

                if (!isNumeric(value)) {
                    SketchwareUtil.toastError("Value must be numeric", Toast.LENGTH_SHORT);
                    return;
                }

                if (isEditing) {
                    int idx = dimenList.indexOf(dimenModel);
                    if (idx < 0) return;

                    if (!key.equals(dimenModel.getDimenName()) && isDuplicateName(key, idx)) {
                        SketchwareUtil.toastError(String.format(Helper.getResString(R.string.error_key_already_exists), key), Toast.LENGTH_SHORT);
                        return;
                    }

                    dimenModel.setDimenName(key);
                    dimenModel.setDimenValue(value);
                    dimenModel.setDimenUnit(unit);

                    if (note.isEmpty()) {
                        notesMap.remove(idx);
                    } else {
                        notesMap.put(idx, note);
                    }

                    adapter = new DimensAdapter(dimenList, activity, notesMap);
                    binding.recyclerView.setAdapter(adapter);
                } else {
                    if (isDuplicateName(key, -1)) {
                        SketchwareUtil.toastError(String.format(Helper.getResString(R.string.error_key_already_exists), key), Toast.LENGTH_SHORT);
                        return;
                    }

                    if (!addDimen(key, value, unit, note)) {
                        return;
                    }
                }

                hasUnsavedChanges = true;
                updateNoContentLayout();
                dialog.dismiss();
            });

            if (isEditing) {
                Button deleteButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                deleteButton.setOnClickListener(v -> {
                    int idx = dimenList.indexOf(dimenModel);
                    if (idx >= 0) {
                        dimenList.remove(idx);
                        notesMap.remove(idx);

                        HashMap<Integer, String> reindexed = new HashMap<>();
                        for (java.util.Map.Entry<Integer, String> entry : notesMap.entrySet()) {
                            int k = entry.getKey();
                            reindexed.put(k > idx ? k - 1 : k, entry.getValue());
                        }
                        notesMap.clear();
                        notesMap.putAll(reindexed);
                    }

                    adapter = new DimensAdapter(dimenList, activity, notesMap);
                    binding.recyclerView.setAdapter(adapter);
                    hasUnsavedChanges = true;
                    updateNoContentLayout();
                    dialog.dismiss();
                });
            }
        });

        dialog.show();
    }

    private boolean addDimen(String name, String value, String unit, String note) {
        for (int i = 0; i < dimenList.size(); i++) {
            if (dimenList.get(i).getDimenName().equals(name)) {
                SketchwareUtil.toastError(String.format(Helper.getResString(R.string.error_key_already_exists), name), Toast.LENGTH_SHORT);
                return false;
            }
        }

        dimenList.add(new DimenModel(name, value, unit));

        if (!note.isEmpty()) {
            notesMap.put(dimenList.size() - 1, note);
        }

        adapter = new DimensAdapter(dimenList, activity, notesMap);
        binding.recyclerView.setAdapter(adapter);
        SketchwareUtil.toast(Helper.getResString(R.string.common_word_saved));
        return true;
    }

    private boolean isDuplicateName(String name, int ignoreIndex) {
        for (int i = 0; i < dimenList.size(); i++) {
            if (i == ignoreIndex) continue;
            if (dimenList.get(i).getDimenName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private <T> HashMap<Integer, String> rebuildNotesMap(ArrayList<T> existingItems,
                                                         HashMap<Integer, String> existingNotes,
                                                         ArrayList<T> importedItems,
                                                         HashMap<Integer, String> importedNotes,
                                                         ArrayList<T> finalItems) {
        IdentityHashMap<T, Integer> existingIndexes = new IdentityHashMap<>();
        for (int i = 0; i < existingItems.size(); i++) {
            existingIndexes.put(existingItems.get(i), i);
        }

        IdentityHashMap<T, Integer> importedIndexes = new IdentityHashMap<>();
        for (int i = 0; i < importedItems.size(); i++) {
            importedIndexes.put(importedItems.get(i), i);
        }

        HashMap<Integer, String> rebuiltNotes = new HashMap<>();
        for (int i = 0; i < finalItems.size(); i++) {
            T item = finalItems.get(i);

            Integer importedIndex = importedIndexes.get(item);
            if (importedIndex != null && importedNotes.containsKey(importedIndex)) {
                rebuiltNotes.put(i, importedNotes.get(importedIndex));
                continue;
            }

            Integer existingIndex = existingIndexes.get(item);
            if (existingIndex != null && existingNotes.containsKey(existingIndex)) {
                rebuiltNotes.put(i, existingNotes.get(existingIndex));
            }
        }

        return rebuiltNotes;
    }

    public void saveDimensFile() {
        if (hasUnsavedChanges) {
            XmlUtil.saveXml(contentPath, dimensEditorManager.convertListToXml(dimenList, notesMap));
            hasUnsavedChanges = false;
        }
    }
}