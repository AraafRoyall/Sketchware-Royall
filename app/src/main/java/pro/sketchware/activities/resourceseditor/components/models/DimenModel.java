package pro.sketchware.activities.resourceseditor.components.models;

public class DimenModel {

    private String name;
    private String value;
    private String unit;

    public DimenModel(String name, String value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getDimenName() {
        return name;
    }

    public void setDimenName(String name) {
        this.name = name;
    }

    public String getDimenValue() {
        return value;
    }

    public void setDimenValue(String value) {
        this.value = value;
    }

    public String getDimenUnit() {
        return unit;
    }

    public void setDimenUnit(String unit) {
        this.unit = unit;
    }
}