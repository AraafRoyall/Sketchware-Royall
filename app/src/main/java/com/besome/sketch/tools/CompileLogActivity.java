package com.besome.sketch.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import mod.hey.studios.util.CompileLogHelper;
import mod.hey.studios.util.Helper;
import mod.jbk.diagnostic.CompileErrorSaver;
import mod.jbk.util.AddMarginOnApplyWindowInsetsListener;
import pro.sketchware.databinding.CompileLogBinding;
import pro.sketchware.utility.SketchwareUtil;

public class CompileLogActivity extends BaseAppCompatActivity {

    private static final String PREFERENCE_WRAPPED_TEXT = "wrapped_text";
    private static final String PREFERENCE_USE_MONOSPACED_FONT = "use_monospaced_font";
    private static final String PREFERENCE_FONT_SIZE = "font_size";

    private CompileErrorSaver compileErrorSaver;
    private SharedPreferences logViewerPreferences;
    private CompileLogBinding binding;
    private Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        enableEdgeToEdgeNoContrast();
        super.onCreate(savedInstanceState);

        binding = CompileLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.optionsLayout,
                new AddMarginOnApplyWindowInsetsListener(
                        WindowInsetsCompat.Type.navigationBars(),
                        WindowInsetsCompat.CONSUMED
                )
        );

        logViewerPreferences = getPreferences(Context.MODE_PRIVATE);

        binding.topAppBar.setNavigationOnClickListener(
                Helper.getBackPressedClickListener(this)
        );

        if (intent.getBooleanExtra("showingLastError", false)) {
            binding.topAppBar.setTitle("Last compile log");
        } else {
            binding.topAppBar.setTitle("Compile log");
        }

        String sc_id = intent.getStringExtra("sc_id");
        if (sc_id == null) {
            finish();
            return;
        }

        compileErrorSaver = new CompileErrorSaver(sc_id);

        applyLogViewerPreferences();
        setErrorText();

        binding.copyButton.setOnClickListener(v -> copyErrorToClipboard());
    }

    // ================= MENU =================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem clear = menu.add(0, 1, 0, "Clear logs");
        clear.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        clear.setEnabled(compileErrorSaver.logFileExists());

        MenuItem format = menu.add(0, 2, 1, "Text styles");
        format.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == 1) {
            if (compileErrorSaver.logFileExists()) {
                compileErrorSaver.deleteSavedLogs();
                intent.removeExtra("error");
                SketchwareUtil.toast("Compile logs have been cleared.");
            } else {
                SketchwareUtil.toast("No compile logs found.");
            }

            invalidateOptionsMenu(); // refresh enable state
            setErrorText();
            return true;
        }

        if (id == 2) {
            showFormatMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ================= FORMAT MENU =================

    private void showFormatMenu() {
        final String wrapTextLabel = "Wrap text";
        final String monospacedFontLabel = "Monospaced font";
        final String fontSizeLabel = "Font size";

        PopupMenu options = new PopupMenu(this, findViewById(android.R.id.content));

        options.getMenu().add(wrapTextLabel)
                .setCheckable(true)
                .setChecked(getWrappedTextPreference());

        options.getMenu().add(monospacedFontLabel)
                .setCheckable(true)
                .setChecked(getMonospacedFontPreference());

        options.getMenu().add(fontSizeLabel);

        options.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getTitle().toString()) {
                case wrapTextLabel -> {
                    menuItem.setChecked(!menuItem.isChecked());
                    toggleWrapText(menuItem.isChecked());
                }
                case monospacedFontLabel -> {
                    menuItem.setChecked(!menuItem.isChecked());
                    toggleMonospacedText(menuItem.isChecked());
                }
                case fontSizeLabel -> changeFontSizeDialog();
            }
            return true;
        });

        options.show();
    }

    // ================= LOG DISPLAY =================

    private void setErrorText() {
        String error = intent.getStringExtra("error");
        if (error == null) error = compileErrorSaver.getLogsFromFile();

        if (error == null) {
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.optionsLayout.setVisibility(View.GONE);
            return;
        }

        binding.optionsLayout.setVisibility(View.VISIBLE);
        binding.noContentLayout.setVisibility(View.GONE);

        binding.tvCompileLog.setText(
                CompileLogHelper.getColoredLogs(this, error)
        );
        binding.tvCompileLog.setTextIsSelectable(true);
    }

    private void applyLogViewerPreferences() {
        toggleWrapText(getWrappedTextPreference());
        toggleMonospacedText(getMonospacedFontPreference());
        binding.tvCompileLog.setTextSize(getFontSizePreference());
    }

    // ================= PREFERENCES =================

    private boolean getWrappedTextPreference() {
        return logViewerPreferences.getBoolean(PREFERENCE_WRAPPED_TEXT, false);
    }

    private boolean getMonospacedFontPreference() {
        return logViewerPreferences.getBoolean(PREFERENCE_USE_MONOSPACED_FONT, true);
    }

    private int getFontSizePreference() {
        return logViewerPreferences.getInt(PREFERENCE_FONT_SIZE, 11);
    }

    private void toggleWrapText(boolean isChecked) {
        logViewerPreferences.edit()
                .putBoolean(PREFERENCE_WRAPPED_TEXT, isChecked)
                .apply();

        if (isChecked) {
            binding.errVScroll.removeAllViews();
            if (binding.tvCompileLog.getParent() != null) {
                ((ViewGroup) binding.tvCompileLog.getParent())
                        .removeView(binding.tvCompileLog);
            }
            binding.errVScroll.addView(binding.tvCompileLog);
        } else {
            binding.errVScroll.removeAllViews();
            if (binding.tvCompileLog.getParent() != null) {
                ((ViewGroup) binding.tvCompileLog.getParent())
                        .removeView(binding.tvCompileLog);
            }
            binding.errHScroll.removeAllViews();
            binding.errHScroll.addView(binding.tvCompileLog);
            binding.errVScroll.addView(binding.errHScroll);
        }
    }

    private void toggleMonospacedText(boolean isChecked) {
        logViewerPreferences.edit()
                .putBoolean(PREFERENCE_USE_MONOSPACED_FONT, isChecked)
                .apply();

        binding.tvCompileLog.setTypeface(
                isChecked ? Typeface.MONOSPACE : Typeface.DEFAULT
        );
    }

    private void changeFontSizeDialog() {
        NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(10);
        picker.setMaxValue(70);
        picker.setWrapSelectorWheel(false);
        picker.setValue(getFontSizePreference());

        LinearLayout layout = new LinearLayout(this);
        layout.addView(picker, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        ));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select font size")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    logViewerPreferences.edit()
                            .putInt(PREFERENCE_FONT_SIZE, picker.getValue())
                            .apply();

                    binding.tvCompileLog.setTextSize(picker.getValue());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // ================= COPY =================

    private void copyErrorToClipboard() {

        if (!compileErrorSaver.logFileExists()) {
            android.widget.Toast.makeText(
                    getApplicationContext(),
                    "No saved log file",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String error = compileErrorSaver.getLogsFromFile();

        if (error == null || error.isEmpty()) {
            android.widget.Toast.makeText(
                    getApplicationContext(),
                    "No compile log available",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
            return;
        }

        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard == null) {
            android.widget.Toast.makeText(
                    getApplicationContext(),
                    "Clipboard not available",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
            return;
        }

        clipboard.setPrimaryClip(
                android.content.ClipData.newPlainText("error", error)
        );

        SketchwareUtil.toast("Copied to Clipboard");
    }
}