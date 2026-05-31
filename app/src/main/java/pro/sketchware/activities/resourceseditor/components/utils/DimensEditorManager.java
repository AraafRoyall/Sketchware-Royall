package pro.sketchware.activities.resourceseditor.components.utils;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import javax.xml.parsers.*;

import pro.sketchware.activities.resourceseditor.components.models.DimenModel;

public class DimensEditorManager {

    public boolean isDataLoadingFailed;
    public HashMap<Integer, String> notesMap = new HashMap<>();

    private final Pattern pattern = Pattern.compile("([0-9.]+)(dp|sp)");

    public ArrayList<DimenModel> parse(String xml) {

        isDataLoadingFailed = false;
        ArrayList<DimenModel> list = new ArrayList<>();
        notesMap.clear();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList nodes = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {

                Node node = nodes.item(i);

                if (node.getNodeType() == Node.COMMENT_NODE) {
                    notesMap.put(list.size(), node.getNodeValue());
                }

                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("dimen")) {

                    Element el = (Element) node;
                    String name = el.getAttribute("name");
                    String val = el.getTextContent().trim();

                    Matcher m = pattern.matcher(val);

                    if (m.matches()) {
                        list.add(new DimenModel(name, m.group(1), m.group(2)));
                    } else {
                        list.add(new DimenModel(name, val, "dp"));
                    }
                }
            }

        } catch (Exception e) {
            isDataLoadingFailed = !xml.trim().isEmpty();
        }

        return list;
    }

    public String build(ArrayList<DimenModel> list, HashMap<Integer, String> notes) {

        StringBuilder sb = new StringBuilder("<resources>\n");

        for (int i = 0; i < list.size(); i++) {

            if (notes.containsKey(i)) {
                sb.append("    <!-- ").append(notes.get(i)).append(" -->\n");
            }

            DimenModel m = list.get(i);

            sb.append("    <dimen name=\"")
                    .append(m.getDimenName())
                    .append("\">")
                    .append(m.getDimenValue())
                    .append(m.getDimenUnit())
                    .append("</dimen>\n");
        }

        sb.append("</resources>");
        return sb.toString();
    }
}