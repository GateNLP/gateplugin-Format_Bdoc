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

import gate.Document;
import java.util.HashMap;
import java.util.Map;


/**
 * A very basic representation of all the components of a GATE document.
 * 
 * This is a very basic POJO representation of a GATE document and 
 * this representation also corresponds to the external JSON representation
 * of GATE documents in Python. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocDocument
{
  
  /**
   * Default constructor. 
   */
  public BdocDocument() {
    
  }
  
  /**
   * Create BdocDocument from Map representation.
   * 
   * @param map  map representation
   */
  @SuppressWarnings("unchecked")
  public BdocDocument(Map<String, Object> map) {
    features = (Map<String, Object>) map.get("features");
    text = (String)map.get("text");
    annotation_sets = (Map<String, BdocAnnotationSet>) map.get("annotation_sets");
    offset_type = (String) map.get("offset_type");
    name = (String) map.get("name");
  }

  /**
   * Document features. 
   * Other than the feature map of a GATE document, only String keys are 
   * supported. Non strings in the original document get convert using toString()
   * If there are no features, this may be null.
   * <p>
   * NOTE: this is a HashMap rather than a Map so that jackson-jr does not 
   * use its own DeferredMap on restoring.
   */
  public Map<String, Object> features;
  
  /**
   * Document text.
   * 
   */
  public String text;
  
  /**
   * Document name.
   */
  public String name;
  
  /**
   * Map from annotation set name to annotation set with that name. 
   * <p>
   * NOTE: this is a HashMap rather than a Map so that jackson-jr does not 
   * use its own DeferredMap on restoring.
   */
  public Map<String, BdocAnnotationSet> annotation_sets;
  
  /**
   * Indicates the style of offsets used in this document.
   * j=Java (number of UTF16 code units), p=Python (number of Unicode code 
   * points)
   */
  public String offset_type = "j";
  
  /**
   * Change all the annotation offsets to the required type (java/python).
   * This only works if the text of the document is set and all annotations
   * do have compatible offsets, otherwise a RuntimeException is thrown. 
   * @param newtype the target offset type
   */
  public void fixupOffsets(String newtype) {
    if(offset_type.equals(newtype)) {
      return;
    }
    if(annotation_sets == null || annotation_sets.isEmpty()) { 
      return;
    }
    if(text == null) {
      throw new RuntimeException("Fixing offsets only possible if the text is known");
    }
    // create the offset mapper
    OffsetMapper om = new OffsetMapper(this.text);
    // go through all annotation sets and all annotations and fix them
    for(BdocAnnotationSet annset : annotation_sets.values()) {
      for(BdocAnnotation ann : annset.annotations) {
        if("p".equals(newtype)) {
          ann.start = om.convertToPython(ann.start);
          ann.end = om.convertToPython(ann.end);
        } else {
          ann.start = om.convertToJava(ann.start);
          ann.end = om.convertToJava(ann.end);          
        }
      }
    }
    offset_type = newtype;
  }
  
  /**
   * Convert this BdocDocument instance to a GATE document. 
   * 
   * This will include all the parts present in the BdocDocument.
   * 
   * @return a new GATE document containing everything in the BdocDocument 
   * instance.
   */
  public Document toGateDocument() {
    return new GateDocumentUpdater(this.text).fromBdoc(this);
  }
  
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    Map<String, Map<String, Object>> ass = new HashMap<>();
    for(Map.Entry<String, BdocAnnotationSet> e : this.annotation_sets.entrySet()) {
      ass.put(e.getKey(), e.getValue().toMap());
    }
    map.put("annotation_sets", ass);
    map.put("features", this.features);
    map.put("offset_type", this.offset_type);
    map.put("text", this.text);
    map.put("name", this.name);
    return map;
  }
   
}
