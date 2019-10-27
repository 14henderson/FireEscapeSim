package fireescapedemo;

import java.util.HashMap;

public abstract class MapObject {
    public static Building mainBuilding;
    public static boolean canEdit;
    private static final transient HashMap<String, String> applicationColours = new HashMap<String, String>() {{
        put("WHITESMOKE","FFFFFF");
        put("GREY", "B0B0B0");
        put("AQUAMARINE", "00FFBF");
        put("RED", "FF0000");
    }};


    public abstract void render();

    public static void enableBuild() {canEdit = true;}
    public static void disableBuild() {canEdit = false;}
    public static boolean isBuildEnabled(){return canEdit;}
    public String getColor(String key){
        return applicationColours.get(key);
    }
}
