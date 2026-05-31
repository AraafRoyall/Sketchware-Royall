package mod.hilal.saif.components;

import static mod.hilal.saif.events.EventsHandler.capitalize;

import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.besome.sketch.beans.ComponentBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import a.a.a.Lx;
import mod.hey.studios.util.Helper;
import mod.jbk.util.OldResourceIdMapper;
import pro.sketchware.R;
import pro.sketchware.SketchApplication;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;

import Araaf.Royall.Components.ComponentHelper;

public class ComponentsHandler {

    private static ArrayList<HashMap<String, Object>> cachedCustomComponents = new ArrayList<>();

    private static final HashMap<Integer, HashMap<String, Object>> fastMap = new HashMap<>();
    private static final HashMap<String, HashMap<String, Object>> typeNameMap = new HashMap<>();

    private static boolean ready = false;
    private static final boolean DEBUG = true;

    private ComponentsHandler() {
    }

    private static void ensureInit() {
        if (!ready) {
            refreshCachedCustomComponents();
        }
    }

    private static void logError(String msg) {
        if (DEBUG) {
            SketchwareUtil.toastError(msg, Toast.LENGTH_SHORT);
        }
    }

    private static String getS(HashMap<String, Object> m, String k, String def) {
        if (m == null) return def;
        Object v = m.get(k);
        return (v instanceof String) ? (String) v : def;
    }

    private static HashMap<String, Object> byId(int id) {
        ensureInit();
        return fastMap.get(id);
    }

    private static HashMap<String, Object> byName(String name) {
        ensureInit();
        return typeNameMap.get(name);
    }

    private static ComponentHelper.ComponentData byBuiltInId(int id) {
        for (ComponentHelper.ComponentData c : ComponentHelper.COMPONENTS) {
            if (c == null) continue;
            try {
                if (Integer.parseInt(c.id) == id) return c;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static ComponentHelper.ComponentData byBuiltInTypeName(String typeName) {
        for (ComponentHelper.ComponentData c : ComponentHelper.COMPONENTS) {
            if (c != null && typeName != null && typeName.equals(c.typeName)) {
                return c;
            }
        }
        return null;
    }

    private static int resolveIconValue(String iconValue) {
        if (TextUtils.isEmpty(iconValue)) {
            return R.drawable.color_new_96;
        }

        try {
            return OldResourceIdMapper.getDrawableFromOldResourceId(Integer.parseInt(iconValue));
        } catch (Exception ignored) {
        }

        int resId = SketchApplication.getContext().getResources().getIdentifier(
                iconValue,
                "drawable",
                SketchApplication.getContext().getPackageName()
        );

        if (resId != 0) return resId;

        return R.drawable.color_new_96;
    }

    private static HashMap<String, Object> toMap(ComponentHelper.ComponentData c) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", c.name);
        map.put("id", c.id);
        map.put("icon", c.icon);
        map.put("varName", c.varName);
        map.put("typeName", c.typeName);
        map.put("buildClass", c.buildClass);
        map.put("class", c.clazz);
        map.put("description", c.description);
        map.put("url", c.url);
        map.put("additionalVar", c.additionalVar);
        map.put("defineAdditionalVar", c.defineAdditionalVar);
        map.put("imports", c.imports);
        return map;
    }

    private static void buildMaps() {
        fastMap.clear();
        typeNameMap.clear();

        HashSet<Integer> usedIds = new HashSet<>();
        HashSet<String> usedNames = new HashSet<>();

        for (ComponentHelper.ComponentData c : ComponentHelper.COMPONENTS) {
            if (c == null) continue;

            try {
                if (TextUtils.isEmpty(c.id) || TextUtils.isEmpty(c.typeName)) {
                    continue;
                }

                int id = Integer.parseInt(c.id);

                if (usedIds.contains(id)) {
                    logError("Duplicate built-in ID " + id + " for " + c.typeName);
                    continue;
                }

                if (usedNames.contains(c.typeName)) {
                    logError("Duplicate built-in typeName '" + c.typeName + "'");
                    continue;
                }

                usedIds.add(id);
                usedNames.add(c.typeName);

                HashMap<String, Object> map = toMap(c);
                fastMap.put(id, map);
                typeNameMap.put(c.typeName, map);

            } catch (Exception e) {
                logError("Parse error in built-in component: " + c.typeName);
            }
        }

        for (int i = 0; i < cachedCustomComponents.size(); i++) {
            HashMap<String, Object> comp = cachedCustomComponents.get(i);

            if (comp == null) {
                logError("Null component at index " + i);
                continue;
            }

            if (!isValidComponent(comp)) {
                logError("Invalid structure at index " + i);
                continue;
            }

            try {
                String idStr = (String) comp.get("id");
                String type = (String) comp.get("typeName");

                if (TextUtils.isEmpty(idStr) || TextUtils.isEmpty(type)) {
                    logError("Missing id/type at index " + i);
                    continue;
                }

                int id = Integer.parseInt(idStr);

                if (usedIds.contains(id)) {
                    logError("Duplicate ID " + id + " at index " + i);
                    continue;
                }

                if (usedNames.contains(type)) {
                    logError("Duplicate typeName '" + type + "' at index " + i);
                    continue;
                }

                usedIds.add(id);
                usedNames.add(type);

                fastMap.put(id, comp);
                typeNameMap.put(type, comp);

            } catch (Exception e) {
                logError("Parse error at index " + i);
            }
        }
    }

    public static int id(String name) {
        if ("AsyncTask".equals(name)) return 36;

        ComponentHelper.ComponentData builtIn = byBuiltInTypeName(name);
        if (builtIn != null) {
            try {
                return Integer.parseInt(builtIn.id);
            } catch (Exception ignored) {
            }
        }

        HashMap<String, Object> c = byName(name);

        if (c == null) {
            logError("Component not found: " + name);
            return -1;
        }

        try {
            return Integer.parseInt(getS(c, "id", "-1"));
        } catch (Exception e) {
            logError("Invalid ID for " + name);
            return -1;
        }
    }

    public static String typeName(int id) {
        if (id == 36) return "AsyncTask";

        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) return builtIn.typeName;

        HashMap<String, Object> c = byId(id);

        if (c == null) {
            logError("typeName not found for id " + id);
        }

        return getS(c, "typeName", "");
    }

    public static String name(int id) {
        if (id == 36) return "AsyncTask";

        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) return builtIn.name;

        HashMap<String, Object> c = byId(id);

        if (c == null) {
            logError("name not found for id " + id);
        }

        return getS(c, "name", "component");
    }

