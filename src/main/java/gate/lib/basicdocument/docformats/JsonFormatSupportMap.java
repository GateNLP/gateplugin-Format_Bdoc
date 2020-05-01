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

/**
 * How to serialize/deserialize JSON Map format.
 * 
 * @author Johann Petrak
 */
public class JsonFormatSupportMap implements FormatSupport {

  @Override
  public void save(BdocDocument bdoc, OutputStream os) {
    ObjectMapper om = new ObjectMapper();
    try {
      om.writeValue(os, bdoc);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert Bdoc to JSON map", ex);
    }
  }

  @Override
  public BdocDocument load_bdoc(InputStream is) {
    ObjectMapper om = new ObjectMapper();
    BdocDocument bdoc;
    try {
      bdoc = om.readValue(is, BdocDocument.class);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert JSON map to Bdoc", ex);
    }
    return bdoc;
  }


}
