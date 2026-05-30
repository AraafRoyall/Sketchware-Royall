package Araaf.Royall.events;

import pro.sketchware.R;

public final class EventHelper {

    private EventHelper() {
    }

    public static final class EventData {
        public final String name;
        public final String var;
        public final String listener;
        public final int icon;
        public final String desc;
        public final String blocks;
        public final String spec;
        public final String code;

        public EventData(String name, String var, String listener, int icon,
                         String desc, String blocks, String spec, String code) {
            this.name = name;
            this.var = var == null ? "" : var;
            this.listener = listener == null ? "" : listener;
            this.icon = icon;
            this.desc = desc == null ? "" : desc;
            this.blocks = blocks == null ? "" : blocks;
            this.spec = spec == null ? "" : spec;
            this.code = code == null ? "" : code;
        }
    }

    public static final EventData[] EVENTS = new EventData[] {
            new EventData(
                    "Add Source Directly",
                    "",
                    "",
                    R.drawable.addsourcedirectlyevent,
                    "Direct Inject java to Activity",
                    "",
                    "###",
                    "%s"
            )

            // add new events here
            // new EventData(...)
    };
}