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
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO: use offset mapper when copying over the annotations from bdoc/changelog
//   in case those offsets are type python



/**
 * A class that allows to update a GATE document from a BasicDocument
 *
 * @author Johann Petrak
 */
public class GateDocumentUpdater {

  /**
   * What to do when adding an annotation that already exists in the document.
   */
  public static enum HandleExistingAnns {
    /**
     * Completely replace the annotation with the new one.
     */
    REPLACE_ANNOTATION, // completely replace with the new one
    /**
     * Completely replace the features of the existing annotation.
     */
    REPLACE_FEATURES, // just completely replace the features 
    /**
     * Add new and update existing features, do not delete any.
     */
    UPDATE_FEATURES, // add new and update existing features, do not delete any
    /**
     * Only add new features.
     */
    ADD_NEW_FEATURES, // only add new features
    /**
     * Ignore that annotation.
     */
    IGNORE, // ignore that annotation, do nothing
    /**
     * Add as a new annotation with a new id.
     */
    ADD_WITH_NEW_ID,  // add that annotation with a new id
  }
  
  /**
   * What to do when adding a new annotation.
   */
  public static enum HandleNewAnns {
    /**
     * Add as a new annotation with a new id.
     */
    ADD_WITH_NEW_ID,  // add that annotation with a new id
    /**
     * Add as a new annotation with a new id.
     */
    ADD_WITH_BDOC_ID,  // add that annotation with the id we get from the BDOC    
  }
  
  private Document gateDocument;

  private HandleExistingAnns handleExistingAnns = HandleExistingAnns.ADD_WITH_NEW_ID;
  private HandleNewAnns handleNewAnns = HandleNewAnns.ADD_WITH_BDOC_ID;

  /**
   * If null, use all, otherwise the set of annotation set names to use.
   */
  private Set<String> annsetnames;

  /**
   * If null, use all, otherwise the set of document feature names to use.
   */
  private Set<String> featurenames;
  
  /**
   * OffsetMapper for converting offsets to Java.
   * If we update from a BdocDocument of ChangeLog which does not have Java
   * offsets, we first create the offset mapper and store it here before any
   * annotations get copied. The offset mapper is only built whenever the 
   * first annotation actually needs to get converted.
   */
  private OffsetMapper offsetMapper = null;

  /**
   * Create a document updater with the default options. Initially, all
   * information from the update source except text will be used to update the
   * GATE document. Use the noXxx() methods followed by useXxx() methods to
   * select a specific set of information.
   *
   * @param doc the GATE document to update
   */
  public GateDocumentUpdater(Document doc) {
    this.gateDocument = doc;

  }
  
  /**
   * Create a document updater for updating a brand new document with this text.
   * 
   * This can be used to convert a BdocDocument to a GATE document and still
   * control, if necessary, which annotations/features of the BdocDocument
   * should get converted. 
   * 
   * @param text initial text to start building the document from
   */
  public GateDocumentUpdater(String text) {
    try {
      this.gateDocument = Factory.newDocument(text);
    } catch (ResourceInstantiationException ex) {
      throw new GateRuntimeException("Could not create GATE document from the given text", ex);
    }
  }

