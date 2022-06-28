import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class CsvToXml {

    // add input and output file path
    final String INPUT_FILE = "C:\\Users\\buse\\Desktop\\CsvToXml2\\src\\input.csv";
    final String OUTPUT_FILE = "C:\\Users\\buse\\Desktop\\CsvToXml2\\src\\output.xml";
    final String FIRST_ELEMENT = "information";
    protected DocumentBuilderFactory domFactory = null;
    protected DocumentBuilder domBuilder = null;

    public CsvToXml() {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException exp) {
            System.err.println(exp);
        } catch (FactoryConfigurationError exp) {
            System.err.println(exp);
        } catch (Exception exp) {
            System.err.println(exp);
        }
    }

    public static void main(String[] args) {
        CsvToXml csVtoXML = new CsvToXml();
        csVtoXML.convert(csVtoXML.INPUT_FILE, csVtoXML.OUTPUT_FILE);
    }

    /**
     * This method converts the given CSV file into an XML document
     */
    public int convert(String csvFileName, String xmlFileName) {
        int rowCount = -1;
        try {
            Document newDoc = domBuilder.newDocument();
            Element rootElem = newDoc.createElement(FIRST_ELEMENT);
            newDoc.appendChild(rootElem);
            BufferedReader csvFileReader;
            csvFileReader = new BufferedReader(new FileReader(csvFileName));
            int fieldCount = 0;
            String[] csvFields = null;
            StringTokenizer stringTokenizer = null;
            Element infoChild = newDoc.createElement("info");
            rootElem.appendChild(infoChild);
            Element dataChild = newDoc.createElement("data");
            rootElem.appendChild(dataChild);
            String firstcurrLine = csvFileReader.readLine();

            if (firstcurrLine != null) {
                /* Separate fields based on commas */
                stringTokenizer = new StringTokenizer(firstcurrLine, ",");
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    csvFields = new String[fieldCount];
                    int i = 0;
                    while (stringTokenizer.hasMoreElements()) {
                        csvFields[i++] = String.valueOf(stringTokenizer.nextElement());
                    }
                }
            } else {
                System.out.println("Nothing to parse");
            }
            int line = 1;
            while ((firstcurrLine = csvFileReader.readLine()) != null) {
                stringTokenizer = new StringTokenizer(firstcurrLine, ",");
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    int i = 0;
                    while (stringTokenizer.hasMoreElements()) {
                        try {
                            String currValue = String.valueOf(stringTokenizer.nextElement());
                            Element currElem = newDoc.createElement(csvFields[i++]);
                            currElem.appendChild(newDoc.createTextNode(currValue));
                            infoChild.appendChild(currElem);
                        } catch (Exception exp) {

                        }
                    }
                    rowCount++;
                }
                line++;
                if (line == 2) {
                    break;
                }
            }

            String thirdcurrLine = csvFileReader.readLine();
            if (thirdcurrLine != null) {
                stringTokenizer = new StringTokenizer(thirdcurrLine, ",");
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    csvFields = new String[fieldCount];
                    int i = 0;
                    while (stringTokenizer.hasMoreElements()) {
                        csvFields[i++] = String.valueOf(stringTokenizer.nextElement());
                    }
                }
            } else {
                System.out.println("Nothing to parse");
            }

            while ((thirdcurrLine = csvFileReader.readLine()) != null) {
                stringTokenizer = new StringTokenizer(thirdcurrLine, ",");
                fieldCount = stringTokenizer.countTokens();
                if (fieldCount > 0) {
                    Element rowElem = newDoc.createElement("transaction");
                    Element transactionInfoChild = newDoc.createElement("transactionInfo");
                    rowElem.appendChild(transactionInfoChild);

                    String secondHeaderFirstColumnValue = String.valueOf(stringTokenizer.nextElement());
                    rowElem.setAttribute("secondHeaderFirstColumn", secondHeaderFirstColumnValue);
                    Element dateChild = newDoc.createElement("date");

                    int i = 1;
                    while (stringTokenizer.hasMoreElements()) {
                        try {
                            String currValue = String.valueOf(stringTokenizer.nextElement());
                            Element currElem = newDoc.createElement(csvFields[i++]);
                            currElem.appendChild(newDoc.createTextNode(currValue));
                            if (currElem.getTagName().equals("secondHeaderThirdColumn")) {
                                transactionInfoChild.appendChild(dateChild);
                                dateChild.appendChild(currElem);
                            } else {
                                transactionInfoChild.appendChild(currElem);
                            }
                        } catch (Exception exp) {

                        }
                    }
                    dataChild.appendChild(rowElem);
                    rowCount++;
                }
            }
            csvFileReader.close();
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer transformer = tranFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source src = new DOMSource(newDoc);
            Result dest = new StreamResult(new File(xmlFileName));
            transformer.transform(src, dest);
            rowCount++;
        } catch (IOException exp) {
            System.err.println(exp);
        } catch (Exception exp) {
            System.err.println(exp);
        }
        return rowCount;
    }
}
