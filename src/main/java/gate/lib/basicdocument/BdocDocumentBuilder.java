/*
 * Copyright (c) 2019 The University of Sheffield.
 *
 * This file is part of gatelib-basicdocument 
 * (see https://github.com/GateNLP/gatelib-basicdocument).
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

import com.fasterxml.jackson.jr.ob.JSON;
import gate.Annotation;
import gate.Document;
import gate.lib.basicdocument.docformats.Format;
import gate.lib.basicdocument.docformats.Saver;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for building a JsonDocument.
 * 
 * This allows building the document from a GATE document or from scratch,
 * optionally limiting the parts to add to the document.
 * Once all the parts are ready, this can be used to return the BdocDocument
 * instance or directly serialise it as JSON to various destinations.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocDocumentBuilder {
  
  String text;
  HashMap<String,Set<Annotation>> includedSets = new HashMap<>();
  HashMap<String,Integer> nextAnnotationIds = new HashMap<>();
  HashMap<String, Object> includedFeatures = new HashMap<>();
  File toFile = null;
  String offset_type = "j";
  List<JSON.Feature> addJSONFeatures = new ArrayList<>();

  /**
   * Tell the builder to create the JsonDocument from a GATE document.
   * By default, this will include all features and all annotation sets,
   * but this can be changed later.
   * Alternately, the JsonDocument can be constructed by adding the parts
   * (text, document features, annotation sets) individually. 
   * 
   * @param doc the Gate document to build the BdocDocument/JSON from
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder fromGate(Document doc) {
    // TODO: for now check that Document is a SimpleDocument
    this.text = doc.getContent().toString(); 
    includedSets.put("", doc.getAnnotations());
    for (String name : doc.getNamedAnnotationSets().keySet()) {      
      includedSets.put(name, doc.getAnnotations(name));
    }
    BdocUtils.featureMap2Map(doc.getFeatures(), includedFeatures);
    return this;
  }
  
  /**
   * Add an annotation set. 
   * Same as addSet(name, annset) but the annotation set uses the given
   * nextAnnotationId for new annotations.
   * NOTE: if the maximum annotation id in the set is bigger than the 
   * given nextAnnotationId, then the bigger Id is used instead!
   * 
   * @param name the name of the annotation set (this can differ from the 
   * original name if an annotation set is passed. Must not be null, the 
   * "default" set uses the empty string as name. 
   * @param annset a set of annotations, could be an AnnotationSet or a set
   * of annotations.
   * @param nextAnnotationId the annotation id to start from when new 
   * annotations are added to this set in the Bdoc document.
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder addSet(String name, Set<Annotation> annset, 
          int nextAnnotationId) {
    includedSets.put(name, annset);
    nextAnnotationIds.put(name, nextAnnotationId);
    return this;
  }

  
  /**
   * Add all features from the given (feature) map as document features.
   * 
   * The map can have keys and values of any type, but when adding features,
   * the following conversions are carried out: null keys are removed, 
   * any key that is not a String is converted to String. The value of all
   * features should be something that is directly JSON-serialisable, but this
   * is not checked. Note that some types can get serialised to JSON but 
   * will get converted to a different type when read back from JSON!
   * 
   * The user is responsible for making sure that value types work with the 
   * JSON default serialisation. 
   * 
   * If a feature has already been added previously, its old value is 
   * replaced. 
   * 
   * @param fm a map to interpret as a feature map
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder addFeatures(Map<Object,Object> fm) {
    BdocUtils.featureMap2Map(fm, includedFeatures);
    return this;
  }
  
  /**
   * Set/update the text of the document.
   * 
   * @param text the new document text
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setText(String text) {
    this.text = text;
    return this;
  }
  
  
  /**
   * Set the list of included AnnotationSet names.
   * 
   * All the names must already be registered to get added, otherwise an 
   * exception is thrown. 
   * This allows you to select the annotations to  actually use from 
   * a GATE document. 
   * @param names a collection of names to choose and use for the BdocDocument
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setAnnotationSetNames(Collection<String> names) {
    HashMap<String,Set<Annotation>> newSets = new HashMap<>();
    for (String tmpname : names) {
      if(includedSets.containsKey(tmpname)) {
        newSets.put(tmpname, includedSets.get(tmpname));
      } else {
        throw new GateRuntimeException("Cannot select annotation set "+
                tmpname+" because it does not exist");
      }
    }
    includedSets = newSets;
    return this;
  }
  
  
  /**
   * Set the list of included features.
   * 
   * All the names must already be registered to get added, otherwise an 
   * exception is thrown. 
   * This allows you to select the features to actually use from a GATE document. 
   * @param featurenames a collection of  feature names to choose and use for 
   * the BdocDocument
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setFeatureNames(Collection<String> featurenames) {
    HashMap<String, Object> newFeatures = new HashMap<>();
    for (String tmpname : featurenames) {
      if(includedFeatures.containsKey(tmpname)) {
        newFeatures.put(tmpname, includedFeatures.get(tmpname));
      } else {
        throw new GateRuntimeException("Cannot select feature "+
                tmpname+" because it does not exist");
      }
    }
    includedFeatures = newFeatures;
    return this;    
  }
  
  /**
   * Add/set a single feature as a document feature.
   * 
   * @param name Feature name 
   * @param value Feature value
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder addFeature(String name, Object value) {
    includedFeatures.put(name, value);
    return this;
  }
  
  /**
   * Make the JsonDocument use java offsets (the default). 
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder javaOffsets() {
    offset_type = "j";
    return this;
  }
  
  /**
   * Make the JsonDocument use python/unicode codepoint offsets. 
   * This wil fix all the annotation offsets so they refer to unicode
   * code points instead of java utf16 code units. This will only work
   * if the text of the json document is set and compatible with the 
   * offsets of the added annotations, otherwise an exception is thrown. 
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder pythonOffsets() {
    offset_type = "p";
    return this;
  }
  
  /**
   * Set a JSON serialisation feature.
   * @param feature the feature to set
   * @return the modified builder instance
   */
  public BdocDocumentBuilder withJSONFeature(JSON.Feature feature) {
    addJSONFeatures.add(feature);
    return this;
  }
  
  /**
   * Given all the info accumulated, build a JsonDocument and return it.
   * 
   * This adds a BdocDocument instance with all the information added
   * so far. 
   * 
   * @return the BdocDocument containing all the information added so far
   */
  public BdocDocument buildBdoc() {
    BdocDocument ret = new BdocDocument();
    ret.text = text;
    if(includedFeatures.size() > 0) {
      ret.features = includedFeatures;
    }
    if(includedSets.size() > 0) {
      HashMap<String, BdocAnnotationSet> annotation_sets = new HashMap<>();
      for(String name : includedSets.keySet()) {     
        BdocAnnotationSet annset = new BdocAnnotationSet();
        annset.name = name;
        annset.annotations = new ArrayList<>();
        int next_annid = 0;
        for (Annotation ann : includedSets.get(name)) {
          BdocAnnotation bdocann = BdocAnnotation.fromGateAnnotation(ann);
          if(bdocann.id >= next_annid) {
            next_annid = bdocann.id + 1;
          }
          annset.annotations.add(bdocann);
        }
        if(nextAnnotationIds.containsKey(name)) {
          annset.next_annid = Math.max(next_annid, nextAnnotationIds.get(name));
        } else {
          annset.next_annid = next_annid;
        }
        annotation_sets.put(annset.name, annset);
      }     
      ret.annotation_sets = annotation_sets;
    }
    // do any offset fixup, if necessary
    ret.fixupOffsets(offset_type);
    return ret;
  }
  
  
  /**
   * Store the BdocDocument to a file.
   * 
   * @param path  where to store
   */
  public void toJson(File path) {
    BdocDocument bdoc = buildBdoc();
    new Saver().format(Format.JSON_MAP).to(path).save(bdoc);
  }
  
  /**
   * Store the BdocDocument to a file.
   * 
   * @param path  where to store
   */
  public void toJson(String path) {
    BdocDocument bdoc = buildBdoc();
    new Saver().format(Format.JSON_MAP).to(new File(path)).save(bdoc);
  }
  
  /**
   * Convert the BdocDocument to a JSON string.
   * 
   *
   * @return JSON String
   */
  public String toJsonString() {
    BdocDocument bdoc = buildBdoc();
    return new Saver().format(Format.JSON_MAP).asString().save(bdoc);
  }
  
  /**
   * Write the BdocDocument to the output stream.
   * 
   * @param os stream to write to
   */
  public void toJson(OutputStream os) {
    BdocDocument bdoc = buildBdoc();
    new Saver().format(Format.JSON_MAP).to(os).save(bdoc);
  }
    
}
