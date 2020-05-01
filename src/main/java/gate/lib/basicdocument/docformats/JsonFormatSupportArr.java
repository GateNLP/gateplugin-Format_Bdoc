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

import com.fasterxml.jackson.databind.ObjectMapper;
import gate.lib.basicdocument.BdocDocument;
import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Experimental code for compat JSON representation as arrays, not maps.
 * 
 * For now this works by first converting the BdocDocument in a POJO representation
 * which uses arrays (so field names are implicit).
 * 
 * @author Johann Petrak
 */
public class JsonFormatSupportArr implements FormatSupport {

  
  public List<Object> bdoc2list(BdocDocument bdoc) {
    ArrayList<Object> ret = new ArrayList<>();
    return ret;
  }
  
  public BdocDocument list2bdoc(List<Object> list) {
    BdocDocument ret = new BdocDocument();
    return ret;
  }
  
  @Override
  public void save(BdocDocument bdoc, OutputStream os) {
    List<Object> bdocAsList = bdoc2list(bdoc);
    ObjectMapper om = new ObjectMapper();
    try {
      om.writeValue(os, bdocAsList);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert Bdoc to JSON array", ex);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public BdocDocument load_bdoc(InputStream is) {
    ObjectMapper om = new ObjectMapper();
    List<Object> bdocAsList;
    try {
      bdocAsList = (List<Object>)om.readValue(is, List.class);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert JSON array to Bdoc", ex);
    }
    BdocDocument bdoc = list2bdoc(bdocAsList);
    return bdoc;
  }

}
