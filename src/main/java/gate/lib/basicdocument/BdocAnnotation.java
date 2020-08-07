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

package gate.lib.basicdocument;

import gate.Annotation;
import java.util.Map;

/**
 * Representation of an annotation within a set. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocAnnotation {
  
  /**
   * Create from a GATE annotation.
   * @param ann GATE annotation
   * @return BdocAnnotation
   */
  public static BdocAnnotation fromGateAnnotation(Annotation ann) {
    BdocAnnotation ret = new BdocAnnotation();
    ret.type = ann.getType();
    ret.start = ann.getStartNode().getOffset().intValue();
    ret.end = ann.getEndNode().getOffset().intValue();
    ret.features = BdocUtils.featureMap2Map(ann.getFeatures(), null);
    ret.id = ann.getId();
    return ret;
  }
  
  // Fields
  /**
   * BdocAnnotation features
   */
  public Map<String, Object> features;
  
  /**
   * BdocAnnotation type
   */
  public String type;

  /**
   * The annotation id. 
   * If set, this id should get assigned to a newly created annotations. 
   * If null, assign the next free id from the containing annotation set.
   */
  public Integer id;
  
  /**
   * Start offset.
   */
  public int start;
  
  /**
   * End offset.
   */
  public int end;

  /**
   * Default constructor.
   */
  public BdocAnnotation() {
    
  }
  
  /**
   * Construct from map representation. 
   * 
   * @param annmap  the map as used in serialization.
   */
  @SuppressWarnings("unchecked")
  public BdocAnnotation(Map<String, Object> annmap) {
    features = (Map<String, Object>) annmap.get("features");
    type = (String) annmap.get("type");
    id = (Integer)annmap.get("id");
    start = (int)annmap.get("start");
    end = (int)annmap.get("end");
  }
  
  
}
