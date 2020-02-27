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

package gate.lib.basicdocument.docformats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import gate.lib.basicdocument.BdocAnnotation;
import gate.lib.basicdocument.BdocAnnotationSet;
import gate.lib.basicdocument.ChangeLog;
import gate.lib.basicdocument.BdocDocument;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.msgpack.jackson.dataformat.MessagePackFactory;

// TODO!!
// So far this is just a copy of the simplejson class
// We need to actually implement the de/serialization using MsgPack!
// For this we need our own utility class to handle objects where we 
// do not need the type in advance!

/**
 * Serialize and deserialize BdocDocument and BdocChangeLog instances as MsgPack.
 * 
 * These are convenience methods to make it easy to serialise and deserialise
 * BdocDocument and BdocChangeLog instances as MsgPack.  
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class MsgPack {
    
  public static final int VERSION = 1;
  public MsgPack() {
  }
  
  private ObjectMapper initObjectMapper4Dump() {
    ObjectMapper om = new ObjectMapper(new MessagePackFactory());
    om.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    return om;
  }
  private ObjectMapper initObjectMapper4Load() {
    ObjectMapper om = new ObjectMapper(new MessagePackFactory());
    om.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    return om;
  }

  /**
   * Serialise a BdocDocument to an OutputStream.
   * 
   * @param bdoc BdocDocument instance
   * @param os output stream to serialise to
   */
  public void dump(BdocDocument bdoc, OutputStream os) {
    ObjectMapper om = initObjectMapper4Dump();
    try {
      om.writeValue(os, VERSION); 
      om.writeValue(os, bdoc.gatenlp_type);
      om.writeValue(os, bdoc.offset_type);
      om.writeValue(os, bdoc.text);
      om.writeValue(os, bdoc.features);
      System.err.println("DEBUG: writing nr annsets: "+bdoc.annotation_sets.size());
      om.writeValue(os, bdoc.annotation_sets.size());
      for(Map.Entry<String,BdocAnnotationSet> e : bdoc.annotation_sets.entrySet()) {
        System.err.println("DEBUG: writing annset name: "+e.getKey());
        om.writeValue(os, e.getKey());
        BdocAnnotationSet as = e.getValue();
        om.writeValue(os, as.next_annid);
        System.err.println("DEBUG: writing nr anns: "+as.annotations.size());
        om.writeValue(os, as.annotations.size());
        for(BdocAnnotation ann : as.annotations) {
          System.err.println("DEBUG writing annotation type "+ann.type);
          om.writeValue(os, ann.type);
          om.writeValue(os, ann.start);
          om.writeValue(os, ann.end);
          om.writeValue(os, ann.id);
          om.writeValue(os, ann.features);
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save to output stream", ex);
    }    
  }  
  
  /**
   * Serialise a BdocDocument to a file. 
   * 
   * @param bdoc Bdoc document
   * @param path the file path where to write to, will get overwritten
   */
  public void dump(BdocDocument bdoc, File path) {
    try (OutputStream os = new FileOutputStream(path)) {
      dump(bdoc, os);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to "+path, ex);
    }
  }
  
  /**
   * Serialise a ChangeLog to an OutputStream.
   * 
   * @param clog ChangeLog instance
   * @param os output stream to serialise to
   */
  public void dump(ChangeLog clog, OutputStream os) {
    ObjectMapper om = initObjectMapper4Dump();
    try {
      om.writeValue(os, "bdoc");
      om.writeValue(os, 1);
      om.writeValue(os, clog.gatenlp_type);
      om.writeValue(os, clog.offset_type);
      om.writeValue(os, clog.changes);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save ChangeLog to output stream", ex);
    }    
  }

  /**
   * Serialise a ChangeLog to a file. 
   * 
   * @param clog ChangeLog instance
   * @param path the file path where to write to, will get overwritten
   */
  public void dump(ChangeLog clog, File path) {
    try (OutputStream os = new FileOutputStream(path)) {
      dump(clog, os);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to "+path, ex);
    }
  }


  
  // ************************************************************************8
  // 2) Load: it seems this works properly out of the box, no need for custom readers
  
  // 2.1) BdocDocument
  /**
   * Load MsgPack from stream
   * @param is open stream to load from
   * @return bdoc document instance
   */
  @SuppressWarnings("unchecked")
  public BdocDocument load_doc(InputStream is) {
    // TODO
    BdocDocument bdoc = new BdocDocument();
    ObjectMapper om = initObjectMapper4Load();
    try {
      Integer vint = om.readValue(is, Integer.class);
      if(vint != 1) {
        throw new GateRuntimeException("Did not get expected version number "+VERSION+" but "+vint);
      }
      String vstr = om.readValue(is, String.class);
      if(!"Document".equals(vstr)) {
        throw new GateRuntimeException("Did not get expected type: Document but "+vstr);
      }
      bdoc.offset_type = om.readValue(is, String.class);
      bdoc.text = om.readValue(is, String.class);
      bdoc.features = om.readValue(is, Map.class);
      System.err.println("DEBUG: got features from msgp: "+bdoc.features);
      int nannsets = om.readValue(is, Integer.class);
      System.err.println("DEBUG: got nr annsets from msgp: "+nannsets);
      Map<String, BdocAnnotationSet> annsets = new HashMap<>();
      for(int i = 0; i<nannsets; i++) {
        String name = om.readValue(is, String.class);
        if(name == null) {
          name = "";
        }
        System.err.println("DEBUG: annset name from msgp: "+name);
        BdocAnnotationSet as = new BdocAnnotationSet();
        as.next_annid = om.readValue(is, Integer.class);
        int nanns = om.readValue(is, Integer.class);
        List<BdocAnnotation> anns = new ArrayList<>(nanns);
        for(int j=0; j<nanns; j++) {
          BdocAnnotation ann = new BdocAnnotation();
          ann.type = om.readValue(is, String.class);
          ann.start = om.readValue(is, Integer.class);
          ann.end = om.readValue(is, Integer.class);
          ann.id = om.readValue(is, Integer.class);
          ann.features = om.readValue(is, Map.class);
          anns.add(ann);
        }
        as.annotations = anns;   
        annsets.put(name, as);
      }
      bdoc.annotation_sets = annsets;
    } catch (IOException ex) {
      throw new RuntimeException("Could not load BdocDocument from MsgPack input stream", ex);
    }    
    return bdoc;
  }
  
  /**
   * Load MsgPack from reader
   * @param rdr open reader to load from
   * @return bdoc document instance
   */
  @SuppressWarnings("unchecked")
  public BdocDocument load_doc(Reader rdr) {
    // TODO
    BdocDocument bdoc = new BdocDocument();
    ObjectMapper om = initObjectMapper4Load();
    try {
      Integer vint = om.readValue(rdr, Integer.class);
      if(vint != 1) {
        throw new GateRuntimeException("Did not get expected version number "+VERSION+" but "+vint);
      }
      String vstr = om.readValue(rdr, String.class);
      if(!"Document".equals(vstr)) {
        throw new GateRuntimeException("Did not get expected type: Document but "+vstr);
      }
      bdoc.offset_type = om.readValue(rdr, String.class);
      bdoc.text = om.readValue(rdr, String.class);
      bdoc.features = om.readValue(rdr, Map.class);
      int nannsets = om.readValue(rdr, Integer.class);
      Map<String, BdocAnnotationSet> annsets = new HashMap<>();
      for(int i = 0; i<nannsets; i++) {
        String name = om.readValue(rdr, String.class);
        BdocAnnotationSet as = new BdocAnnotationSet();
        as.next_annid = om.readValue(rdr, Integer.class);
        int nanns = om.readValue(rdr, Integer.class);
        List<BdocAnnotation> anns = new ArrayList<>(nanns);
        for(int j=0; j<nanns; j++) {
          BdocAnnotation ann = new BdocAnnotation();
          ann.type = om.readValue(rdr, String.class);
          ann.start = om.readValue(rdr, Integer.class);
          ann.end = om.readValue(rdr, Integer.class);
          ann.id = om.readValue(rdr, Integer.class);
          ann.features = om.readValue(rdr, Map.class);
          anns.add(ann);
        }
        as.annotations = anns;        
      }
    } catch (IOException ex) {
      throw new RuntimeException("Could not load BdocDocument from MsgPack reader", ex);
    }    
    return bdoc;
  }
  
  /**
   * Load MsgPack from File
   * @param file open stream to load from
   * @return bdoc document instance
   */
  public BdocDocument load_doc(File file) {
    // TODO
    BdocDocument bdoc = new BdocDocument();
    return bdoc;
  }
  
  // 2.1) BdocDocument
  
  /**
   * Load MsgPack changelog representation from stream
   * @param instream open stream
   * @return changelog instance
   */
  public ChangeLog load_log(InputStream instream) {
    // TODO
    ChangeLog clog = new ChangeLog();
    return clog;
  }
  
  /**
   * Load MsgPack changelog representation from File
   * @param file open stream
   * @return changelog instance
   */
  public ChangeLog load_log(File file) {
    // TODO
    ChangeLog clog = new ChangeLog();
    return clog;
  }
  
  
}
