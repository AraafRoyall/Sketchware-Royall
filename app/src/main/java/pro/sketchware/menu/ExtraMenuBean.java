package pro.sketchware.menu;

import static android.text.TextUtils.isEmpty;
import static pro.sketchware.utility.SketchwareUtil.getDip;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.LinkedHashSet;
import androidx.annotation.NonNull;

import com.besome.sketch.beans.AdTestDeviceBean;
import com.besome.sketch.beans.AdUnitBean;
import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.editor.LogicEditorActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.a.a.Ss;
import a.a.a.eC;
import a.a.a.jC;
import a.a.a.uq;
import a.a.a.wB;
import dev.pranav.filepicker.FilePickerCallback;
import dev.pranav.filepicker.FilePickerDialogFragment;
import dev.pranav.filepicker.FilePickerOptions;
import dev.pranav.filepicker.SelectionMode;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.activities.tools.ConfigActivity;
import mod.hilal.saif.asd.AsdDialog;
import pro.sketchware.R;
import pro.sketchware.activities.resourceseditor.components.utils.StringsEditorManager;
import pro.sketchware.lib.base.BaseTextWatcher;
import pro.sketchware.lib.highlighter.SimpleHighlighter;
import pro.sketchware.utility.CustomVariableUtil;
import pro.sketchware.utility.FilePathUtil;
import pro.sketchware.utility.FileResConfig;
import pro.sketchware.utility.FileUtil;
import mod.hilal.saif.components.ComponentsHandler;

public class ExtraMenuBean {
	
	public static final int VARIABLE_TYPE_BOOLEAN = 0;
	public static final int VARIABLE_TYPE_NUMBER = 1;
	public static final int VARIABLE_TYPE_MAP = 3;
	public static final int VARIABLE_TYPE_STRING = 2;
	
	public static final int LIST_TYPE_NUMBER = 1;
	public static final int LIST_TYPE_MAP = 3;
	public static final int LIST_TYPE_STRING = 2;
	
