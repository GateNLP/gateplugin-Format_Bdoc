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

import java.io.*;
import gate.*;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.corpora.MimeType;
import gate.corpora.RepositioningInfo;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.GateDocumentUpdater;
import gate.lib.basicdocument.docformats.SimpleJson;
import gate.util.DocumentFormatException;
import gate.util.InvalidOffsetException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;

/**
 * Read document in Gzip-compressed BdocJson Format.
 * 
 * @author Johann Petrak
 */
@CreoleResource(
        name = "GATE Gzipped Bdoc-Json Format", 
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = true)},
        comment = "Support for JSON-serialised GATE basic document, GZIP compressed",
        helpURL = "https://github.com/GateNLP/gateplugin-Format_Bdoc"
)
public class FormatBdocJsonGzip extends DocumentFormat {
  private static final long serialVersionUID = 687845503643563918L;
  
  @Override
  public Boolean supportsRepositioning() {
    return false;
  } 
  /**
   * Logger.
   */
  public Logger logger = Logger.getLogger(this.getClass());
  /**
   * Register support for this format.
   * @return the resource instance
   * @throws ResourceInstantiationException  if an error occurs
   */
@Override
  public Resource init() throws ResourceInstantiationException {
    MimeType mime = new MimeType("text", "bdocjson+gzip");
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),this);
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    suffixes2mimeTypeMap.put("bdocjson.gz", mime);
    setMimeType(mime);
    return this;
  }
  
  /**
   * De-register support for this format on cleanup.
   */
  @Override
  public void cleanup() {
    super.cleanup();
    MimeType mime = getMimeType();  
    mimeString2ClassHandlerMap.remove(mime.getType() + "/" + mime.getSubtype());
    mimeString2mimeTypeMap.remove(mime.getType() + "/" + mime.getSubtype());  
    suffixes2mimeTypeMap.remove("bdocjson.gz");
  }
  
  /**
   * Method to read a file with this format.
   * @param dcmnt the document, we need the sourceURL from this.
   * @throws DocumentFormatException if error
   */
  @Override
  public void unpackMarkup(Document dcmnt) throws DocumentFormatException {
    URL sourceURL = dcmnt.getSourceUrl();
    if(sourceURL == null) {
      throw new DocumentFormatException("Cannot create document, no sourceURL");
    }
    String json;
    try (
            InputStream urlStream = sourceURL.openStream();
            InputStreamReader isr =
                    new InputStreamReader(new GZIPInputStream(urlStream));
            BufferedReader br = new BufferedReader(isr);
            ) {
      StringBuilder sb = new StringBuilder();
      String line;
      while(null != (line = br.readLine())) {
        sb.append(line);
      }
      json = sb.toString();
    } catch (IOException ex) { 
      throw new DocumentFormatException("Exception when trying to read the document "+sourceURL,ex);
    }
    SimpleJson sj = new SimpleJson();
    BdocDocument bdoc = sj.loads_doc(json);
    DocumentContent newContent = new DocumentContentImpl(bdoc.text);
    try {
      dcmnt.edit(0L, dcmnt.getContent().size(), newContent);
    } catch (InvalidOffsetException ex) {
      throw new DocumentFormatException("Could not set document content", ex);
    }
    GateDocumentUpdater gdu = new GateDocumentUpdater(dcmnt);
    gdu.handleNewAnnotation(GateDocumentUpdater.HandleNewAnns.ADD_WITH_BDOC_ID);
    gdu.fromBdoc(bdoc);
  }

  /**
   * Method for unpacking with repo info: not supported.
   * @param dcmnt document
   * @param ri repo info 
   * @param ri1 repo info
   * @throws DocumentFormatException  exception
   */
  @Override
  public void unpackMarkup(Document dcmnt, RepositioningInfo ri, RepositioningInfo ri1) throws DocumentFormatException {
    throw new UnsupportedOperationException("Should not be needed"); 
  }
  
}

