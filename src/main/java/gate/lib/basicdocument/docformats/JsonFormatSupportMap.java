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
import gate.lib.basicdocument.ChangeLog;
import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * How to serialize/deserialize JSON Map format.
 * 
 * @author Johann Petrak
 */
public class JsonFormatSupportMap implements FormatSupport {

  private ObjectMapper om;
  
  public JsonFormatSupportMap() {
    om = new ObjectMapper();
  }
  
  @Override
  public void save(BdocDocument bdoc, OutputStream os) {
    try {
      om.writeValue(os, bdoc);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert Bdoc to JSON map", ex);
    }
  }

  @Override
  public BdocDocument load_bdoc(InputStream is) {
    BdocDocument bdoc;
    try {
      bdoc = om.readValue(is, BdocDocument.class);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert JSON map to Bdoc", ex);
    }
    return bdoc;
  }

  @Override
  public ChangeLog load_log(InputStream is) {
    ChangeLog log;
    try {
      log = om.readValue(is, ChangeLog.class);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert JSON map to ChangeLog", ex);
    }
    return log;    
  }


}
