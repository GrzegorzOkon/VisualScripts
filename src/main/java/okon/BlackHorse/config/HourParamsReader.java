package okon.BlackHorse.config;

import okon.BlackHorse.AppException;
import okon.BlackHorse.Hour;
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

public class HourParamsReader {
    private static final Logger logger = LogManager.getLogger(HourParamsReader.class);

    public static List<Hour> readHourParams(File file) {
        Element root = parseXml(file);
        List<Hour> result = new ArrayList<>();
        NodeList scripts = root.getElementsByTagName("hour");
        if (scripts != null && scripts.getLength() > 0) {
            for (int i = 0; i < scripts.getLength(); i++) {
                Node script = scripts.item(i);
                if (script.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)script;
                    String alias = element.getElementsByTagName("alias").item(0).getTextContent();
                    String digits = element.getElementsByTagName("digits").item(0).getTextContent();
                    List<String> interfaceNames = new ArrayList<>();
                    for (int j = 0; j < element.getElementsByTagName("interface_name").getLength(); j++) {
                        interfaceNames.add(element.getElementsByTagName("interface_name").item(j).getTextContent());
                    }
                    result.add(new Hour(alias, digits, interfaceNames));
                }
            }
        }
        logger.debug("ReadHourParams(" + file.getName() + ") : OK");
        return result;
    }

    private static Element parseXml(File file) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(file);
            logger.debug("ParseXml(" + file.getName() + ") : OK");
            return document.getDocumentElement();
        } catch (Exception e) {
            logger.error("ParseXml(" + file.getName() + ") : " + e.getMessage());
            throw new AppException(e);
        }
    }
}
