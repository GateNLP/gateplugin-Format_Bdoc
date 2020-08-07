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

import org.yaml.snakeyaml.Yaml;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.ChangeLog;
import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * How to serialize/deserialize JSON Map format.
 * 
 * @author Johann Petrak
 * 
 * Since each Yaml instance should be restricted to a thread, instances
 * of this class should not get shared between threads!
 * 
 */
public class YamlFormatSupportMap implements FormatSupport {

  
  private final Yaml yml;
  
  public YamlFormatSupportMap() {
    yml = new Yaml();
  }
  
  
  @Override
  public void save(BdocDocument bdoc, OutputStream os) {
    try {
      // Save without the class tag, so it looks like a map
      String rep = yml.dumpAsMap(bdoc);
      os.write(rep.getBytes("UTF-8"));
    } catch (IOException ex) {
      ex.printStackTrace(System.err);
      throw new GateRuntimeException("Could not convert Bdoc to YAML map", ex);
    }
  }

  @Override
  public BdocDocument load_bdoc(InputStream is) {    
    // load as map ... 
    Map<String,Object> map = yml.load(is);
    // and create the bdoc from the map
    BdocDocument bdoc = new BdocDocument(map);
    return bdoc;
  }

  @Override
  public ChangeLog load_log(InputStream is) {
    ChangeLog log;
    log = yml.load(is);
    return log;    
  }


}
