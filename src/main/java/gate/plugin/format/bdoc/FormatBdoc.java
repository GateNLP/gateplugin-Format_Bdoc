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
package gate.plugin.format.bdoc;

import gate.Document;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.docformats.Format;
import gate.lib.basicdocument.docformats.Loader;
import gate.util.DocumentFormatException;
import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * Read a document in "bdoc" format.
 * 
 * This will try to load a document in bdoc format, without known what the 
 * actual serialization that is used is. It will try to figure this out by 
 * reading part or all of the URL and then delegating to the proper 
 * serialization specific class. 
 * 
 * @author Johann Petrak
 */
@CreoleResource(
        name = "GATE Bdoc Format", 
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = true)},
        comment = "Format Bdoc",
        helpURL = "https://github.com/GateNLP/gateplugin-Format_Bdoc"
)
public class FormatBdoc 
        extends BaseFormatBdoc
{
  
  public FormatBdoc() {
    mimeType = "text";
    mimeSubtype = "bdoc";
    suffix = "bdoc";
  }
  private static final long serialVersionUID = 284756435L;
    
  /**
   * Logger.
   */
  public transient Logger logger = Logger.getLogger(this.getClass());
  
  /**
   * Method to read a file with this format.
   * @param dcmnt the document, we need the sourceURL from this.
   * @throws DocumentFormatException if error
   */
  @Override
  public void unpackMarkup(Document dcmnt) throws DocumentFormatException {
    URL sourceUrl = dcmnt.getSourceUrl();
    if(sourceUrl == null) {
      throw new GateRuntimeException("Source URL is null");
    }
    Format fmt;
    try (InputStream is = sourceUrl.openStream()) {
      byte buf[] = new byte[8];
      int n = is.read(buf);
      if(n<2) {
        throw new GateRuntimeException("Could not read bdoc from URL "+sourceUrl+" not enough data");
      }
      // TODO: for now, a very simple heuristic: if it starts with "{" must be
      // JSON, otherwise msgpack. should maybe check the msgpack bytes 
      // For JSON should maybe ignore byte order encoding marker 
      if(buf[0] == 123) { // ascii for "{"
        fmt = Format.JSON_MAP;
      // TODO: in case we want to support a serialization format that uses
      // a json array instead of map
      //} else if (buf[0] == 91) { // ascii for "["
      //  fmt = Format.JSON_ARR;
      } else {
        fmt = Format.MSGPACK;
      }
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not read MsgPack data from URL "+sourceUrl, ex);
    }    
    // open the URL again and call the proper loading function
    BdocDocument bdoc;
    try (InputStream is = sourceUrl.openStream()) {
      bdoc = new Loader().from(is).format(fmt).load_bdoc();
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not read Bdoc from URL "+sourceUrl, ex);
    } 
    updateDocument(dcmnt, bdoc);
  }
}
