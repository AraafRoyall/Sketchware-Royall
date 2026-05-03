package pro.sketchware.activities.resourceseditor.components.models;

public class DimenModel {

    private String dimenName;
    private String dimenValue;
    private String dimenUnit;

    public DimenModel(String name, String value, String unit) {
        this.dimenName = name;
        this.dimenValue = value;
        this.dimenUnit = unit;
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
        this.dimenUnit = dimenUnit;
    }
}