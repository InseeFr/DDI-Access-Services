package fr.insee.rmes.transfoxsl.service;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.FragmentData;
import fr.insee.rmes.tocolecticaapi.models.NamespaceContextMap;
import fr.insee.rmes.utils.XMLUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class XsltTransformationService {

    public static final String DISALLOW_DOCTYPE_DECL1 = "http://apache.org/xml/features/disallow-doctype-decl";
    public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    public static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final String VERSION = "version";
    static final byte[] DATA_TAG_BEGIN = "<data>".getBytes();
    static final byte[] DATA_TAG_END = "</data>".getBytes();

    private final Processor processor;

    public XsltTransformationService() {
        this.processor = new Processor(false);
    }


    public String transformFileForComplexList(
            MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre,
            String principale,
            List<String> secondaire,
            List<String> labelSecondaire
    ) throws RmesException {

        DDIProcessor ddiProcessor = (inputData, xsltStream) ->
                transformToXmlForComplexList(inputData, xsltStream, idValue, nomenclatureName, suggesterDescription, timbre, version, principale, secondaire, labelSecondaire);
        return doTransformFile(file, idepUtilisateur, "/jsonToDdiXmlForComplexList.xsl", ddiProcessor, "/DDIComplexxmltojson.xsl");
    }

    public String transformFile(
            @NonNull MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre
    ) throws RmesException {

        DDIProcessor ddiProcessor = (inputData, xsltStream) -> transformToXml(inputData, xsltStream, idValue, nomenclatureName, suggesterDescription, timbre, version);
        return doTransformFile(file, idepUtilisateur, "/jsontoDDIXML.xsl", ddiProcessor, "/DDIxmltojson.xsl");
    }
    


    public static byte[] transformToJson(Resource resultResource, InputStream xsltFile, String idepUtilisateur) throws
            IOException, TransformerException {
        return transformWithSaxon(xsltFile, new StreamSource(resultResource.getInputStream()),
                Map.of("idepUtilisateur", idepUtilisateur)
        );
    }

    private byte[] transformToXml(byte[] inputData, InputStream xsltFile, String idValue, String
                                          nomenclatureName,
                                  String suggesterDescription, String timbre, String version) throws TransformerException {

        return transformWithSaxon(xsltFile, new StreamSource(new ByteArrayInputStream(inputData)),
                Map.of(
                        "idValue", idValue,
                        "suggesterName", nomenclatureName,
                        "suggesterDescription", suggesterDescription,
                        "timbre", timbre,
                        VERSION, version)
        );
    }

    private static byte[] transformWithSaxon(InputStream xsltFile, StreamSource input, Map<String, Object> parameters) throws TransformerException {

        Transformer transformer = XMLUtils.newTransformer(new StreamSource(xsltFile));
        parameters.forEach(transformer::setParameter);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult xmlResult = new StreamResult(outputStream);

        transformer.transform(input, xmlResult);

        return outputStream.toByteArray();
    }

    private byte[] transformToXmlForComplexList(byte[] inputData, InputStream xsltFile, String idValue, String
                                                        nomenclatureName,
                                                String suggesterDescription, String timbre, String version, String principale,
                                                List<String> secondaire, List<String> labelSecondaire)
            throws TransformerException {

       return transformWithSaxon(xsltFile, new StreamSource(new ByteArrayInputStream(inputData)),
                Map.of(
                        "idValue", idValue,
        "suggesterName", nomenclatureName,
        "suggesterDescription", suggesterDescription,
        "timbre", timbre,
        VERSION, version,
        "principale", principale,
        "secondaire", secondaire,
        "labelSecondaire", labelSecondaire
                )
        );
    }


    private String doTransformFile(MultipartFile file, String idepUtilisateur, String jsonToDdiXslFilename, DDIProcessor ddiProcessor, String ddiToJsonXslFilename) throws RmesException {
        if (file.isEmpty()) {
            throw new RmesException(HttpStatus.BAD_REQUEST, "Uploaded file is empty", null);
        }
        try {
            byte[] inputData = contentInDataTag(file);
            log.debug("Le contenu du fichier a été modifié avec succès (ajout balise data) !");

            
            InputStream xsltStream1 = getClass().getResourceAsStream(jsonToDdiXslFilename);
            byte[] xmlContent = ddiProcessor.process(inputData, xsltStream1);

            
            InputStream xsltStream2 = getClass().getResourceAsStream(ddiToJsonXslFilename);
            return new String(transformToJson(new ByteArrayResource(xmlContent), xsltStream2, idepUtilisateur));
        } catch (Exception e) {
            throw new ServerErrorException("Erreur lors de la transformation du fichier.", e);
        }
    }

    private byte[] contentInDataTag(@NonNull MultipartFile file) throws IOException {
        var fileContent = file.getBytes();
        byte[] retour = new byte[DATA_TAG_BEGIN.length + fileContent.length + DATA_TAG_END.length];
        System.arraycopy(DATA_TAG_BEGIN, 0, retour, 0, DATA_TAG_BEGIN.length);
        System.arraycopy(fileContent, 0, retour, DATA_TAG_BEGIN.length, fileContent.length);
        System.arraycopy(DATA_TAG_END, 0, retour, DATA_TAG_BEGIN.length + fileContent.length, DATA_TAG_END.length);
        return retour;
    }


    byte[] transform(InputStream inputStream, String xslFileName, SerializerConfigurer serializerConfigurer) throws XsltTransformationException, IOException {
        try {
            log.atDebug().log(() -> "Starting transformation with XSLT file: " + xslFileName);

            XsltCompiler compiler = processor.newXsltCompiler();

            // Remplacer l'utilisation de getFile() par getInputStream() pour charger la ressource du classpath
            InputStream xslInputStream = new ClassPathResource(xslFileName).getInputStream();
            XsltExecutable executable = compiler.compile(new StreamSource(xslInputStream));
            XsltTransformer transformer = executable.load();

            transformer.setSource(new StreamSource(inputStream));

            // Utilisation d'un OutputStream pour capturer la sortie en mémoire
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Serializer serializer = serializerConfigurer.configure(outputStream);

            transformer.setDestination(serializer);
            transformer.transform();

            return outputStream.toByteArray();
        } catch (SaxonApiException e) {
            throw new XsltTransformationException("Error during XSLT transformation", e);
        }
    }

    static byte[] transformWithParameters(byte[] source, InputStream xsltFileJson, Map<String, String> parameters) throws javax.xml.transform.TransformerException {
        // Créer un transformateur XSLT sécurisé
        // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
        Source xslt = new StreamSource(xsltFileJson);
        Transformer transformer = XMLUtils.newTransformer(xslt);
        parameters.forEach(transformer::setParameter);

        // Effectuer la transformation
        StreamSource text = new StreamSource(new ByteArrayInputStream(source));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult xmlResult = new StreamResult(outputStream);

        transformer.transform(text, xmlResult);

        return outputStream.toByteArray();
    }

    public String transformToXmlString(InputStream inputStream, String xslFileName) throws XsltTransformationException, IOException {
        return toXmlString(transformToXml(inputStream, xslFileName));
    }

    public byte[] transformToXml(InputStream inputStream, String xslFileName) throws IOException {
        return transform(inputStream, xslFileName, SerializerConfigurer.forXmlString(processor));
    }

    private String toXmlString(byte[] result) {
        return new String(result);
    }

    public byte[] transformToRawText(InputStream stream, String xslFilename) throws IOException {
        return transform(stream, xslFilename, SerializerConfigurer.forRawText(processor));
    }


    public String replaceXmlParameters(String inputXml, DDIItemType type, String label, int version, String name, String idepUtilisateur) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Désactivation des entités externes pour des raisons de sécurité (prévention des attaques XXE)
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(DISALLOW_DOCTYPE_DECL1, true);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(inputXml)));

            String typeName = type.getName();
            NodeList typeNodes = document.getElementsByTagNameNS("ddi:logicalproduct:3_3", typeName);

            if (typeNodes.getLength() == 0) {
                return "Erreur : Aucun élément correspondant trouvé pour le type " + typeName;
            }

            Element typeNameDocument = (Element) typeNodes.item(0);

            if (!typeName.equals(typeNameDocument.getNodeName())) {
                return "Erreur : Attention ce n'est pas le bon type. L'item chargé n'est pas du type que vous avez sélectionné.";
            }

            Document copy = (Document) document.cloneNode(true);

            NodeList versionNodes = copy.getElementsByTagName("r:Version");
            for (int i = 0; i < versionNodes.getLength(); i++) {
                Node versionNode = versionNodes.item(i);
                if (versionNode instanceof Element) {
                    Element versionElement = (Element) versionNode;
                    versionElement.setTextContent(String.valueOf(version));
                }
            }

            Node nameNode = copy.getElementsByTagName("r:String").item(0);
            nameNode.setTextContent(name);

            Node labelNode = copy.getElementsByTagName("r:Content").item(0);
            labelNode.setTextContent(label);

            NodeList urnNodes = copy.getElementsByTagName("r:URN");
            for (int i = 0; i < urnNodes.getLength(); i++) {
                Node urnNode = urnNodes.item(i);
                if (urnNode instanceof Element) {
                    Element urnElement = (Element) urnNode;
                    String urnCode = urnElement.getTextContent();
                    urnElement.setTextContent(urnCode.substring(0, urnCode.lastIndexOf(":")) + ":" + version);
                }
            }

            ByteArrayOutputStream outputStreamResult = onlyIndent(copy);

            // Ajout de la transformation XML vers JSON avec XSLT
            InputStream xsltStream = getClass().getResourceAsStream("/DDIxmltojsonForOneObject.xsl");
            return transformToJson(outputStreamResult.toByteArray(), xsltStream, idepUtilisateur, version);

        } catch (Exception e) {
            return "Error processing XML";
        }
    }

    private static ByteArrayOutputStream onlyIndent(Document document) throws TransformerException {
        Transformer transformer = XMLUtils.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        ByteArrayOutputStream outputStreamResult = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(outputStreamResult));
        return outputStreamResult;
    }

    private static String transformToJson(byte[] source, InputStream xsltFileJson, String idepUtilisateur, int version) throws TransformerException {
        return new String(transformWithParameters(source, xsltFileJson, Map.of("idepUtilisateur", idepUtilisateur, VERSION, String.valueOf(version))));
    }

    public static JSONArray extractIdentifiersFromDiiInstance(String fragmentXml) {
        Set<FragmentData> uniqueData = new HashSet<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(DISALLOW_DOCTYPE_DECL1, true);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(fragmentXml.getBytes(StandardCharsets.UTF_8)));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            xpath.setNamespaceContext(new NamespaceContextMap("r", "ddi:reusable:3_3", "ddi", "ddi:instance:3_3"));

            // Sélectionner tous les nœuds ayant un ID, Agency ou Version
            NodeList nodes = (NodeList) xpath.evaluate("//*[local-name()='ID' or local-name()='Agency' or local-name()='Version']", document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node currentNode = nodes.item(i).getParentNode();

                String id = xpath.evaluate("r:ID", currentNode);
                String agency = xpath.evaluate("r:Agency", currentNode);
                String version = xpath.evaluate("r:Version", currentNode);

                FragmentData fragmentData = new FragmentData(id, agency, version);
                uniqueData.add(fragmentData);
            }

            // Convertir Set en JSONArray
            JSONArray allFragmentsData = new JSONArray();
            for (FragmentData data : uniqueData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Identifier", data.getIdentifier());
                jsonObject.put("AgencyId", data.getAgencyId());
                jsonObject.put("Version", data.getVersion());
                allFragmentsData.put(jsonObject);
            }

            return allFragmentsData;

        } catch (Exception e) {
            log.error("Error while extractIdentifiersFromDiiInstance "+e.getMessage(), e);
            return new JSONArray();
        }
    }

    @FunctionalInterface
    private interface DDIProcessor {
        byte[] process(byte[] inputData, InputStream xsltStream) throws TransformerException, IOException;
    }
}
