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
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import gate.lib.basicdocument.docformats.Format;
import gate.lib.basicdocument.docformats.Loader;
import gate.lib.basicdocument.docformats.Saver;
import gate.plugin.format.bdoc.ExporterBdocMsgPack;
import gate.test.GATEPluginTestCase;
import gate.util.GateException;
import java.io.File;
import java.io.IOException;
import gate.plugin.format.bdoc.tests.Utils;
import gate.util.InvalidOffsetException;
/**
 * Test the FormatBdcoJson class.
 * @author Johann Petrak
 */
public class TestFormatMsgPack extends GATEPluginTestCase {
  private static final String TMPTESTFILE = "tmp4testing-doc1.bdocmp";
  /**
   * Test.
   * 
   * Stepwise save/reload in msgpack format.
   * @throws gate.creole.ResourceInstantiationException error
   */
  public void testSaveLoadDocument1() throws ResourceInstantiationException, InvalidOffsetException {
    Document doc = Utils.makeTestDocument1();
    BdocDocument bdoc1 = new BdocDocumentBuilder().fromGate(doc).buildBdoc();
    assertEquals(Utils.CONTENT1, bdoc1.text);
    new Saver().format(Format.MSGPACK).to(TMPTESTFILE).save(bdoc1);
    BdocDocument bdoc2 = new Loader().format(Format.MSGPACK).from(TMPTESTFILE).load_bdoc();
    String txt = bdoc2.text;
    assertEquals(Utils.CONTENT1, txt);
  }
  /**
   * Test.Direct save/load.
   * 
   * @throws gate.creole.ResourceInstantiationException error
   * @throws java.io.IOException error
   */
  public void testSaveLoadDocument2() throws GateException, IOException {
    Document doc1 = Utils.makeTestDocument1();
    ExporterBdocMsgPack exp = 
            (ExporterBdocMsgPack)gate.Gate.getCreoleRegister().
                    getAllInstances("gate.plugin.format.bdoc.ExporterBdocMsgPack").
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

}
