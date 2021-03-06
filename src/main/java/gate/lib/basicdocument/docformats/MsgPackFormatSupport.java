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
package gate.lib.basicdocument.docformats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import gate.lib.basicdocument.BdocAnnotation;
import gate.lib.basicdocument.BdocAnnotationSet;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.ChangeLog;
import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.msgpack.jackson.dataformat.MessagePackFactory;

/**
 * How to serialize/deserialize MsgPack format.
 * 
 * @author Johann Petrak
 */
public class MsgPackFormatSupport implements FormatSupport {

  public static final String VERSION = "sm2"; // Simple MsgPack 2
  
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
  
  public MsgPackFormatSupport() {
    omLoad = initObjectMapper4Load();
    omDump = initObjectMapper4Dump();
  }
  
  ObjectMapper omLoad;
  ObjectMapper omDump;
  
  @Override
  public void save(BdocDocument bdoc, OutputStream os) {
    try {
      omDump.writeValue(os, VERSION); 
      
      omDump.writeValue(os, bdoc.offset_type);
      omDump.writeValue(os, bdoc.text);
      omDump.writeValue(os, bdoc.name);
      omDump.writeValue(os, bdoc.features);
      omDump.writeValue(os, bdoc.annotation_sets.size());
      for(Map.Entry<String,BdocAnnotationSet> e : bdoc.annotation_sets.entrySet()) {
        omDump.writeValue(os, e.getKey());
        BdocAnnotationSet as = e.getValue();
        omDump.writeValue(os, as.next_annid);
        omDump.writeValue(os, as.annotations.size());
        for(BdocAnnotation ann : as.annotations) {
          omDump.writeValue(os, ann.type);
          omDump.writeValue(os, ann.start);
          omDump.writeValue(os, ann.end);
          omDump.writeValue(os, ann.id);
          omDump.writeValue(os, ann.features);
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save to output stream", ex);
    }    
  }

  @Override
  @SuppressWarnings("unchecked")
  public BdocDocument load_bdoc(InputStream is) {
    try {
      String version = omLoad.readValue(is, String.class);
      if (!version.equals(VERSION)) { // check if we got our own format
        throw new GateRuntimeException("Not the expected MsgPack format sm2 but "+version);
      }
      BdocDocument bdoc = new BdocDocument();
      // we expect to be ready at this point to read everything after the version string
      bdoc.offset_type = omLoad.readValue(is, String.class);
      bdoc.text = omLoad.readValue(is, String.class);
      bdoc.name = omLoad.readValue(is, String.class);
      bdoc.features = omLoad.readValue(is, Map.class);
      int nannsets = omLoad.readValue(is, Integer.class);
      Map<String, BdocAnnotationSet> annsets = new HashMap<>();
      for(int i = 0; i<nannsets; i++) {
        String name = omLoad.readValue(is, String.class);
        if(name == null) {
          name = "";
        }
        BdocAnnotationSet as = new BdocAnnotationSet();
        as.name = name;
        as.next_annid = omLoad.readValue(is, Integer.class);
        int nanns = omLoad.readValue(is, Integer.class);
        List<BdocAnnotation> anns = new ArrayList<>(nanns);
        for(int j=0; j<nanns; j++) {
          BdocAnnotation ann = new BdocAnnotation();
          ann.type = omLoad.readValue(is, String.class);
          ann.start = omLoad.readValue(is, Integer.class);
          ann.end = omLoad.readValue(is, Integer.class);
          ann.id = omLoad.readValue(is, Integer.class);
          ann.features = omLoad.readValue(is, Map.class);
          anns.add(ann);
        }
        as.annotations = anns;   
        annsets.put(name, as);
      }
      bdoc.annotation_sets = annsets;
      
      return bdoc;
    } catch (IOException ex) {
      throw new GateRuntimeException("Error parsing MsgPack data", ex);
    }
        
    
  }

  @Override
  public ChangeLog load_log(InputStream is) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
