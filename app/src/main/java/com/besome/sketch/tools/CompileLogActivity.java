package com.besome.sketch.tools;

import android.content.*;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import mod.hey.studios.util.*;
import mod.jbk.diagnostic.CompileErrorSaver;
import mod.jbk.util.AddMarginOnApplyWindowInsetsListener;
import pro.sketchware.R;
import pro.sketchware.databinding.CompileLogBinding;
import pro.sketchware.utility.SketchwareUtil;

public class CompileLogActivity extends BaseAppCompatActivity {
	
	private CompileErrorSaver saver;
	private SharedPreferences pref;
	private CompileLogBinding b;
	private Intent i;
	
	@Override
	public void onCreate(Bundle s) {
		enableEdgeToEdgeNoContrast();
		super.onCreate(s);
		
		b = CompileLogBinding.inflate(getLayoutInflater());
		setContentView(b.getRoot());
		setSupportActionBar(b.topAppBar);
		
		i = getIntent();
		pref = getPreferences(0);
		
		ViewCompat.setOnApplyWindowInsetsListener(
		b.optionsLayout,
		new AddMarginOnApplyWindowInsetsListener(
		WindowInsetsCompat.Type.navigationBars(),
		WindowInsetsCompat.CONSUMED));
		
		b.topAppBar.setNavigationOnClickListener(
		Helper.getBackPressedClickListener(this));
		
		b.topAppBar.setTitle(
		i.getBooleanExtra("showingLastError", false)
		? "Last compile log" : "Compile log");
		
		b.clearButton.setVisibility(View.GONE);
		b.formatButton.setVisibility(View.GONE);
		
		String id = i.getStringExtra("sc_id");
		if (id == null) {
			finish();
			return;
		}
		
		saver = new CompileErrorSaver(id);
		
		apply();
		setText();
		
		b.copyButton.setEnabled(hasLog());
		b.copyButton.setOnClickListener(v -> copy());
		
	}
	
	private boolean hasLog() {
		try {
			if (!saver.logFileExists()) return false;
			String e = saver.getLogsFromFile();
			return e != null && !e.isEmpty();
		} catch (Exception e) {
			return false;
		}
	}
	
	private String getLog() {
		try {
			if (!saver.logFileExists()) return null;
			String e = saver.getLogsFromFile();
			return (e == null || e.isEmpty()) ? null : e;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		if (hasLog()) {
			m.add(0, 1, 0, "Clear")
			.setIcon(R.drawable.dlt)
			.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			m.add(0, 2, 1, "Filter")
			.setIcon(R.drawable.stylefilter)
			.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem it) {
		return switch (it.getItemId()) {
			case 1 -> {
				clear();
				yield true;
			}
			case 2 -> {
				format(findViewById(it.getItemId()));
				yield true;
			}
			default -> super.onOptionsItemSelected(it);
		};
	}
	
	private void setText() {
		String e = getLog();
		
		if (e == null) {
			b.noContentLayout.setVisibility(View.VISIBLE);
			b.optionsLayout.setVisibility(View.GONE);
			b.copyButton.setEnabled(false);
			return;
		}
		
		b.noContentLayout.setVisibility(View.GONE);
		b.optionsLayout.setVisibility(View.VISIBLE);
		
		b.tvCompileLog.setText(CompileLogHelper.getColoredLogs(this, e));
		b.tvCompileLog.setTextIsSelectable(true);
		b.copyButton.setEnabled(true);
	}
	
	private void clear() {
		try {
			if (saver.logFileExists()) {
				saver.deleteSavedLogs();
			}
			
			b.tvCompileLog.setText("");
			b.noContentLayout.setVisibility(View.VISIBLE);
			b.optionsLayout.setVisibility(View.GONE);
			b.copyButton.setEnabled(false);
			
			invalidateOptionsMenu();
			SketchwareUtil.toast("Compile logs cleared");
		} catch (Exception e) {
			SketchwareUtil.toastError("Clear failed: " + e.toString());
		}
	}
	
	private void format(View anchor) {
		PopupMenu p = new PopupMenu(this, anchor);
		
		p.getMenu().add("Wrap text")
		.setCheckable(true)
		.setChecked(pref.getBoolean("wrapped_text", false));
		
		p.getMenu().add("Monospaced font")
		.setCheckable(true)
		.setChecked(pref.getBoolean("use_monospaced_font", true));
		
		p.getMenu().add("Font size");
		
		p.setOnMenuItemClickListener(it -> {
			String t = it.getTitle().toString();
			
			if ("Wrap text".equals(t)) wrap(it);
			else if ("Monospaced font".equals(t)) mono(it);
			else size();
			
			return true;
		});
		
		p.show();
	}
	
	private void moveLog(boolean wrapped) {
		b.errVScroll.removeAllViews();
		b.errHScroll.removeAllViews();
		
		ViewParent p = b.tvCompileLog.getParent();
		if (p instanceof ViewGroup) {
			((ViewGroup) p).removeView(b.tvCompileLog);
		}
		
		if (wrapped) {
			b.errVScroll.addView(b.tvCompileLog);
		} else {
			b.errHScroll.addView(b.tvCompileLog);
			b.errVScroll.addView(b.errHScroll);
		}
	}
	
	private void wrap(MenuItem it) {
		boolean v = !it.isChecked();
		it.setChecked(v);
		pref.edit().putBoolean("wrapped_text", v).apply();
		moveLog(v);
	}
	
	private void mono(MenuItem it) {
		boolean v = !it.isChecked();
		it.setChecked(v);
		pref.edit().putBoolean("use_monospaced_font", v).apply();
		b.tvCompileLog.setTypeface(v ? Typeface.MONOSPACE : Typeface.DEFAULT);
	}
	
	private void size() {
		NumberPicker p = new NumberPicker(this);
		p.setMinValue(10);
		p.setMaxValue(70);
		p.setValue(pref.getInt("font_size", 11));
		
		LinearLayout l = new LinearLayout(this);
		l.addView(p, new LinearLayout.LayoutParams(-2,-2,Gravity.CENTER));
		
		
		new MaterialAlertDialogBuilder(this)
		.setTitle("Font size")
		.setView(l)
		.setPositiveButton("Save", (d, w) -> {
			int size = p.getValue();
			pref.edit().putInt("font_size", size).apply();
			b.tvCompileLog.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		})
		.setNegativeButton("Cancel", null)
		.show();
	}
	
	private void apply() {
		moveLog(pref.getBoolean("wrapped_text", false));
		
		b.tvCompileLog.setTypeface(
		pref.getBoolean("use_monospaced_font", true)
		? Typeface.MONOSPACE : Typeface.DEFAULT);
		
		b.tvCompileLog.setTextSize(
		TypedValue.COMPLEX_UNIT_SP,
		pref.getInt("font_size", 11));
	}
	
	private void copy() {
		try {
			String e = getLog();
			
			if (e == null) {
				SketchwareUtil.toastError("No logs to copy");
				return;
			}
			
			ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			if (c == null) {
				SketchwareUtil.toastError("Clipboard unavailable");
				return;
			}
			
			c.setPrimaryClip(ClipData.newPlainText("error", e));
			SketchwareUtil.toast("Copied to Clipboard");
		} catch (Exception ex) {
			SketchwareUtil.toastError("Copy failed: " + ex.toString());
		}
	}
	
	
}