    public static int icon(int id) {
        if (id == 36) return R.drawable.ic_cycle_color_48dp;

        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) {
            return resolveIconValue(builtIn.icon);
        }

        try {
            return resolveIconValue(getS(byId(id), "icon", ""));
        } catch (Exception e) {
            logError("Invalid icon for id " + id);
            return R.drawable.color_new_96;
        }
    }

    public static String description(int id) {
        int res = ComponentBean.getDescStrResource(id);
        return res != 0
                ? SketchApplication.getContext().getString(res)
                : description2(id);
    }

    public static String description2(int id) {
        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) return builtIn.description;

        return getS(byId(id), "description", "new component");
    }

    public static String docs(int id) {
        if (id == 36) return "";

        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) return builtIn.url;

        return getS(byId(id), "url", "");
    }

    public static String getBuildClassById(int id) {
        if (id == 36) return "AsyncTask";

        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) return builtIn.buildClass;

        return getS(byId(id), "buildClass", "");
    }

    public static void add(ArrayList<ComponentBean> list) {
        ensureInit();

        HashSet<Integer> used = new HashSet<>();

        list.add(new ComponentBean(36));
        used.add(36);

        for (ComponentHelper.ComponentData c : ComponentHelper.COMPONENTS) {
            if (c == null) continue;
            try {
                int id = Integer.parseInt(c.id);
                if (!used.contains(id)) {
                    list.add(new ComponentBean(id));
                    used.add(id);
                }
            } catch (Exception e) {
                logError("Invalid built-in component in add()");
            }
        }

        for (HashMap<String, Object> c : cachedCustomComponents) {
            try {
                int id = Integer.parseInt(getS(c, "id", "-1"));
                if (!used.contains(id)) {
                    list.add(new ComponentBean(id));
                    used.add(id);
                }
            } catch (Exception e) {
                logError("Invalid component in add()");
            }
        }
    }

    public static String getTypeName(int id) {
        if (id == 36) return "#";

        ComponentHelper.ComponentData builtIn = byBuiltInId(id);
        if (builtIn != null) return builtIn.typeName;

        return getS(byId(id), "typeName", "");
    }

    public static String getVarName(String name) {
        ComponentHelper.ComponentData builtIn = byBuiltInTypeName(name);
        if (builtIn != null) return builtIn.varName;

        return getS(byName(name), "varName", name);
    }

    @NonNull
    public static String getClassByTypeName(@NonNull String name) {
        if (name.equals("AsyncTask")) return "Component.AsyncTask";

        ComponentHelper.ComponentData builtIn = byBuiltInTypeName(name);
        if (builtIn != null) return builtIn.clazz;

        return getS(byName(name), "class", "Component");
    }

    public static String extraVar(String name, String code, String varName) {
        ComponentHelper.ComponentData builtIn = byBuiltInTypeName(name);
        String add = builtIn != null ? builtIn.additionalVar : getS(byName(name), "additionalVar", "");

        if (TextUtils.isEmpty(add)) return code;

        return code + "\n" +
                add.replace("###", varName)
                        .replace("$name", varName)
                        .replace("$Name", capitalize(varName))
                        .replace("$NAME", varName.toUpperCase());
    }

    public static String defineExtraVar(String name, String varName) {
        ComponentHelper.ComponentData builtIn = byBuiltInTypeName(name);
        String def = builtIn != null ? builtIn.defineAdditionalVar : getS(byName(name), "defineAdditionalVar", "");

        if (TextUtils.isEmpty(def)) return "";

        return def.replace("###", varName)
                .replace("$name", varName)
                .replace("$Name", capitalize(varName))
                .replace("$NAME", varName.toUpperCase());
    }

    public static void getImports(String name, ArrayList<String> list) {
        ComponentHelper.ComponentData builtIn = byBuiltInTypeName(name);
        String imp = builtIn != null ? builtIn.imports : getS(byName(name), "imports", "");

        if (!TextUtils.isEmpty(imp)) {
            list.addAll(Arrays.asList(imp.split("\n")));
        }
    }

    public static String getPath() {
        return FileUtil.getExternalStorageDir() + "/.sketchware/data/system/component.json";
    }

    private static ArrayList<HashMap<String, Object>> readCustomComponents() {
        try {
            if (!FileUtil.isExistFile(getPath())) {
                logError("Component JSON not found");
                return new ArrayList<>();
            }

            ArrayList<HashMap<String, Object>> data =
                    new Gson().fromJson(FileUtil.readFile(getPath()), Helper.TYPE_MAP_LIST);

            if (data == null) {
                logError("Invalid JSON structure");
                return new ArrayList<>();
            }

            return data;

        } catch (Exception e) {
            logError("JSON read error");
            return new ArrayList<>();
        }
    }

    public static void refreshCachedCustomComponents() {
        cachedCustomComponents = readCustomComponents();
        buildMaps();
        ready = true;
    }

    public static boolean isValidComponent(Map<String, Object> map) {
        return map != null &&
                map.containsKey("name") &&
                map.containsKey("id") &&
                map.containsKey("icon") &&
                map.containsKey("varName") &&
                map.containsKey("typeName") &&
                map.containsKey("buildClass") &&
                map.containsKey("class") &&
                map.containsKey("description") &&
                map.containsKey("url") &&
                map.containsKey("additionalVar") &&
                map.containsKey("defineAdditionalVar") &&
                map.containsKey("imports");
    }

    public static boolean isValidComponentList(List<? extends Map<String, Object>> list) {
        if (list == null) return false;

        for (Map<String, Object> map : list) {
            if (!isValidComponent(map)) return false;
        }

        return true;
    }

    public static Pair<Optional<String>, List<HashMap<String, Object>>> readComponents(String path) {
        String content = FileUtil.readFile(path);

        if (content.isEmpty() || content.equals("[]")) {
            return new Pair<>(Optional.of("Empty file"), Collections.emptyList());
        }

        ArrayList<HashMap<String, Object>> data =
                new Gson().fromJson(content, Helper.TYPE_MAP_LIST);

        if (data == null || data.isEmpty() || !isValidComponentList(data)) {
            return new Pair<>(Optional.of("Invalid JSON"), Collections.emptyList());
        }

        return new Pair<>(Optional.empty(), data);
    }
}