
package gate.plugin.format.bdoc.tests;
import gate.Annotation;
import gate.AnnotationSet;
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
    assertEquals("A simple test document.", doc.getContent().toString());
    FeatureMap docfm = doc.getFeatures();
    assertEquals(12, docfm.getOrDefault("docfeature1", 0));
    AnnotationSet defset = doc.getAnnotations();
    assertEquals(1, defset.size());
    Annotation ann1 = defset.iterator().next();
    assertEquals("ANNTYPE", ann1.getType());
    assertEquals(0L, (long)ann1.getStartNode().getOffset());
    assertEquals(5L, (long)ann1.getEndNode().getOffset());
    FeatureMap annfm1 = ann1.getFeatures();
    assertEquals(0, annfm1.size());
    AnnotationSet set1set = doc.getAnnotations("Set1");
    assertEquals(1, set1set.size());
    Annotation ann2 = set1set.iterator().next();
    assertEquals("SOMETYPE", ann2.getType());
    assertEquals(2L, (long)ann2.getStartNode().getOffset());
    assertEquals(3L, (long)ann2.getEndNode().getOffset());
    FeatureMap annfm2 = ann2.getFeatures();
    assertEquals(13, annfm2.getOrDefault("annfeature1", 0));
    
  }
}
