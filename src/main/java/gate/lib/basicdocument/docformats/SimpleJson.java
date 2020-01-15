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

import com.fasterxml.jackson.jr.ob.JSON;
import gate.lib.basicdocument.ChangeLog;
import gate.lib.basicdocument.BdocDocument;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Serialize and deserialize BdocDocument and BdocChangeLog instances as JSON.
 * 
 * These are convenience methods to make it easy to serialise and deserialise
 * BdocDocument and BdocChangeLog instances as JSON. 
 * This is done in a separate class since 
 * we may want to use other serialisation formats in the future and to 
 * hide any details of the de/serialisation.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class SimpleJson {
  
  // 1) Writing to JSON: this is really simple, we essentially just write 
  // exactly what we have
  
  /**
   * Serialise a BdocDocument to a file. 
   * 
   * @param bdoc Bdoc document
   * @param path the file path where to write to, will get overwritten
   */
  public void dump(BdocDocument bdoc, File path) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(bdoc, path);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to "+path, ex);
    }
  }
  
  /**
   * Serialise a ChangeLog to a file. 
   * 
   * @param clog ChangeLog instance
   * @param path the file path where to write to, will get overwritten
   */
  public void dump(ChangeLog clog, File path) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(clog, path);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to "+path, ex);
    }
  }

  /**
   * Serialise a BdocDocument to a Writer.
   * 
   * @param bdoc BdocDocument instance
   * @param writer writer to serialise to
   */
  public void dump(BdocDocument bdoc, Writer writer) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(bdoc, writer);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to writer", ex);
    }
    
  }

  /**
   * Serialise a ChangeLog to a Writer.
   * 
   * @param clog ChangeLog instance
   * @param writer writer to serialise to
   */
  public void dump(ChangeLog clog, Writer writer) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(clog, writer);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to writer", ex);
    }
    
  }
  
  /**
   * Serialise a BdocDocument to an OutputStream.
   * 
   * @param bdoc BdocDocument instance
   * @param ostream output stream to serialise to
   */
  public void dump(BdocDocument bdoc, OutputStream ostream) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(bdoc, ostream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to output stream", ex);
    }    
  }  
  
  /**
   * Serialise a ChangeLog to an OutputStream.
   * 
   * @param clog ChangeLog instance
   * @param ostream output stream to serialise to
   */
  public void dump(ChangeLog clog, OutputStream ostream) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(clog, ostream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to output stream", ex);
    }    
  }

  /**
   * Serialise a BdocDocument as a String
   * 
   * @param bdoc Bdoc document
   * @return the generated JSON string
   */
  public String dumps(BdocDocument bdoc) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.asString(bdoc);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON String from BdocDocument", ex);
    }    
  }
  
  /**
   * Serialise a BdocDocument as a String
   * 
   * @param clog ChangeLog instance
   * @return the generated JSON string
   */
  public String dumps(ChangeLog clog) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.asString(clog);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON String from ChangeLog", ex);
    }    
  }
  
  
  
  // 2) Load: it seems this works properly out of the box, no need for custom readers
  
  // 2.1) BdocDocument
  /**
   * Load JSON from stream
   * @param instream open stream to load from
   * @return bdoc document instance
   */
  public BdocDocument load_doc(InputStream instream) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, instream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from input stream", ex);
    }
  }
  
  /**
   * Load JSON from reader
   * @param reader the open reader to load from
   * @return bdoc document instance
   */
  public BdocDocument load_doc(Reader reader) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, reader);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from reader", ex);
    }
  }
  
  /**
   * Load JSON from String
   * @param json json string
   * @return bdoc document instance
   */
  public BdocDocument loads_doc(String json) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, json);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from String", ex);
    }
  }

  // 2.1) BdocDocument
  
  /**
   * Load JSON changelog representation from stream
   * @param instream open stream
   * @return changelog instance
   */
  public ChangeLog load_log(InputStream instream) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(ChangeLog.class, instream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocChangeLog from input stream", ex);
    }
  }
  
  /**
   * Load JSON changelog representation from reader
   * @param reader open reader
   * @return changelog instance
   */
  public ChangeLog load_log(Reader reader) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(ChangeLog.class, reader);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocChangeLog from reader", ex);
    }
  }
  
  /**
   * Load JSON changelog representation from string
   * @param json string
   * @return changelog instance
   */
  public ChangeLog loads_log(String json) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(ChangeLog.class, json);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocChangeLog from String", ex);
    }
  }
  
}
