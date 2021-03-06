/*
 * Copyright (c) 2019 The University of Sheffield.
 *
 * This file is part of gateplugin-Format_Bdoc 
 * (see https://github.com/GateNLP/gateplugin-Format_Bdoc).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gate.plugin.format.bdoc.tests;
import gate.Annotation;
import gate.AnnotationSet;
import java.net.URL;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.plugin.format.bdoc.ExporterBdocJson;
import gate.plugin.format.bdoc.ExporterBdocMsgPack;
import gate.test.GATEPluginTestCase;
import gate.util.GateException;
import java.io.File;
import java.io.IOException;
import static junit.framework.TestCase.assertEquals;
/**
 * Test the FormatBdcoJson class.
 * @author Johann Petrak
 */
public class TestFormatBdocJson extends GATEPluginTestCase {
  String expectedText = "A simple \uD83D\uDCA9 document.";
  private static final String TMPTESTFILE = "tmp4testing-doc1.bdocjs";

  
  /**
   * Test.Direct save/load.
   * 
   * @throws gate.creole.ResourceInstantiationException error
   * @throws java.io.IOException error
   */
  public void testSaveLoadDocument2() throws GateException, IOException {
    Document doc1 = Utils.makeTestDocument1();
    ExporterBdocJson exp = 
            (ExporterBdocJson)gate.Gate.getCreoleRegister().
                    getAllInstances("gate.plugin.format.bdoc.ExporterBdocJson").
                    iterator().next();
    exp.export(doc1, new File(TMPTESTFILE), Factory.newFeatureMap());
    FeatureMap parms = Factory.newFeatureMap();
    parms.put("sourceUrl", new File(TMPTESTFILE).toURI().toURL());      
    Document doc2 = (Document)Factory.createResource("gate.corpora.DocumentImpl", parms);
    assertEquals(Utils.CONTENT1, doc2.getContent().toString());
    assertEquals(1, doc2.getFeatures().get("docfeat1"));
    assertEquals("docfeatvalue", doc2.getFeatures().get("docfeat2"));
    AnnotationSet anns = doc2.getAnnotations();
    assertEquals(1, anns.size());
    Annotation ann = anns.iterator().next();
    assertEquals("ANN", ann.getType());
    assertEquals(1, ann.getFeatures().get("ann1feat1"));
    assertEquals("annfeatvalue", ann.getFeatures().get("ann1feat2"));
    assertEquals((long)ann.getStartNode().getOffset(), 30L);
    assertEquals((long)ann.getEndNode().getOffset(), 35L);
  }
  
  
  /**
   * Test.
   * @throws Exception  if error
   */
  public void testLoadDocument1() throws Exception {
    URL docURL = this.getClass().getResource("/resources/testdoc1.bdocjs");
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_URL_PARAMETER_NAME, docURL);
    params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
    params.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/bdocjs");
    Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl", params);
    assertEquals(expectedText, doc.getContent().toString());
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
  
  /**
   * Test.
   * @throws Exception  if error
   */
  public void testLoadDocumentGzip1() throws Exception {
    //Document tmp = (Document)Factory.newDocument(expectedText);
    //System.err.println("DEBUG: GATE expected text: "+expectedText);
    //System.err.println("DEBUG: GATE text from doc: "+tmp.getContent().toString());
    
    URL docURL = this.getClass().getResource("/resources/testdoc1.bdocjs.gz");
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_URL_PARAMETER_NAME, docURL);
    params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
    params.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/bdocjs+gzip");
    Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl", params);
    //System.err.println("DEBUG: BDOC expected text: "+expectedText);
    //System.err.println("DEBUG: BDOC text from doc: "+doc.getContent().toString());
    assertEquals(expectedText, doc.getContent().toString());
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
