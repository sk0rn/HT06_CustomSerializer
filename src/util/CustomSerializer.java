package util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.lang.reflect.Field;


public class CustomSerializer {

    private Object o;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder docBuilder;
    private TransformerFactory transformerFactory;
    private Transformer transformer;

    public CustomSerializer() throws ParserConfigurationException, TransformerConfigurationException {
        this.dbf = DocumentBuilderFactory.newInstance();
        this.docBuilder = dbf.newDocumentBuilder();
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer();
    }

    public void serialize(Object o, String file) throws TransformerException {
        this.o = o;
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement(o.getClass().getSimpleName());
        doc.appendChild(root);
        for (Class cl = o.getClass(); !cl.getSimpleName().equals("Object"); cl = cl.getSuperclass()) {
            for (Field field : cl.getDeclaredFields()) {
                field.setAccessible(true);
                Element node = doc.createElement(field.getName());
                Attr attr = doc.createAttribute("type");
                attr.setValue(String.valueOf(field.getType().getSimpleName()));
                node.setAttributeNode(attr);
                try {
                    node.appendChild(doc.createTextNode(String.valueOf(field.get(o))));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                root.appendChild(node);
            }
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(file));
        transformer.transform(source, result);
        System.out.println("File created on: " + file);
    }

    public Object deserialize(String file) {
        File xmlFile = new File(file);
        try {
            Document doc = docBuilder.parse(xmlFile);
            Class clazz = Class.forName("model." + doc.getDocumentElement().getNodeName());
            o = clazz.newInstance();
            if (doc.hasChildNodes()) {
                iterateNode(doc.getChildNodes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    private void iterateNode(NodeList list) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < list.getLength(); i++) {
            Node tempNode = list.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String name = tempNode.getNodeName();
                String value = tempNode.getTextContent();
                String attr = "";
                if (tempNode.getAttributes().item(0) != null) {
                    attr = tempNode.getAttributes().item(0).toString();
                }
                if (!name.toUpperCase().equals(o.getClass().getSimpleName().toUpperCase())) {
                    Field field = o.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    switch (attr) {
                        case "type=\"int\"":
                        case "type=\"Integer\"":
                            field.set(o, Integer.valueOf(value));
                            break;
                        case "type=\"boolean\"":
                        case "type=\"Boolean\"":
                            field.set(o, Boolean.valueOf(value));
                            break;
                        case "type=\"byte\"":
                        case "type=\"Byte\"":
                            field.set(o, Byte.valueOf(value));
                            break;
                        case "type=\"short\"":
                        case "type=\"Short\"":
                            field.set(o, Short.valueOf(value));
                            break;
                        case "type=\"char\"":
                        case "type=\"Character\"":
                            field.set(o, Character.valueOf(value.charAt(0)));
                            break;
                        case "type=\"float\"":
                        case "type=\"Float\"":
                            field.set(o,Float.valueOf(value));
                            break;
                        case "type=\"double\"":
                        case "type=\"Double\"":
                            field.set(o,Double.valueOf(value));
                            break;
                        case "type=\"long\"":
                        case "type=\"Long\"":
                            field.set(o,Long.valueOf(value));
                            break;
                        default:
                            field.set(o, value);
                    }
                }
                if (tempNode.hasChildNodes()) {
                    iterateNode(tempNode.getChildNodes());
                }
            }
        }
    }
}
