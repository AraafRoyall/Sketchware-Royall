package pro.sketchware.activities.resourceseditor.components.models;

public class DimenModel {
    private String dimenName;
    private String dimenValue;
    private String dimenUnit;

    public DimenModel(String dimenName, String dimenValue) {
        this(dimenName, dimenValue, "dp");
    }

    public DimenModel(String dimenName, String dimenValue, String dimenUnit) {
        this.dimenName = dimenName;
        this.dimenValue = dimenValue;
        setDimenUnit(dimenUnit);
    }

    public String getDimenName() {
        return dimenName;
    }

    public void setDimenName(String dimenName) {
        this.dimenName = dimenName;
    }

    public String getDimenValue() {
        return dimenValue;
    }

    public void setDimenValue(String dimenValue) {
        this.dimenValue = dimenValue;
    }

    public String getDimenUnit() {
        return dimenUnit;
    }

    public void setDimenUnit(String dimenUnit) {
        this.dimenUnit = "sp".equalsIgnoreCase(dimenUnit) ? "sp" : "dp";
    }
}