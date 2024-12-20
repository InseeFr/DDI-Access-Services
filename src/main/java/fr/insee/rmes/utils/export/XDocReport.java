package fr.insee.rmes.utils.export;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.service.VarBookExportBuilder;
import fr.insee.rmes.utils.XsltUtils;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import freemarker.ext.dom.NodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static fr.insee.rmes.transfoxsl.service.XsltTransformationService.*;

@Component
public record XDocReport(VarBookExportBuilder varBookExport) {

	static final Logger logger = LoggerFactory.getLogger(XDocReport.class);

	public byte[] getCodeBookExportV2(String ddi, String xslPatternFile) {

		InputStream xslRemoveNameSpaces = getClass().getResourceAsStream("/xslTransformerFiles/remove-namespaces.xsl");
		InputStream xslCheckReference = getClass().getResourceAsStream("/xslTransformerFiles/check-references.xsl");
		String dicoCode = "/xslTransformerFiles/dico-codes.xsl";
		String zipRmes = "/xslTransformerFiles/dicoCodes/toZipForDicoCodes.zip";

		byte[] ddiRemoveNameSpace = XsltUtils.transformerStringWithXsl(ddi, xslRemoveNameSpaces);
		byte[] control = XsltUtils.transformerInputStreamWithXsl(ddiRemoveNameSpace, xslCheckReference);

        // Créer un DocumentBuilderFactory sécurisé
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		dbf.setFeature(DISALLOW_DOCTYPE_DECL1, true);
		dbf.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
		dbf.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

		DocumentBuilder db = dbf.newDocumentBuilder();


		Document doc = db.parse(new ByteArrayInputStream(control));

		String checkResult = doc.getDocumentElement().getNodeName();

		if (!"OK".equals(checkResult)) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + control.getName());
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			ByteArrayResource resourceByte = new ByteArrayResource(Files.readAllBytes(((InputStream) new ByteArrayInputStream(XsltUtils.transformerInputStreamWithXsl(XsltUtils.transformerStringWithXsl(ddi, xslRemoveNameSpaces), xslCheckReference))).toPath()));

			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(control.length())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(resourceByte);
		}

		HashMap<String, String> contentXML = new HashMap<>();
		contentXML.put("ddi-file", Files.readString(ddiRemoveNameSpaces.toPath()));

		return exportUtils.exportAsODT(Path.of("export.odt"), contentXML, dicoCode, xslPatternFile, zipRmes, "dicoVariable");
	}


	public byte[] getCodeBookCheck(byte[] ddiContent) throws Exception {

		InputStream xslRemoveNameSpaces = getClass().getResourceAsStream("/xslTransformerFiles/remove-namespaces.xsl");
		File ddiRemoveNameSpaces = File.createTempFile("ddiRemoveNameSpaces", ".xml");
		ddiRemoveNameSpaces.deleteOnExit();
		XsltUtils.transformerInputStreamWithXsl(ddiContent, xslRemoveNameSpaces, ddiRemoveNameSpaces);

		InputStream xslCodeBookCheck = getClass().getResourceAsStream("/xslTransformerFiles/dico-codes-test-ddi-content.xsl");

		return XsltUtils.transformerFileWithXsl(ddiRemoveNameSpaces, xslCodeBookCheck);
	}

	public byte[] exportVariableBookInOdt(String xml, byte[] odtTemplate) throws RmesException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IXDocReport report;


		// 1) Load DOCX into XWPFDocument
		try {
			report = getReportTemplate(odtTemplate);
			
			// 2) Create Java model context 
			IContext context = getXmlData(report, xml);
	
			report.process(context, baos);
		}catch (IOException | XDocReportException e) {
			logger.error(e.getMessage());
		}
		return baos.toByteArray();
	}



	private IContext getXmlData(IXDocReport report, String xmlInput)
			throws  RmesException {

		String xmlString = varBookExport.getData(xmlInput);
		InputStream projectInputStream = new ByteArrayInputStream(xmlString.getBytes());
		InputSource projectInputSource = new InputSource( projectInputStream );
		NodeModel xml = null;
		IContext context = null ;
		try {
			xml = NodeModel.parse( projectInputSource );
			context = report.createContext();
		} catch (SAXException | IOException | ParserConfigurationException | XDocReportException e) {
			throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getClass()+" - Can't put xml in XdocReport context");
		}
		context.put("racine", xml);
		return context;
	}

	private IXDocReport getReportTemplate(byte[] odtTemplate) throws IOException, XDocReportException {
		try(InputStream is = new ByteArrayInputStream(odtTemplate)){
			return  XDocReportRegistry.getRegistry().loadReport(is,TemplateEngineKind.Freemarker);
		}

	}

}
