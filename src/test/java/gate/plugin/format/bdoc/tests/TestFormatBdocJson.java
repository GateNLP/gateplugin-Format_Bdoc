
package gate.plugin.format.bdoc.tests;
import java.net.URL;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.test.GATEPluginTestCase;
/**
 * Test the FormatBdcoJson class.
 * @author Johann Petrak
 */
public class TestFormatBdocJson extends GATEPluginTestCase {
  public void testLoadDocument1() throws Exception {
    URL docURL = this.getClass().getResource("/resources/testdoc1.bdocjson");
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_URL_PARAMETER_NAME, docURL);
    params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
    params.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/bdocjson");
    Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl", params);
    //assertEquals("A simple test document.", doc.getContent().toString());
    //FeatureMap docfm = doc.getFeatures();
    //assertEquals(12, docfm.getOrDefault("docfeature1", 0));
  }
}
