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

package gate.lib.basicdocument.tests;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Utils;
import gate.creole.ResourceInstantiationException;
import gate.lib.basicdocument.BdocAnnotationSet;
import gate.lib.basicdocument.BdocAnnotation;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import gate.lib.basicdocument.OffsetMapper;
import gate.lib.basicdocument.docformats.Format;
import gate.lib.basicdocument.docformats.Loader;
import gate.lib.basicdocument.docformats.Saver;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * First set of simple tests.
 * @author Johann Petrak
 */
public class TestBasicDocument1 extends TestCase {
  String sampleText1 = "This is a simple 💩 document. It has two sentences.";
  String sampleText2 = "A \uD83D\uDCA9 emoji";
  
  @Override
  public void setUp() throws GateException {
    Gate.init();
  }
  
  /**
   * Run a test.
   * @throws ResourceInstantiationException if error
   * @throws InvalidOffsetException  if error
   */
  @Test
  public void testBasic1() throws ResourceInstantiationException, InvalidOffsetException {
    // create a simple GATE document, convert to BdocDocument
    Document doc = Factory.newDocument(sampleText1);
    AnnotationSet defSet = doc.getAnnotations();
    defSet.add(0L, 4L, "Token", Utils.featureMap("string", "This"));
    defSet.add(6L, 8L, "Token", Utils.featureMap("string", "is"));
    defSet.add(17L, 19L, "Token", Utils.featureMap("string", "poo"));
    BdocDocument bdoc = new BdocDocumentBuilder()
            .fromGate(doc)
            .pythonOffsets()
            .buildBdoc();
    Assert.assertEquals(sampleText1, bdoc.text);
    BdocAnnotationSet bset = bdoc.annotation_sets.get("");
    Assert.assertEquals(3, bset.annotations.size());
    Assert.assertEquals((Integer)3, bset.next_annid);
    Assert.assertEquals("", bset.name);
    BdocAnnotation bann1 = new BdocAnnotation();  // suppress null pointer warning
    BdocAnnotation bann2 = bann1;
    BdocAnnotation bann3 = bann1;
    for (BdocAnnotation bann : bset.annotations) {
      Assert.assertNotNull(bann.id);
      Assert.assertTrue(bann.id >= 0);
      Assert.assertTrue(bann.id <= 2);
      if(null == bann.id) {
        bann3 = bann;
      } else switch (bann.id) {
        case 0:
          bann1 = bann;
          break;
        case 1:
          bann2 = bann;
          break;
        default:
          bann3 = bann;
          break;
      }
    }
    Assert.assertEquals(0, bann1.start);
    Assert.assertEquals(4, bann1.end);
    Assert.assertEquals("Token", bann1.type);
    Assert.assertEquals(6, bann2.start);
    Assert.assertEquals(8, bann2.end);
    Assert.assertEquals("Token", bann2.type);
    Assert.assertEquals(17, bann3.start);
    Assert.assertEquals(18, bann3.end);     // !!! one less because python!
    Assert.assertEquals("Token", bann3.type);
    
    new Saver().format(Format.JSON_MAP).to("test-doc1.bdocjs").save(bdoc);
    String json =
            new Saver().format(Format.JSON_MAP).asString().save(bdoc);
        
    // try to re-create Bdoc from JSON
    BdocDocument bdoc2 = 
            new Loader().format(Format.JSON_MAP).fromString(json).load_bdoc();
    Assert.assertNotNull(bdoc2.annotation_sets);
    System.err.println("annotation_sets: "+bdoc2.annotation_sets.getClass().getName());
    Assert.assertTrue(bdoc2.annotation_sets instanceof Map);
    Assert.assertTrue(bdoc2.annotation_sets.containsKey(""));
    BdocAnnotationSet bset2 = bdoc2.annotation_sets.get("");
    Assert.assertNotNull(bset2);
    System.err.println("the set: "+bset2.getClass().getName());
    Assert.assertTrue(bset2.getClass().getName().equals("gate.lib.basicdocument.BdocAnnotationSet"));
  }

  /**
   * Test offset mapping.
   * @throws ResourceInstantiationException if error
   * @throws InvalidOffsetException  if error
   */
  @Test
  public void testOffsetMappings1() throws ResourceInstantiationException, InvalidOffsetException {
    Document doc = Factory.newDocument(sampleText2);
    AnnotationSet defSet = doc.getAnnotations();
    // System.err.println("DEBUG: text len="+sampleText2.length());
    OffsetMapper om = new OffsetMapper(sampleText2);
    List<Integer> p2j = om.getPython2JavaList();
    List<Integer> j2p = om.getJava2PythonList();
    // System.err.println("DEBUG: j2p="+j2p);
    // System.err.println("DEBUG: p2j="+p2j);
    Assert.assertEquals(11, j2p.size());
    Assert.assertEquals(10, p2j.size());
    int[] j2p_expected = {0,1,2,2,3,4,5,6,7,8,9};
    int[] p2j_expected = {0,1,2,4,5,6,7,8,9,10};
    int[] j2p_actual = om.getJava2PythonArray();
    int[] p2j_actual = om.getPython2JavaArray();
    Assert.assertArrayEquals(j2p_expected, j2p_actual);
    Assert.assertArrayEquals(p2j_expected, p2j_actual);
  }
  
}
