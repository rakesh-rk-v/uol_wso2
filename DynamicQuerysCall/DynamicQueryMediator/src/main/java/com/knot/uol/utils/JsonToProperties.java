package com.knot.uol.utils;

import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonToProperties extends AbstractMediator {

	@Override
	public boolean mediate(MessageContext mc) {
		String inputAttributes = (String) mc.getProperty("inputAttributes");
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(inputAttributes);
		return false;
	}
	public static void main(String[] args) {
        // Your XML payload
        String xmlPayload = "<soapenv:Envelope xmlns:cus=\"http://ericsson.com/services/ws_CIL_7/customerread\" xmlns:ses=\"http://ericsson.com/services/ws_CIL_7/sessionchange\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <soapenv:Header>\n" +
                "        <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                "            <wsse:UsernameToken wsu:Id=\"UsernameToken-56D2CD5FBD0197EF2717017598886421\">\n" +
                "                <wsse:Username>ADMX</wsse:Username>\n" +
                "                <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">ADMX</wsse:Password>\n" +
                "            </wsse:UsernameToken>\n" +
                "        </wsse:Security>\n" +
                "    </soapenv:Header>\n" +
                "    <soapenv:Body>\n" +
                "        <cus:inputAttributes>\n" +
                "            <cus:csId>518936</cus:csId>\n" +
                "            <cus:anotherElement>SomeValue</cus:anotherElement>\n" +
                "            <!-- Add more elements as needed -->\n" +
                "        </cus:inputAttributes>\n" +
                "        <cus:sessionChangeRequest>\n" +
                "            <ses:values>\n" +
                "                <ses:item>\n" +
                "                    <ses:key>BU_ID</ses:key>\n" +
                "                    <ses:value>2</ses:value>\n" +
                "                </ses:item>\n" +
                "            </ses:values>\n" +
                "        </cus:sessionChangeRequest>\n" +
                "    </cus:customerReadRequest>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        try {
            // Parse the XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlPayload)));

            // Find the element with the tag 'cus:inputAttributes'
            Element inputAttributesElement = (Element) document.getElementsByTagName("cus:inputAttributes").item(0);

            // Loop through child elements and set their values
            NodeList childNodes = inputAttributesElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode instanceof Element) {
                    Element childElement = (Element) childNode;
                    // Set dynamic values for each element
                    if ("cus:csId".equals(childElement.getTagName())) {
                        childElement.setTextContent("NEW_CS_ID_VALUE");
                    } else if ("cus:anotherElement".equals(childElement.getTagName())) {
                        childElement.setTextContent("NEW_ANOTHER_VALUE");
                    }
                    // Add more conditions as needed for other elements
                }
            }

            // Convert the modified XML back to a string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            String modifiedXml = writer.toString();

            System.out.println(modifiedXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void main2(String[] args) {
		String inputAttributes = "{\"sccode\":\"1\",\"submId\":\"1\",\"plcode\":\"1001\",\"rpcode\":\"1\"}";
		Gson gson = new Gson();
		 // Define the type of the Map
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
		 // Convert JSON string to a Map
        Map<String, String> keyValueMap = gson.fromJson(inputAttributes,mapType);
     // Display the key-value pairs
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
	
	}
}
