package Araaf.royall.components;

import pro.sketchware.R;

public final class ComponentHelper {

    private ComponentHelper() {
    }

    public static final class ComponentData {
        public final String name;
        public final String id;
        public final String icon;
        public final String varName;
        public final String typeName;
        public final String buildClass;
        public final String clazz;
        public final String description;
        public final String url;
        public final String additionalVar;
        public final String defineAdditionalVar;
        public final String imports;

        public ComponentData(String name, String id, String icon, String varName,
                             String typeName, String buildClass, String clazz,
                             String description, String url, String additionalVar,
                             String defineAdditionalVar, String imports) {
            this.name = name;
            this.id = id;
            this.icon = icon;
            this.varName = varName;
            this.typeName = typeName;
            this.buildClass = buildClass;
            this.clazz = clazz;
            this.description = description;
            this.url = url;
            this.additionalVar = additionalVar;
            this.defineAdditionalVar = defineAdditionalVar;
            this.imports = imports;
        }
    }

    public static final ComponentData[] COMPONENTS = {

            new ComponentData(
                    "My Component",
                    "100",
                    "2131165298",
                    "myVar",
                    "MyType",
                    "MyClass",
                    "Component.MyClass",
                    "My description",
                    "",
                    "",
                    "",
                    ""
            )

            // add more components here
    };
}