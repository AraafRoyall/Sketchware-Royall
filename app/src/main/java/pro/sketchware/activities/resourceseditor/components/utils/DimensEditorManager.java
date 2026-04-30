package pro.sketchware.activities.resourceseditor.components.utils;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import pro.sketchware.activities.resourceseditor.components.models.DimenModel;

public class DimensEditorManager {

    public String contentPath;
    public boolean isDataLoadingFailed;
    public HashMap<Integer, String> notesMap = new HashMap<>();

    private static final Pattern DIMEN_PATTERN =
            Pattern.compile("^([+-]?(?:\\d+(?:\\.\\d+)?|\\.\\d+))(dp|sp)$", Pattern.CASE_INSENSITIVE);

    public void parseDimensXML(ArrayList<DimenModel> dimenList, String dimenXml) {
        isDataLoadingFailed = false;

        try {
            dimenList.clear();
            notesMap.clear();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(dimenXml)));
            document.getDocumentElement().normalize();

            NodeList childNodes = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);

                if (node.getNodeType() == Node.COMMENT_NODE) {
                    Comment comment = (Comment) node;
                    notesMap.merge(dimenList.size(), comment.getNodeValue().trim(), (a, b) -> a + "\n" + b);
                } else if (node.getNodeType() == Node.ELEMENT_NODE && "dimen".equals(node.getNodeName())) {
                    Element element = (Element) node;
                    String dimenName = element.getAttribute("name");
                    String rawValue = element.getTextContent().trim().replace(" ", "");

                    Matcher matcher = DIMEN_PATTERN.matcher(rawValue);
                    if (matcher.matches()) {
                        dimenList.add(new DimenModel(
                                dimenName,
                                matcher.group(1),
                                matcher.group(2).toLowerCase(Locale.US)
                        ));
                    } else {
                        dimenList.add(new DimenModel(dimenName, rawValue, "dp"));
                    }
                }
            }
        } catch (Exception e) {
            isDataLoadingFailed = !dimenXml.trim().isEmpty();
        }
    }

    public String convertListToXml(ArrayList<DimenModel> dimenList, HashMap<Integer, String> notesMap) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<resources>\n");

        for (int i = 0; i < dimenList.size(); i++) {
            if (notesMap.containsKey(i)) {
                for (String comment : notesMap.get(i).split("\n")) {
                    xmlBuilder.append("    <!-- ").append(comment).append(" -->\n");
                }
            }

            DimenModel dimenModel = dimenList.get(i);
            xmlBuilder.append("    <dimen name=\"")
                    .append(dimenModel.getDimenName())
                    .append("\">")
                    .append(dimenModel.getDimenValue())
                    .append(dimenModel.getDimenUnit())
                    .append("</dimen>\n");
        }

        if (notesMap.containsKey(dimenList.size())) {
            for (String comment : notesMap.get(dimenList.size()).split("\n")) {
                xmlBuilder.append("    <!-- ").append(comment).append(" -->\n");
            }
        }

        xmlBuilder.append("</resources>");
        return xmlBuilder.toString();
    }
}