  // Methods to set options about how to update the document
  // These can be chained as necessary
  /**
   * Set the current list of known annotation set names to add to empty.
   * Initially, all annotation sets are added, this can be used to start giving
   * an explicit list of annotation set names to use by subsequently calling
   * useAnnotationSet(name)
   *
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater noAnnotationSet() {
    annsetnames = new HashSet<>();
    return this;
  }

  /**
   * Include this annotation set in the updates.
   *
   * @param name name of annotation set to include
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater useAnnotationSet(String name) {
    annsetnames.add(name);
    return this;
  }

  /**
   * Clear the list of document feature names to use for updating.
   *
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater noFeature() {
    featurenames = new HashSet<String>();
    return this;
  }

  /**
   * Add feature name to include for updating.
   *
   * @param name the name of the feature
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater useFeature(String name) {
    featurenames.add(name);
    return this;
  }

  /**
   * Specify how annotations with an id that already exists should be
   * handled.Default is ADD_WITH_NEW_ID
   *
   *
   * @param option The annotation handling option to use
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater handleExistingAnnotation(HandleExistingAnns option) {
    handleExistingAnns = option;
    return this;
  }
  
  /**
   * Specify how new annotations should be
   * handled.Default is ADD_WITH_NEW_ID.
   *
   * For restoring a document exactly as it was from a BDOC representation,
   * ADD_WITH_BDOC_ID is necessary!
   * 
   * @param option The annotation handling option to use
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater handleNewAnnotation(HandleNewAnns option) {
    handleNewAnns = option;
    return this;
  }
  

  /**
   * Add an annotation to the GATE annotation set.
   * This uses the information from a changelog or a bdoc document to
   * add an annotation to the GATE annotation set, or update an annotation.
   * The flags handleNewAnns and handleExistingAnns are used to influence 
   * the behavior. 
   * @param gateset the GATE annotation set to update
   * @param bdocannid  the annotation id of the annotation from changelog/bdoc
   * @param startoffset start offset
   * @param endoffset end offset
   * @param bdoctype annotation type
   * @param bdocfeatures annotation features
   */
  private void addAnnotation(AnnotationSet gateset,
          int bdocannid, int bdocstart, int bdocend, String bdoctype,
          Map<String, Object> bdocfeatures, String offsetType) {
    // make sure we always have the offsets as java offsets
    long startoffset = convertOffset(bdocstart, offsetType);
    long endoffset = convertOffset(bdocend, offsetType);
    
    // make sure we always have a non-null feature map, use a new empty one
    // if necessary.
    Map<String, Object> bdoc_fm = 
            (bdocfeatures == null)
            ? new HashMap<>()
            : bdocfeatures;
    // try to get the annotation with the annotation id 
    Annotation gateann = gateset.get(bdocannid);
    
    // Case 1: the annotation does not already exist and we want to add it
    // with a new id
    if (gateann == null && handleNewAnns == HandleNewAnns.ADD_WITH_NEW_ID) {
      try {
        gateset.add(startoffset, endoffset,
                bdoctype, gate.Utils.toFeatureMap(bdoc_fm));
      } catch (InvalidOffsetException ex) {
        throw new RuntimeException("Cannot add annotation", ex);
      }     
    // Case 2: the annotation does not already exist and we want to add it 
    // with its own existing id.
    } else if (gateann == null && handleNewAnns == HandleNewAnns.ADD_WITH_BDOC_ID) {
      try {
        gateset.add(bdocannid,
                startoffset, endoffset,
                bdoctype, gate.Utils.toFeatureMap(bdoc_fm));
      } catch (InvalidOffsetException ex) {
        throw new RuntimeException("Cannot add annotation", ex);
      }     
    // Case 3: the annotation already exists but we want to add with a new id
    } else if (gateann != null && handleExistingAnns == HandleExistingAnns.ADD_WITH_NEW_ID) {
      try {
        gateset.add(startoffset, endoffset,
                bdoctype, gate.Utils.toFeatureMap(bdoc_fm));
      } catch (InvalidOffsetException ex) {
        throw new RuntimeException("Cannot add annotation", ex);
      }
    // All other cases: we already have that annotation, and we want to do 
    // something with it, depending on the HandleExistingAnns flag
    } else if(gateann != null) {  // make null pointer checker happy
      // an annotation with this id already exists, choose what to do
      // first get the existing featuremap and map string feature names
      // to the original keys. in theory this could yield duplicates but
      // we do not care about this for now, those features really should all
      // have string names! null keys are ignored
      
      // NOTE: the offsets we get from the bdoc/chlog should correspond to
      // the offsets of the existing annotation!
      // We check this here to catch any bugs that may still exist!
      if (!gateann.getStartNode().getOffset().equals(startoffset) ||
          !gateann.getEndNode().getOffset().equals(endoffset)) {
        throw new GateRuntimeException(
                "Annotation offsets do not match for GATE annotation: "+
                        gateann+
                        " and bdoc/chlog annotation: from(orig)="+bdocstart+
                        ", from(converted)="+startoffset+
                        ", to(orig)="+bdocend+
                        ", to(converted)="+endoffset
        );
      }
      
      
      FeatureMap gatefm = gateann.getFeatures();
      Map<String, Object> name2key = new HashMap<>();
      for (Object key : gatefm.keySet()) {
        if (key != null) {
          name2key.put(
                  (key instanceof String)
                          ? (String) key : key.toString(), key);
        }
      }
      // Subsequently, when we need to figure out if a feature is in the 
      // featuremap, use the name2key mapping
      switch (handleExistingAnns) {
        case ADD_NEW_FEATURES:
          for (String fname : bdoc_fm.keySet()) {
            if (!(name2key.containsKey(fname) && gatefm.containsKey(name2key.get(fname)))) {
              gatefm.put(fname, bdoc_fm.get(fname));
            }
          }
          break;
        // already gets handled above!
        // case ADD_WITH_NEW_ID:            
        //  break;
        case REPLACE_ANNOTATION:
          // I think there is no way to actually update need to remove and add with id
          gateset.remove(gateann);
          try {
            gateset.add(bdocannid, startoffset, endoffset,
                    bdoctype, gate.Utils.toFeatureMap(bdoc_fm));
          } catch (InvalidOffsetException ex) {
            throw new RuntimeException("Cannot add annotation", ex);
          }
          break;
        case REPLACE_FEATURES:
          gatefm.clear();
          for (String fname : bdoc_fm.keySet()) {
            gatefm.put(fname, bdoc_fm.get(fname));
          }
          break;
        case UPDATE_FEATURES:
          bdoc_fm.keySet().forEach((fname) -> {
            gatefm.put(fname, bdoc_fm.get(fname));
        });
          break;

        case IGNORE:
          break;
        default:
          throw new RuntimeException("Should never happen!");
      }
    }

  }

