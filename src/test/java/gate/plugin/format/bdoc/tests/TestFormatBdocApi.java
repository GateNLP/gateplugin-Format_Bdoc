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
import gate.Gate;
import gate.gui.ResourceHelper;
import gate.test.GATEPluginTestCase;
/**
 * Test the FormatBdcoJson class.
 * @author Johann Petrak
 */
public class TestFormatBdocApi extends GATEPluginTestCase {
  String expectedText = "A simple \uD83D\uDCA9 document.";
  /**
   * Test.
   * @throws Exception  if error
   */
  public void testApi() throws Exception {
    Document doc = Factory.newDocument("Some document");
    ResourceHelper rh = (ResourceHelper)Gate.getCreoleRegister()
                     .get("gate.plugin.format.bdoc.API")
                     .getInstantiations().iterator().next();
    String json = (String)rh.call("json_from_doc", doc);
    System.err.println("JSON="+json);
  }
  
}
