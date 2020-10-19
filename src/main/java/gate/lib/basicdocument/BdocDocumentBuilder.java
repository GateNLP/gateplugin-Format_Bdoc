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
import gate.Document;
import gate.corpora.DocumentImpl;
import gate.lib.basicdocument.docformats.Format;
import gate.lib.basicdocument.docformats.Saver;
import gate.util.GateRuntimeException;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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
  HashMap<String,Set<Annotation>> knownSets = new HashMap<>();
  Set<String> includedSetNames =  new HashSet<>();
  HashMap<String,Integer> nextAnnotationIds = new HashMap<>();
  HashMap<String, Object> includedFeatures = new HashMap<>();
  boolean includePlaceholderSets = false;
  String offset_type = "j";
  String name = "";
  int nextAnnId = 1;

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
    this.name = doc.getName();
    if(! (doc instanceof DocumentImpl)) {
      throw new GateRuntimeException("Cannot build Bdoc document from something that is not a gate.corpora.DocumentImpl");
    }
    nextAnnId = ((DocumentImpl)doc).getNextAnnotationId();
    knownSets.put("", doc.getAnnotations());
    includedSetNames.add("");
    for (String name : doc.getNamedAnnotationSets().keySet()) {      
      knownSets.put(name, doc.getAnnotations(name));
      includedSetNames.add(name);
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
    knownSets.put(name, annset);
    includedSetNames.add(name);
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
   * Set/update the name of the bdoc document. 
   * 
   * @param name the new document name
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setName(String name) {
    this.name = name;
    return this;
  }
  
  /**
   * Set the list of included AnnotationSet names.
   * 
   * This allows you to select the annotation sets to  actually use from
   * a GATE document.
   *
   * If set names are specified which are not in initial or currently known list of sets,
   * this is silently ignored. That way the same selection can be used for many documents,
   * even if some of them do not actually contain some of the sets.
   *
   * @param names a collection of names to choose and use for the BdocDocument
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setAnnotationSetNames(Collection<String> names) {
    includedSetNames.clear();
    includedSetNames.addAll(names);
    return this;
  }

  /**
   * Set if placeholders sets should get included.
   *
   * This is only relevant if a subset of all annotation sets is selected to get added.
   * @param flag
   * @return
   */
  public BdocDocumentBuilder setIncludePlaceholderSets(boolean flag) {
    includePlaceholderSets = flag;
    return this;
  }
  
  /**
   * Set the list of included features.
   * 
   * This allows you to select the features to actually use from a GATE document.
   * If features are selected which are not known, this is silently ignored.
   *
   *
   * @param featurenames a collection of  feature names to choose and use for 
   * the BdocDocument
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setFeatureNames(Collection<String> featurenames) {
    HashMap<String, Object> newFeatures = new HashMap<>();
    for (String tmpname : featurenames) {
      if(includedFeatures.containsKey(tmpname)) {
        newFeatures.put(tmpname, includedFeatures.get(tmpname));
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
    ret.name = name;
    if(includedFeatures.size() > 0) {
      ret.features = includedFeatures;
    }
    if(knownSets.size() > 0) {
      HashMap<String, BdocAnnotationSet> annotation_sets = new HashMap<>();
      for(String name : knownSets.keySet()) {
        if(!includedSetNames.contains(name)) {
          // if a set should not get added, we either completely ignore it, or if
          // includePlaceholderSets is true, we add an empty set with that name which
          // has the next annotation id set to the one from the gate document
          if (includePlaceholderSets) {
            BdocAnnotationSet annset = new BdocAnnotationSet();
            annset.name = name;
            annset.annotations = new ArrayList<>();
            annset.next_annid = nextAnnId;
            annotation_sets.put(annset.name, annset);
          }
          continue;
        }
        BdocAnnotationSet annset = new BdocAnnotationSet();
        annset.name = name;
        annset.annotations = new ArrayList<>();
        int next_annid = 0;
        for (Annotation ann : knownSets.get(name)) {
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
