import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Java Class that converts an XML(OSM) file to CSV for a better processing afterwards.
 */
public class Preprocessor {
    /**
     * Boolean method that converts an XML(OSM) file to CSV.
     * @param filePath  The path of input file.
     * @param outputPath    The path of the output file.
     * @return true it conversion was successful, false otherwise.
     */
    public boolean convertToCSV(String filePath,String outputPath)
    {
        try {
            //Parse XML file for conversion
            File OSMFile = new File(filePath);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();
            Document osmDoc = docBuilder.parse(OSMFile);
            osmDoc.getDocumentElement().normalize();

            //Initialize xPath
            XPath xPath =  XPathFactory.newInstance().newXPath();

            //Set xPath in XML file
            String path = "/osm/node";
            //Get node list in the specific path
            NodeList nodeList = (NodeList) xPath.compile(path).evaluate(
                    osmDoc, XPathConstants.NODESET);

            //Write metadata to .csv document
            FileWriter myWriter = new FileWriter(outputPath);
            myWriter.write("id,lat,lon\n");

            //Iterate through nodes and write the needed attributes of node in the csv file
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    String line = eElement.getAttribute("id")+','+eElement.getAttribute("lat")+' '+eElement.getAttribute("lon");
                    myWriter.write(line+ "\n");
                }
            }

            //Writing ended
            myWriter.close();

            //Inform that everything went well
            return true;

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean convertToTXT(String filePath,String outputPath)
    {
        try {
            //Parse XML file for conversion
            File OSMFile = new File(filePath);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();
            Document osmDoc = docBuilder.parse(OSMFile);
            osmDoc.getDocumentElement().normalize();

            //Initialize xPath
            XPath xPath =  XPathFactory.newInstance().newXPath();

            //Set xPath in XML file
            String path = "/osm/node";
            //Get node list in the specific path
            NodeList nodeList = (NodeList) xPath.compile(path).evaluate(
                    osmDoc, XPathConstants.NODESET);

            //Write metadata to .csv document
            FileWriter myWriter = new FileWriter(outputPath);

            //Iterate through nodes and write the needed attributes of node in the txt file
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    String line = eElement.getAttribute("lat")+' '+eElement.getAttribute("lon");
                    myWriter.write(line+ "\n");
                }
            }

            //Writing ended
            myWriter.close();

            //Inform that everything went well
            return true;

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args)
    {
        String filePath = "data/ask.csv";
        String outputPath = "data/ask_processed.txt";
        Preprocessor pre = new Preprocessor();
        //Convert to CSV
        if (pre.convertToCSV(filePath,outputPath))
            System.out.println("Successful conversion!");
        else
            System.out.println("An error occurred!");
        //Convert to TXT
//        if (pre.convertToTXT(filePath,outputPath))
//            System.out.println("Successful conversion!");
//        else
//            System.out.println("An error occurred!");
    }
}