	public static final String[] adSize = {"AUTO_HEIGHT", "BANNER", "FLUID", "FULL_BANNER", "FULL_WIDTH", "INVALID", "LARGE_BANNER", "LEADERBOARD", "MEDIUM_RECTANGLE", "SEARCH", "SMART_BANNER", "WIDE_SKYSCRAPER"};
	public static final String[] intentKey = {"EXTRA_ALLOW_MULTIPLE", "EXTRA_EMAIL", "EXTRA_INDEX", "EXTRA_INTENT", "EXTRA_PHONE_NUMBER", "EXTRA_STREAM", "EXTRA_SUBJECT", "EXTRA_TEXT", "EXTRA_TITLE"};
	public static final String[] pixelFormat = {"OPAQUE", "RGBA_1010102", "RGBA_8888", "RGBA_F16", "RGBX_8888", "RGB_565", "RGB_888", "TRANSLUCENT", "TRANSPARENT", "UNKNOWN"};
	public static final String[] patternFlags = {"CANON_EQ", "CASE_INSENSITIVE", "COMMENTS", "DOTALL", "LITERAL", "MULTILINE", "UNICODE_CASE", "UNIX_LINES"};
	public static final String[] permission = {"CAMERA", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION", "RECORD_AUDIO", "READ_CONTACTS", "WRITE_CONTACTS", "READ_SMS", "SEND_SMS", "READ_PHONE_STATE", "CALL_PHONE", "READ_CALENDAR", "WRITE_CALENDAR", "BLUETOOTH", "BLUETOOTH_ADMIN"};
	
	private final String ASSETS_PATH = FileUtil.getExternalStorageDir() + "/.sketchware/data/%s/files/assets/";
	private final String NATIVE_PATH = FileUtil.getExternalStorageDir() + "/.sketchware/data/%s/files/native_libs/";
	private final DefaultExtraMenuBean defaultExtraMenu;
	private final FilePathUtil fpu;
	private final FileResConfig frc;
	private final LogicEditorActivity logicEditor;
	private final FilePickerOptions mOptions = new FilePickerOptions();
	private final eC projectDataManager;
	private final String sc_id;
	private final String javaName;
	
	public ExtraMenuBean(LogicEditorActivity logicA) {
		logicEditor = logicA;
		sc_id = logicA.scId;
		fpu = new FilePathUtil();
		frc = new FileResConfig(logicA.scId);
		defaultExtraMenu = new DefaultExtraMenuBean(logicA);
		projectDataManager = jC.a(logicA.scId);
		javaName = logicA.M.getJavaName();
	}
	
	public static void setupSearchView(View view, ViewGroup viewGroup) {
		if (viewGroup.getChildCount() == 0) {
			return;
		}
		EditText searchInput = view.findViewById(R.id.searchInput);
		TextInputLayout textInputLayout = view.findViewById(R.id.searchInputLayout);
		textInputLayout.setVisibility(View.VISIBLE);
		searchInput.addTextChangedListener(new BaseTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String filterText = s.toString().toLowerCase();
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					View childView = viewGroup.getChildAt(i);
					if (childView instanceof TextView textView) {
						String itemText = Helper.getText(textView).toLowerCase();
						if (itemText.contains(filterText)) {
							textView.setVisibility(View.VISIBLE);
						} else {
							textView.setVisibility(View.GONE);
						}
					}
				}
			}
		});
	}
	
	private void codeMenu(Ss menu) {
		AsdDialog asdDialog = new AsdDialog(logicEditor);
		asdDialog.setContent(menu.getArgValue().toString());
		asdDialog.show();
		asdDialog.setOnSaveClickListener(logicEditor, false, menu, asdDialog);
		asdDialog.setOnCancelClickListener(asdDialog);
	}
	
	public void defineMenuSelector(Ss ss) {
		String menuType = ss.b;
		String menuName = ss.getMenuName();
		
		switch (menuType) {
			case "d":
			logicEditor.a(ss, true);
			break;
			
			case "s":
			switch (menuName) {
				case "intentData":
				logicEditor.e(ss);
				return;
				
				case "url":
				logicEditor.c(ss);
				return;
				
				case "inputCode":
				codeMenu(ss);
				return;
				
				case "import":
				asdDialog(ss, "Enter the path without import & semicolon");
				return;
				
				default:
				asdDialog(ss, null);
			}
			break;
			
			case "m":
			switch (menuName) {
				case "resource":
				logicEditor.pickImage(ss, "property_image");
				return;
				
				case "resource_bg":
				logicEditor.pickImage(ss, "property_background_resource");
				return;
				
				case "sound":
				logicEditor.h(ss);
				return;
				
				case "font":
				logicEditor.d(ss);
				return;
				
				case "typeface":
				logicEditor.i(ss);
				return;
				
				case "color":
				logicEditor.b(ss);
				return;
				
				case "view":
				case "textview":
				case "edittext":
				case "imageview":
				case "listview":
				case "spinner":
				case "listSpn":
				case "webview":
				case "checkbox":
				case "switch":
				case "seekbar":
				case "calendarview":
				case "compoundButton":
				case "materialButton":
				case "adview":
				case "progressbar":
				case "mapview":
				case "radiobutton":
				case "ratingbar":
				case "searchview":
				case "videoview":
				case "gridview":
				case "actv":
				case "mactv":
				case "tablayout":
				case "viewpager":
				case "bottomnavigation":
				case "badgeview":
				case "patternview":
				case "sidebar":
				case "recyclerview":
				case "cardview":
				case "collapsingtoolbar":
				case "textinputlayout":
				case "swiperefreshlayout":
				case "radiogroup":
				case "lottie":
				case "otpview":
				case "signinbutton":
				case "youtubeview":
				case "codeview":
				case "datepicker":
				case "timepicker":
				logicEditor.f(ss);
				return;
				
				case "Assets":
				case "NativeLib":
				pathSelectorMenu(ss);
				return;
				
				case "permissions":
				multiSelectMenu(ss);
				return;
				
				default:
				defaultMenus(ss);
			}
			break;
		}
	}
	
	private void multiSelectMenu(@NonNull Ss menu) {
		MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(logicEditor);
		
		String currentVal = menu.getArgValue().toString();
		String[] selectedItems = currentVal.split(Pattern.quote("|"));
		ArrayList<String> selectedList = new ArrayList<>(Arrays.asList(selectedItems));
		
		boolean[] checkedItems = new boolean[permission.length];
		for (int i = 0; i < permission.length; i++) {
			for (String s : selectedList) {
				if (s.trim().equals(permission[i])) {
					checkedItems[i] = true;
					break;
				}
			}
		}
		
		dialog.setTitle("Select Permissions")
		.setMultiChoiceItems(permission, checkedItems, (dialogInterface, which, isChecked) ->
		checkedItems[which] = isChecked)
		.setPositiveButton(R.string.common_word_select, (v, which) -> {
			StringJoiner joiner = new StringJoiner("|");
			for (int i = 0; i < permission.length; i++) {
				if (checkedItems[i]) {
					joiner.add(permission[i]);
				}
			}
			logicEditor.a(menu, joiner.toString());
		})
		.setNegativeButton(R.string.common_word_cancel, null)
		.show();
	}
	
	@SuppressLint("SetTextI18n")
	private void defaultMenus(Ss menu) {
		String menuName = menu.getMenuName();
		MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(logicEditor);
		View rootView = wB.a(logicEditor, R.layout.property_popup_selector_single);
		ViewGroup viewGroup = rootView.findViewById(R.id.rg_content);
		ArrayList<String> menus = new ArrayList<>();
		String title;
		switch (menuName) {
			case "varInt":
			title = logicEditor.getString(R.string.logic_editor_title_select_variable_number);
			menus = getDynamicMenus("Number", javaName, projectDataManager);
			break;
			
			case "varBool":
			title = logicEditor.getString(R.string.logic_editor_title_select_variable_boolean);
			menus = getDynamicMenus("Boolean", javaName, projectDataManager);
			break;
			
			case "String":
			case "varStr":
			title = logicEditor.getString(R.string.logic_editor_title_select_variable_string);
			menus = getDynamicMenus("String", javaName, projectDataManager);
			break;
			
			case "varObject":
			title = "Select Object Variable";
			menus = getDynamicMenus("ObjectX", javaName, projectDataManager);
			break;
			
			case "varMap":
			title = logicEditor.getString(R.string.logic_editor_title_select_variable_map);
			menus = getDynamicMenus("Map", javaName, projectDataManager);
			break;
			
			case "listInt":
			title = logicEditor.getString(R.string.logic_editor_title_select_list_number);
			menus = getListMenus(LIST_TYPE_NUMBER);
			break;
			
			case "listStr":
			title = logicEditor.getString(R.string.logic_editor_title_select_list_string);
			menus = getListMenus(LIST_TYPE_STRING);
			break;
			
			case "listMap":
			title = logicEditor.getString(R.string.logic_editor_title_select_list_map);
			menus = getListMenus(LIST_TYPE_MAP);
			break;
			
			case "list":
			title = logicEditor.getString(R.string.logic_editor_title_select_list);
			for (String variable : projectDataManager.c(javaName)) {
				String variableName = CustomVariableUtil.getVariableName(variable);
				menus.add(variableName != null ? variableName : variable);
			}
			break;
			
			case "intent":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_intent);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_INTENT);
			break;
			
			case "file":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_file);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_SHAREDPREF);
			break;
			
			case "intentAction":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_intent_action);
			menus = new ArrayList<>(Arrays.asList(uq.b()));
			break;
			
			case "intentFlags":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_intent_flags);
			menus = new ArrayList<>(Arrays.asList(uq.c()));
			break;
			
			case "calendar":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_calendar);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_CALENDAR);
			break;
			
			case "calendarField":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_calendar_field);
			menus = new ArrayList<>(Arrays.asList(uq.e));
			break;
			
			case "vibrator":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_vibrator);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_VIBRATOR);
			break;
			
			case "timer":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_timer);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_TIMERTASK);
			break;
			
			case "firebase":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_firebase);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE);
			break;
			
			case "firebaseauth":
			title = logicEditor.getString(R.string.logic_editor_component_firebaseauth_title_select_firebase_auth);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH);
			break;
			
			case "firebasestorage":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_firebasestorage);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_STORAGE);
			break;
			
			case "dialog":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_dialog);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_DIALOG);
			break;
			
			case "mediaplayer":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_mediaplayer);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_MEDIAPLAYER);
			break;
			
			case "soundpool":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_soundpool);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_SOUNDPOOL);
			break;
			
			case "objectanimator":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_objectanimator);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_OBJECTANIMATOR);
			break;
			
			case "aniRepeatMode":
			title = logicEditor.getString(R.string.logic_editor_title_select_animator_repeat_mode);
			menus = new ArrayList<>(Arrays.asList(uq.j));
			break;
			
			case "aniInterpolator":
			title = logicEditor.getString(R.string.logic_editor_title_select_animator_interpolator);
			menus = new ArrayList<>(Arrays.asList(uq.k));
			break;
			
			case "visible":
			title = logicEditor.getString(R.string.logic_editor_title_select_visibility);
			menus = new ArrayList<>(Arrays.asList(uq.g));
			break;
			
			case "cacheMode":
			title = logicEditor.getString(R.string.logic_editor_title_select_cache_mode);
			menus = new ArrayList<>(Arrays.asList(uq.h));
			break;
			
			case "animatorproperty":
			title = logicEditor.getString(R.string.logic_editor_title_select_animator_target_property);
			menus = new ArrayList<>(Arrays.asList(uq.i));
			break;
			
			case "gyroscope":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_gyroscope);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_GYROSCOPE);
			break;
			
			case "interstitialad":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_interstitialad);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD);
			break;
			
			case "camera":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_camera);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_CAMERA);
			break;
			
			case "filepicker":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_filepicker);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FILE_PICKER);
			break;
			
			case "directoryType":
			title = logicEditor.getString(R.string.logic_editor_title_select_directory_type);
			menus = new ArrayList<>(Arrays.asList(uq.l));
			break;
			
			case "requestnetwork":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_request_network);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_REQUEST_NETWORK);
			break;
			
			case "method":
			title = logicEditor.getString(R.string.logic_editor_title_request_network_method);
			menus = new ArrayList<>(Arrays.asList(uq.n));
			break;
			
			case "requestType":
			title = logicEditor.getString(R.string.logic_editor_title_request_network_request_type);
			menus = new ArrayList<>(Arrays.asList(uq.o));
			break;
			
			case "texttospeech":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_text_to_speech);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_TEXT_TO_SPEECH);
			break;
			
			case "speechtotext":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_speech_to_text);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_SPEECH_TO_TEXT);
			break;
			
			case "bluetoothconnect":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_bluetooth_connect);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_BLUETOOTH_CONNECT);
			break;
			
			case "locationmanager":
			title = logicEditor.getString(R.string.logic_editor_title_select_component_location_manager);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_LOCATION_MANAGER);
			break;
			
			case "videoad":
			title = logicEditor.getString(R.string.logic_editor_title_select_component);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_REWARDED_VIDEO_AD);
			break;
			
			case "progressdialog":
			title = logicEditor.getString(R.string.logic_editor_title_select_component);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_PROGRESS_DIALOG);
			break;
			
			case "datepickerdialog":
			title = logicEditor.getString(R.string.logic_editor_title_select_component);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_DATE_PICKER_DIALOG);
			break;
			
			case "asynctask":
			title = logicEditor.getString(R.string.logic_editor_title_select_component);
			menus = getComponentMenus(36);
			break;
			
			case "timepickerdialog":
			title = logicEditor.getString(R.string.logic_editor_title_select_component);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_TIME_PICKER_DIALOG);
			break;
			
			case "notification":
			title = logicEditor.getString(R.string.logic_editor_title_select_component);
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_NOTIFICATION);
			break;
			
			case "fragmentAdapter":
			title = "Select a FragmentAdapter Component";
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FRAGMENT_ADAPTER);
			break;
			
			case "phoneauth":
			title = "Select a FirebasePhone Component";
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_PHONE);
			break;
			
			case "cloudmessage":
			title = "Select a CloudMessage Component";
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_CLOUD_MESSAGE);
			break;
			
			case "googlelogin":
			title = "Select a FirebaseGoogle Component";
			menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_GOOGLE_LOGIN);
			break;
			
			case "providerType":
			title = logicEditor.getString(R.string.logic_editor_title_location_manager_provider_type);
			menus = new ArrayList<>(Arrays.asList(uq.p));
			break;
			
			case "mapType":
			title = logicEditor.getString(R.string.logic_editor_title_mapview_map_type);
			menus = new ArrayList<>(Arrays.asList(uq.q));
			break;
			
			case "markerColor":
			title = logicEditor.getString(R.string.logic_editor_title_mapview_marker_color);
			menus = new ArrayList<>(Arrays.asList(uq.r));
			break;
			
			case "service":
			title = "Select a Background Service";
			if (FileUtil.isExistFile(fpu.getManifestService(sc_id))) {
				menus = frc.getServiceManifestList();
			}
			break;
			
			case "broadcast":
			title = "Select a Broadcast Receiver";
			if (FileUtil.isExistFile(fpu.getManifestBroadcast(sc_id))) {
				menus = frc.getBroadcastManifestList();
			}
			break;
			
			case "activity":
			ArrayList<String> activityMenu = new ArrayList<>();
			title = logicEditor.getString(R.string.logic_editor_title_select_activity);
			for (ProjectFileBean projectFileBean : jC.b(sc_id).b()) {
				activityMenu.add(projectFileBean.getActivityName());
			}
			for (String activity : activityMenu) {
				viewGroup.addView(logicEditor.e(activity));
			}
			activityMenu = new ArrayList<>();
			if (FileUtil.isExistFile(fpu.getManifestJava(sc_id))) {
				for (String activity : frc.getJavaManifestList()) {
					if (activity.contains(".")) {
						activityMenu.add(activity.substring(1 + activity.lastIndexOf(".")));
					}
				}
				if (!activityMenu.isEmpty()) {
					TextView txt = new TextView(logicEditor);
					txt.setText("Custom Activities");
					txt.setPadding((int) getDip(2), (int) getDip(4), (int) getDip(4), (int) getDip(4));
					txt.setTextSize(14f);
					viewGroup.addView(txt);
				}
				for (String activity : activityMenu) {
					viewGroup.addView(logicEditor.e(activity));
				}
			}
			setupSearchView(rootView, viewGroup);
			break;
			
			case "customViews":
			title = "Select a Custom View";
			for (ProjectFileBean projectFileBean : jC.b(sc_id).c()) {
				menus.add(projectFileBean.fileName);
			}
			break;
			
			case "SignButtonColor":
			title = "Select a SignInButton Color";
			menus.add("COLOR_AUTO");
			menus.add("COLOR_DARK");
			menus.add("COLOR_LIGHT");
			break;
			
			case "SignButtonSize":
			title = "Select SignInButton Size";
			menus.add("SIZE_ICON_ONLY");
			menus.add("SIZE_STANDARD");
			menus.add("SIZE_WIDE");
			break;
			
			case "ResString":
			title = "Select a ResString";
			
			String filePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/files/resource/values/strings.xml"));
			ArrayList<HashMap<String, Object>> StringsListMap = new ArrayList<>();
			StringsEditorManager stringsEditorManager = new StringsEditorManager();
			stringsEditorManager.convertXmlStringsToListMap(FileUtil.readFileIfExist(filePath), StringsListMap);
			
			if (!stringsEditorManager.isXmlStringsExist(StringsListMap, "app_name")) {
				menus.add("R.string.app_name");
			}
			for (HashMap<String, Object> map : StringsListMap) {
				menus.add("R.string." + map.get("key"));
			}
			
			break;
			case "ResStyle":
			case "ResColor":
			case "ResArray":
			case "ResDimen":
			case "ResBool":
			case "ResInteger":
			case "ResAttr":
			case "ResXml":
			title = "Deprecated";
			dialog.setMessage("This Block Menu was initially used to parse resource values, but was too I/O heavy and has been removed due to that. Please use the Code Editor instead.");
			break;
			
			case "AdUnit":
			dialog.setIcon(R.drawable.unit_96);
			title = "Select an Ad Unit";
			for (AdUnitBean bean : jC.c(sc_id).e.adUnits) {
				menus.add(bean.id);
			}
			break;
			
			case "TestDevice":
			dialog.setIcon(R.drawable.ic_test_device_48dp);
			title = "Select a Test device";
			for (AdTestDeviceBean testDevice : jC.c(sc_id).e.testDevices) {
				menus.add(testDevice.deviceId);
			}
			break;
			
			case "IntentKey":
			title = "Select an Intent key";
			menus.addAll(new ArrayList<>(Arrays.asList(intentKey)));
			break;
			
			case "PatternFlag":
			title = "Select a Pattern Flags";
			menus.addAll(new ArrayList<>(Arrays.asList(patternFlags)));
			break;
			
			case "Permission":
			title = "Select a Permission";
			menus.addAll(new ArrayList<>(Arrays.asList(permission)));
			break;
			
			case "AdSize":
			title = "Select an Ad size";
			menus.addAll(new ArrayList<>(Arrays.asList(adSize)));
			break;
			
			case "PixelFormat":
			title = "Select a PixelFormat";
			menus.addAll(new ArrayList<>(Arrays.asList(pixelFormat)));
			break;
			
			case "Variable":
			title = "Select a Variable";
			for (Pair<Integer, String> integerStringPair : projectDataManager.k(javaName)) {
				String variable = integerStringPair.second;
				String variableName = CustomVariableUtil.getVariableName(variable);
				menus.add(variableName != null ? variableName : variable);
			}
			break;
			
			case "Component":
			title = "Select a Component";
			for (ComponentBean componentBean : projectDataManager.e(javaName)) {
				menus.add(componentBean.componentId);
			}
			break;
			
			case "CustomVar":
			title = "Select a Custom Variable";
			for (String s : projectDataManager.e(javaName, 5)) {
				Matcher matcher = Pattern.compile("^(\\w+)[\\s]+(\\w+)").matcher(s);
				while (matcher.find()) {
					menus.add(matcher.group(2));
				}
			}
			for (String variable : projectDataManager.e(javaName, 6)) {
				String variableName = CustomVariableUtil.getVariableName(variable);
				menus.add(variableName != null ? variableName : variable);
			}
			break;
			
			default:
			Pair<String, ArrayList<String>> menuPair = defaultExtraMenu.getMenu(menu);
			title = menuPair.first;
			menus = new ArrayList<>(menuPair.second);
		}
		
		for (String menuArg : menus) {
			viewGroup.addView(logicEditor.e(menuArg));
		}
		setupSearchView(rootView, viewGroup);
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			if (viewGroup.getChildAt(i) instanceof RadioButton rb) {
				if (menu.getArgValue().toString().equals(Helper.getText(rb))) {
					rb.setChecked(true);
					break;
				}
			}
		}
		
		dialog.setTitle(title);
		dialog.setView(rootView);
		dialog.setPositiveButton(R.string.common_word_select, (v, which) -> {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				if (viewGroup.getChildAt(i) instanceof RadioButton rb) {
					if (rb.isChecked()) {
						logicEditor.a(menu, Helper.getText(rb));
					}
				}
			}
			v.dismiss();
		});
		dialog.setNegativeButton(R.string.common_word_cancel, null);
		dialog.setNeutralButton("Code Editor", (v, which) -> {
			AsdDialog editor = new AsdDialog(logicEditor);
			editor.setContent(menu.getArgValue().toString());
			editor.show();
			editor.setOnSaveClickListener(logicEditor, false, menu, editor);
			editor.setOnCancelClickListener(editor);
			v.dismiss();
		});
		dialog.show();
	}
	
	private ArrayList<String> getVarMenus(int type) {
		return projectDataManager.e(javaName, type);
	}
	
	
	private ArrayList<String> extractVars(String code) {
		
		ArrayList<String> list = new ArrayList<>();
		
		if (code == null) return list;
		
		String[] lines = code.split("\n");
		
		for (String line : lines) {
			
			line = line.trim();
			
			if (!line.startsWith("private")) continue;
			
			line = line.replace(";", "").trim();
			
			// normalize spacing
			line = line.replaceAll("\\s+", " ");
			
			String[] parts = line.split(" ");
			
			if (parts.length < 3) continue;
			
			String type = parts[1];
			String name = parts[2];
			
			// handle generics (HashMap<String, Object>)
			if (parts.length > 3 && parts[2].contains("<")) {
				type = parts[1] + " " + parts[2];
				name = parts[3];
			}
			
			if (name.contains("=")) {
				name = name.split("=")[0];
			}
			
			list.add(type + ":" + name.trim());
		}
		
		return list;
	}        
	
	private ArrayList<String> getComponentMenus(int type) {
		return projectDataManager.b(javaName, type);
	}
	
	private void asdDialog(Ss ss, String message) {
		MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(logicEditor);
		dialog.setTitle(R.string.logic_editor_title_enter_string_value);
		
		if (!isEmpty(message)) dialog.setMessage(message);
		
		View root = wB.a(logicEditor, R.layout.property_popup_input_text);
		EditText edittext = root.findViewById(R.id.ed_input);
		edittext.setImeOptions(EditorInfo.IME_ACTION_NONE);
		
		if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_USE_ASD_HIGHLIGHTER)) {
			new SimpleHighlighter(edittext);
		}
		edittext.setText(ss.getArgValue().toString());
		dialog.setView(root);
		
		dialog.setPositiveButton(R.string.common_word_save, (v, which) -> {
			String content = Helper.getText(edittext);
			if (!content.isEmpty() && content.charAt(0) == '@') {
				content = " " + content;
			}
			logicEditor.a(ss, content);
			v.dismiss();
		});
		dialog.setNegativeButton(R.string.common_word_cancel, null);
		dialog.setNeutralButton("Code Editor", (v, which) -> {
			AsdDialog asdDialog = new AsdDialog(logicEditor);
			asdDialog.setContent(Helper.getText(edittext));
			asdDialog.show();
			asdDialog.setOnSaveClickListener(logicEditor, false, ss, asdDialog);
			asdDialog.setOnCancelClickListener(asdDialog);
			v.dismiss();
		});
		dialog.show();
	}
	
	private void pathSelectorMenu(Ss ss) {
		String menuName = ss.getMenuName();
		ArrayList<String> markedPath = new ArrayList<>();
		
		mOptions.setSelectionMode(SelectionMode.BOTH);
		String path = null;
		if (menuName.equals("Assets")) {
			mOptions.setTitle("Select an Asset");
			path = String.format(ASSETS_PATH, sc_id);
			markedPath.add(0, path + ss.getArgValue().toString());
		} else if (menuName.equals("NativeLib")) {
			mOptions.setTitle("Select a Native library");
			path = String.format(NATIVE_PATH, sc_id);
			markedPath.add(0, path + ss.getArgValue().toString());
		}
		String[] strArr = path.split("/");
		String splitter = strArr[strArr.length - 1];
		mOptions.setInitialDirectory(path);
		FilePickerCallback callback = new FilePickerCallback() {
			@Override
			public void onFileSelected(File file) {
				logicEditor.a(ss, file.getAbsolutePath().split(splitter)[1]);
			}
		};
		FilePickerDialogFragment fpd = new FilePickerDialogFragment(mOptions, callback);
		fpd.show(logicEditor.getSupportFragmentManager(), "filePicker");
	}
	
	
	private static String normalizeType(String type) {
		if (type == null) return "";
		return type.replace(" ", "").trim();
	}
	
	private static String getBaseType(String type) {
		type = normalizeType(type);
		if (type.isEmpty()) return "";
		
		int index = type.indexOf('<');
		if (index != -1) {
			return type.substring(0, index).trim();
		}
		return type.trim();
	}
	
	private static String getSimpleName(String type) {
		if (type == null) return "";
		type = type.trim();
		
		int dot = type.lastIndexOf('.');
		return dot != -1 ? type.substring(dot + 1) : type;
	}
	
	private static boolean isArrayType(String type) {
		if (type == null) return false;
		return normalizeType(type).endsWith("[]");
	}
	
	private static String getArrayItemType(String type) {
		if (!isArrayType(type)) return "";
		String t = normalizeType(type);
		return t.substring(0, t.length() - 2).trim();
	}
	
	private static String unwrapArrayType(String type) {
		String current = normalizeType(type);
		if (current.isEmpty()) return "";
		
		while (isArrayType(current)) {
			String next = getArrayItemType(current);
			if (next.isEmpty() || next.equals(current)) break;
			current = next;
		}
		return current;
	}
	
	private static String getGenericPart(String type) {
		type = normalizeType(type);
		int start = type.indexOf('<');
		int end = type.lastIndexOf('>');
		if (start == -1 || end == -1 || end <= start) return "";
		return type.substring(start + 1, end).trim();
	}
	
	private static String getFirstGenericArg(String type) {
		String generic = getGenericPart(type);
		if (generic.isEmpty()) return "";
		
		int depth = 0;
		for (int i = 0; i < generic.length(); i++) {
			char c = generic.charAt(i);
			
			if (c == '<') depth++;
			else if (c == '>') depth--;
			else if (c == ',' && depth == 0) {
				return generic.substring(0, i).trim();
			}
		}
		return generic.trim();
	}
	
	private static boolean isSameSimpleType(String type, String... names) {
		if (type == null) return false;
		
		String base = getBaseType(type);
		String simple = getSimpleName(base);
		
		for (String name : names) {
			if (simple.equals(name) || base.equals(name) || type.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isStringType(String type) {
		if (type == null) return false;
		String base = getBaseType(type);
		return base.endsWith("String") || base.equals("CharSequence");
	}
	
	private static boolean isBooleanType(String type) {
		if (type == null) return false;
		String base = getBaseType(type);
		return base.equals("boolean") || base.equals("Boolean");
	}
	
	private static boolean isNumberType(String type) {
		if (type == null) return false;
		String base = getBaseType(type);
		
		return base.equals("int") || base.equals("double") ||
		base.equals("float") || base.equals("long") ||
		base.equals("short") || base.equals("byte") ||
		base.equals("Integer") || base.equals("Double") ||
		base.equals("Float") || base.equals("Long") ||
		base.equals("Short") || base.equals("Byte") ||
		base.equals("BigDecimal") || base.equals("BigInteger") ||
		base.equals("java.lang.Integer") ||
		base.equals("java.lang.Double") ||
		base.equals("java.lang.Float") ||
		base.equals("java.lang.Long") ||
		base.equals("java.lang.Short") ||
		base.equals("java.lang.Byte");
	}
	
	private static boolean isRealMap(String type) {
		if (type == null) return false;
		
		String base = getBaseType(type);
		
		return (base.endsWith("HashMap") || base.endsWith("Map"))
		&& !type.contains("ArrayList")
		&& !type.contains("List");
	}
	
	private static boolean isRealList(String type) {
		if (type == null) return false;
		
		String base = getBaseType(type);
		
		return (base.endsWith("ArrayList") || base.endsWith("List") || base.endsWith("LinkedList"))
		&& !type.contains("HashMap");
	}
	
	private static boolean isUriType(String type) {
		return isSameSimpleType(type, "Uri", "android.net.Uri");
	}
	
	private static boolean isIntentType(String type) {
		return isSameSimpleType(type, "Intent", "android.content.Intent");
	}
	
	private static boolean isFileType(String type) {
		return isSameSimpleType(type, "File", "java.io.File");
	}
	
	private static boolean isBitmapType(String type) {
		return isSameSimpleType(type, "Bitmap", "android.graphics.Bitmap");
	}
	
	private static boolean isViewType(String type) {
		return isSameSimpleType(type, "View", "android.view.View");
	}
	
	private static boolean isActivityType(String type) {
		return isSameSimpleType(type, "Activity", "android.app.Activity");
	}
	
	private static boolean isContextType(String type) {
		String simple = getSimpleName(getBaseType(type));
		return simple.equals("Context")
		|| simple.equals("Activity")
		|| simple.equals("Service")
		|| simple.equals("Application")
		|| simple.equals("ContextWrapper");
	}
	
	private static boolean isBundleType(String type) {
		return isSameSimpleType(type, "Bundle", "android.os.Bundle");
	}
	
	private static boolean isJSONObjectType(String type) {
		return isSameSimpleType(type, "JSONObject", "org.json.JSONObject");
	}
	
	private static boolean isJSONArrayType(String type) {
		return isSameSimpleType(type, "JSONArray", "org.json.JSONArray");
	}
	
	private static boolean isDrawableType(String type) {
		return isSameSimpleType(type, "Drawable", "android.graphics.drawable.Drawable");
	}
	
	
	
	private static boolean matchesType(String type, String mode) {
		if (type == null || mode == null) return false;
		
		if (isArrayType(type) || isRealList(type)) return false;
		
		switch (mode) {
			case "String":
			return isStringType(type);
			
			case "Number":
			return isNumberType(type);
			
			case "Boolean":
			return isBooleanType(type);
			
			case "Map":
			return isRealMap(type);
			
			case "Object":
			case "ObjectX":
			return true;
			
			case "Uri":
			return isUriType(type);
			
			case "Intent":
			return isIntentType(type);
			
			case "File":
			return isFileType(type);
			
			case "Bitmap":
			return isBitmapType(type);
			
			case "View":
			return isViewType(type);
			
			case "Activity":
			return isActivityType(type);
			
			case "Context":
			return isContextType(type);
			
			case "Bundle":
			return isBundleType(type);
			
			case "JSONObject":
			return isJSONObjectType(type);
			
			case "JSONArray":
			return isJSONArrayType(type);
			
			case "Drawable":
			return isDrawableType(type);
		}
		
		return false;
	}
	
	@NonNull
	public ArrayList<String> getDynamicMenus(String mode, String javaName, eC projectDataManager) {
		LinkedHashSet<String> menus = new LinkedHashSet<>();
		
		switch (mode) {
			case "Number":
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_NUMBER));
			break;
			
			case "Boolean":
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_BOOLEAN));
			break;
			
			case "String":
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_STRING));
			break;
			
			case "Map":
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_MAP));
			break;
			
			case "Object":
			case "ObjectX":
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_NUMBER));
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_BOOLEAN));
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_STRING));
			menus.addAll(projectDataManager.e(javaName, VARIABLE_TYPE_MAP));
			break;
		}
		
		ArrayList<String> customVars = projectDataManager.e(javaName, 6);
		ArrayList<ComponentBean> components = projectDataManager.e(javaName);
		
		for (String variable : customVars) {
			String type = CustomVariableUtil.getVariableType(variable);
			String name = CustomVariableUtil.getVariableName(variable);
			
			if (type == null || name == null) continue;
			
			if (matchesType(type, mode)) {
				menus.add(name);
			}
		}
		
		for (ComponentBean comp : components) {
			String build = ComponentsHandler.getBuildClassById(comp.type);
			if (build == null) continue;
			
			if (matchesType(build, mode)) {
				menus.add(comp.componentId);
			}
		}
		
		return new ArrayList<>(menus);
	}
	private static boolean matchesListType(String type, int listType) {
		if (type == null) return false;
		
		String base = getBaseType(type);
		
		// real Java lists only
		if (!isRealList(type)) return false;
		
		switch (listType) {
			case LIST_TYPE_STRING:
			return type.contains("String")
			|| type.contains("CharSequence");
			
			case LIST_TYPE_NUMBER:
			return type.contains("Double")
			|| type.contains("Integer")
			|| type.contains("Float")
			|| type.contains("Long")
			|| type.contains("Short")
			|| type.contains("Byte");
			
			case LIST_TYPE_MAP:
			return (type.contains("HashMap") || type.contains("Map"))
			&& type.contains("String")
			&& (type.contains("Object") || type.contains("java.lang.Object"));
		}
		
		return false;
	}
	
	@NonNull
	private ArrayList<String> getListMenus(int listType) {
		LinkedHashSet<String> menus = new LinkedHashSet<>();
		
		menus.addAll(projectDataManager.d(javaName, listType));
		
		ArrayList<String> customVars = projectDataManager.e(javaName, 6);
		ArrayList<ComponentBean> components = projectDataManager.e(javaName);
		
		for (String variable : customVars) {
			String type = CustomVariableUtil.getVariableType(variable);
			String name = CustomVariableUtil.getVariableName(variable);
			
			if (type == null || name == null) continue;
			
			if (matchesListType(type, listType)) {
				menus.add(name);
			}
		}
		
		for (ComponentBean comp : components) {
			String build = ComponentsHandler.getBuildClassById(comp.type);
			if (build == null) continue;
			
			if (matchesListType(build, listType)) {
				menus.add(comp.componentId);
			}
		}
		
		return new ArrayList<>(menus);
	}
	
	private static boolean matchesArrayType(String type, String mode) {
		if (type == null || mode == null) return false;
		if (!isArrayType(type)) return false;
		
		String leafType = unwrapArrayType(type);
		if (leafType.isEmpty()) return false;
		
		switch (mode) {
			case "String":
			return isStringType(leafType);
			
			case "Number":
			return isNumberType(leafType);
			
			case "Boolean":
			return isBooleanType(leafType);
			
			case "Map":
			return isRealMap(leafType);
			
			case "Object":
			case "ObjectX":
			return true;
			
			case "Uri":
			return isUriType(leafType);
			
			case "Intent":
			return isIntentType(leafType);
			
			case "File":
			return isFileType(leafType);
			
			case "Bitmap":
			return isBitmapType(leafType);
			
			case "View":
			return isViewType(leafType);
			
			case "Activity":
			return isActivityType(leafType);
			
			case "Context":
			return isContextType(leafType);
			
			case "Bundle":
			return isBundleType(leafType);
			
			case "JSONObject":
			return isJSONObjectType(leafType);
			
			case "JSONArray":
			return isJSONArrayType(leafType);
			
			case "Drawable":
			return isDrawableType(leafType);
			
			case "List":
			return isRealList(leafType);
		}
		
		return false;
	}
	
	@NonNull
	public ArrayList<String> getArrayMenus(String mode, String javaName, eC projectDataManager) {
		LinkedHashSet<String> menus = new LinkedHashSet<>();
		
		ArrayList<String> customVars = projectDataManager.e(javaName, 6);
		ArrayList<ComponentBean> components = projectDataManager.e(javaName);
		
		for (String variable : customVars) {
			String type = CustomVariableUtil.getVariableType(variable);
			String name = CustomVariableUtil.getVariableName(variable);
			
			if (type == null || name == null) continue;
			
			if (matchesArrayType(type, mode)) {
				menus.add(name);
			}
		}
		
		for (ComponentBean comp : components) {
			String build = ComponentsHandler.getBuildClassById(comp.type);
			if (build == null) continue;
			
			if (matchesArrayType(build, mode)) {
				menus.add(comp.componentId);
			}
		}
		
		return new ArrayList<>(menus);
	}
}