  private void addAnnotationSet(BdocAnnotationSet annset, String offsetType) {
    String setname = annset.name;
    if(setname == null) {
      setname = "";
    }
    AnnotationSet gateset;
    if (setname.equals("")) {
      gateset = gateDocument.getAnnotations();
    } else {
      gateset = gateDocument.getAnnotations(setname);
    }
    annset.annotations.forEach((bdocann) -> {
      addAnnotation(gateset,
              bdocann.id, bdocann.start, bdocann.end, bdocann.type,
              bdocann.features, offsetType);
    });
  }

  /**
   * Actually carry out the update of the GATE document from the BdocDocument.
   * 
   * This carries out the update with whatever options have been set.
   * 
   * @param bdoc the bdoc to use for the updates
   * @return the updated GATE document
   */
  public Document fromBdoc(BdocDocument bdoc) {
    // can only assign features if there are any in the bdoc
    if (bdoc.features != null) {
      if (featurenames == null) {
        gateDocument.getFeatures().putAll(bdoc.features);
      } else {
        featurenames.forEach((fname) -> {
          gateDocument.getFeatures().put(fname, bdoc.features.get(fname));
        });
      }
    }
    if (bdoc.annotation_sets != null) {
      if (annsetnames == null) {
        bdoc.annotation_sets.keySet().forEach((annsetname) -> {
          addAnnotationSet(bdoc.annotation_sets.get(annsetname), bdoc.offset_type);
        });
      } else {
        annsetnames.forEach((annsetname) -> {
          addAnnotationSet(bdoc.annotation_sets.get(annsetname), bdoc.offset_type);
        });
      }
    }
    return gateDocument;
  }

