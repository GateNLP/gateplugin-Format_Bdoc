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

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;

/**
 * Utils for texting.
 * @author Johann Petrak
 */
public class Utils {
  public static final String CONTENT1 = 
          "A simple \uD83D\uDCA9 document.\n" +
          "Another line ! \uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\n" + 
          "\u2620\uFE0F and here is another line mentioning New York!\n";  
  
  public static Document makeTestDocument1() throws ResourceInstantiationException, InvalidOffsetException {
    Document doc = Factory.newDocument(CONTENT1);
    doc.getFeatures().put("docfeat1", 1);
    doc.getFeatures().put("docfeat2", "docfeatvalue");
    AnnotationSet anns = doc.getAnnotations();
    anns.add(30L,35L,"ANN", gate.Utils.featureMap("ann1feat1", 1, "ann1feat2", "annfeatvalue"));
    return doc;
  }
}
