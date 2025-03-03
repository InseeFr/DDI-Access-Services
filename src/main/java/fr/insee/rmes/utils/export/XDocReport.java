package fr.insee.rmes.utils.export;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.service.VarBookExportBuilder;
import fr.insee.rmes.utils.DocumentBuilderUtils;
import fr.insee.rmes.utils.ExportUtils;
import fr.insee.rmes.utils.XsltUtils;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import freemarker.ext.dom.NodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.Map;

@Component
public record XDocReport(VarBookExportBuilder varBookExport, ExportUtils exportUtils) {

	static final Logger logger = LoggerFactory.getLogger(XDocReport.class);

	public static final byte[] XSL_REMOVE_NAMESPACE = loadFileAsBytes("/xslTransformerFiles/remove-namespaces.xsl");
	public static final byte[] XSL_CHECK_REFERENCE = loadFileAsBytes("/xslTransformerFiles/check-references.xsl");

	private static byte[] loadFileAsBytes(String resourcePath) {
        try (InputStream resourceAsStream = XDocReport.class.getResourceAsStream(resourcePath)){
			return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

	public byte[] getCodeBookExportV2(String ddi, String xslPatternFile) throws RmesException, TransformerException, ParserConfigurationException, IOException, SAXException {

		String dicoCode = "/xslTransformerFiles/dico-codes.xsl";
		String zipRmes = "/xslTransformerFiles/dicoCodes/toZipForDicoCodes.zip";

		byte[] ddiRemoveNameSpace = XsltUtils.transformerStringWithXsl(ddi, new ByteArrayInputStream(XSL_REMOVE_NAMESPACE));
		byte[] control = XsltUtils.transformerInputStreamWithXsl(ddiRemoveNameSpace, new ByteArrayInputStream(XSL_CHECK_REFERENCE));

		Document doc = DocumentBuilderUtils.getDocument(new ByteArrayInputStream(control));

		String checkResult = doc.getDocumentElement().getNodeName();

		if (!"OK".equals(checkResult)) {
			return XsltUtils.transformerInputStreamWithXsl(XsltUtils.transformerStringWithXsl(ddi, new ByteArrayInputStream(XSL_REMOVE_NAMESPACE)), new ByteArrayInputStream(XSL_CHECK_REFERENCE));
		}
		logger.debug("Begin To export dicoVariable");
		var odt=exportUtils.exportAsODT(Map.of("ddi-file", ddiRemoveNameSpace), dicoCode, xslPatternFile, zipRmes);
		logger.debug("End To export dicoVariable");
		return odt;
	}


	public byte[] getCodeBookCheck(byte[] ddiContent) throws Exception {
		InputStream xslCodeBookCheck = getClass().getResourceAsStream("/xslTransformerFiles/dico-codes-test-ddi-content.xsl");

		return XsltUtils.transformerInputStreamWithXsl(
				XsltUtils.transformerInputStreamWithXsl(ddiContent, new ByteArrayInputStream(XSL_REMOVE_NAMESPACE)),
				xslCodeBookCheck
		);
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