  /**
   * Actually carry out the update of the GATE document from the Bdoc ChangeLog.
   * 
   * This carries out the update with whatever options have been set.
   * 
   * @param chlog the changelog to use for the updates
   * @return returns the updated GATE document 
   */
  public Document fromChangeLog(ChangeLog chlog) {
    for (Map<String, Object> chg : chlog.changes) {
      // doc-features:clear setname, id
      // doc-feature:set, feature, value
      // doc-feature:remove, feature
      // ann-features:clear set, id
      // ann-feature:set, annid, feature, value
      // ann-feature:remove, annid, feature
      // annotation:add, set, start, end, type, features, id
      // annotation:remove, set, id
      // annotations:clear, setname
      // annotations:add, setname
      String cmd = (String) chg.get("command");
      String setname = (String) chg.get("set");
      AnnotationSet annset = null;
      if (setname != null) {
        annset
                = setname.equals("")
                ? gateDocument.getAnnotations()
                : gateDocument.getAnnotations(setname);
      }
      Integer id = (Integer) chg.get("id");
      String feature = (String) chg.get("feature");
      Object value = chg.get("value");
      switch (cmd) {
        case "doc-features:clear":
          gateDocument.getFeatures().clear();
          break;
        case "ann-features:clear":
          if (setname.equals("")) {
            gateDocument.getAnnotations().clear();
          } else {
            gateDocument.getAnnotations(setname).clear();
          }
          break;
        case "doc-feature:set":
          gateDocument.getFeatures().put(feature, value);
          break;
        case "ann-feature:set":
          if (annset != null) {
            Annotation ann = annset.get(id);
            if (ann == null) {
              // IMPORTANT: this is silently ignored because the changelog can
              // sometimes contain feature changes for annotations which are
              // not in the set any longer. This happens if an annotation gets
              // removed from the set, but still exists as an annotation
              // and somebody sets a feature on that annotation. 
              // throw new RuntimeException("Annotation does not exist with id " + id);
            } else {
                ann.getFeatures().put(feature, value);
            }
          } // TODO: how could it happen that there is no annset?
          break;
        case "doc-feature:remove":
          gateDocument.getFeatures().remove(feature);
          break;
        case "ann-feature:remove":
          if (annset != null) {
            Annotation ann = annset.get(id);
            if (ann == null) {
              throw new RuntimeException("Annotation does not exist with id " + id);
            } else {
              ann.getFeatures().remove(feature);
            }
          }
          break;
        case "annotation:add":
          int start = (Integer) chg.get("start");
          int end = (Integer) chg.get("end");
          String type = (String) chg.get("type");
          @SuppressWarnings("unchecked")
          Map<String, Object> features = (Map<String, Object>) chg.get("features");
          addAnnotation(annset, id, start, end, type, features, chlog.offset_type);
          break;
        case "annotation:remove":
          if (annset != null) {
            Annotation gateann = annset.get(id);
            annset.remove(gateann);
          }
          break;
        case "annotations:clear":
          if (annset != null) {
            annset.clear();
          }
          break;
        case "annotations:remove":
          if (setname != null) {
            if (setname.isEmpty() && annset != null) {
              annset.clear();
            } else {
              gateDocument.removeAnnotationSet(setname);
            }
          }
          break;
      }

    }
    return gateDocument;
  }
  
  /**
   * This converts the given offset from python to java, if necessary.
   * If the offsetType is python, then the offset mapper is used to convert
   * the offset to Java, and if we do not have an offset mapper yet, we 
   * create it on the fly.
   * 
   * @param offset the offset from the bdoc or changelog to convert
   * @param offsetType the offset type of the bdoc or changelog
   * @return  converted offset, if necessary
   */
  private long convertOffset(int offset, String offsetType) {
    if("p".equals(offsetType)) {
      if(offsetMapper == null) {
        offsetMapper = new OffsetMapper(gateDocument.getContent().toString());
      }
      return (long)offsetMapper.convertToJava(offset);
    } else {
      return (long)offset;
    }
  }
  
}
