package okon.VisualScripts.config;

import okon.VisualScripts.AppException;
import okon.VisualScripts.Script;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptParamsReader {
    private static final Logger logger = LogManager.getLogger(ScriptParamsReader.class);

    public static List<Script> readScriptParams(File file) {
        Element root = parseXml(file);
        List<Script> result = new ArrayList<>();
        NodeList scripts = root.getElementsByTagName("script");
        if (scripts != null && scripts.getLength() > 0) {
            for (int i = 0; i < scripts.getLength(); i++) {
                Node script = scripts.item(i);
                if (script.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)script;
                    String interfaceName = element.getElementsByTagName("interface_name").item(0).getTextContent();
                    String alias = element.getElementsByTagName("alias").item(0).getTextContent();
                    String filename = element.getElementsByTagName("filename").item(0).getTextContent();
                    String path = element.getElementsByTagName("path").item(0).getTextContent();
                    String engine = element.getElementsByTagName("engine").item(0).getTextContent();
                    result.add(new Script(interfaceName, alias, filename, path, engine));
                }
            }
        }
        logger.info("ReadScriptParams(" + file.getName() + ") : OK");
        return result;
    }

    private static Element parseXml(File file) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(file);
            logger.info("ParseXml(" + file.getName() + ") : OK");
            return document.getDocumentElement();
        } catch (Exception e) {
            logger.error("ParseXml(" + file.getName() + ") : " + e.getMessage());
            throw new AppException(e);
        }
    }
}