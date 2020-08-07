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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of an annotaton set.
 * We use the name "set" though this representation is really just a list.
 * The order of annotations in the list is probably usually not relevant.
 *
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocAnnotationSet {
  
  // Fields representing the various aspects of an annotation set
  
  /**
   * Default constructor.
   */
  public BdocAnnotationSet() {
    
  }
  
  /** 
   * Construct from map. 
   * 
   * @param map map as used for serialization.
   */
  @SuppressWarnings("unchecked")
  public BdocAnnotationSet(Map<String, Object> map) {
    name = (String) map.get("name");
    next_annid = (Integer) map.get("next_annid");
    List<Map<String, Object>> annmaps = (List<Map<String, Object>>) map.get("annotations");
    annotations = new ArrayList<>();
    if(annmaps != null) {
      for(Map<String, Object> annmap : annmaps) {
        annotations.add(new BdocAnnotation(annmap));
      }
    }
  }
  
  
  /**
   * The annotation set name: the empty string corresponds to the "default
   * annotation set".
   */
  public String name = ""; 
  
  /**
   * The list of annotations in this set.
   */
  public List<BdocAnnotation> annotations;
  
  /**
   * Contains the next id to use for new annotations.
   */
  public Integer next_annid; 

  
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("annotations", this.annotations);
    map.put("name", this.name);
    map.put("next_annid", this.next_annid);
    return map;
  }
  
}
