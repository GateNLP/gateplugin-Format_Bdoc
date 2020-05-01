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

import gate.DirectLoadingDocumentFormat;
import gate.Document;
import gate.DocumentContent;
import gate.DocumentFormat;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.corpora.MimeType;
import gate.corpora.RepositioningInfo;
import gate.creole.ResourceInstantiationException;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.GateDocumentUpdater;
import gate.util.DocumentFormatException;

/**
 * Base class for Bdoc format classes.
 *
 * Common code for all classes.
 *
 * @author Johann Petrak
 */
public abstract class BaseFormatBdoc
        extends DocumentFormat
        implements DirectLoadingDocumentFormat {

  private static final long serialVersionUID = 776942555558L;
  /**
   * Does not support Repositioning.
   *
   * @return false
   */
  @Override
  public Boolean supportsRepositioning() {
    return false;
  }

  protected String mimeType;
  protected String mimeSubtype;
  protected String suffix;
  protected String suffix2 = null;
  
  
  /**
   * Register support for this format.
   *
   * @return the resource instance
   * @throws ResourceInstantiationException if an error occurs
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    System.err.println("DEBUG: init with "+mimeType+"/"+mimeSubtype+" and ext="+suffix);
    MimeType mime = new MimeType(mimeType, mimeSubtype);
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(), this);
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    suffixes2mimeTypeMap.put(suffix, mime);
    if(suffix2 != null) {
      suffixes2mimeTypeMap.put(suffix2, mime);
    }
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
    suffixes2mimeTypeMap.remove(suffix);
    if(suffix2 != null) {
      suffixes2mimeTypeMap.remove(suffix2);
    }
  }
  

  /**
   * Update the given document from the loaded BdocDocument.
   * 
   * @param dcmnt document to update
   * @param bdoc  bdoc document
   */
  public void updateDocument(Document dcmnt, BdocDocument bdoc) {
    DocumentContent newContent = new DocumentContentImpl(bdoc.text);
    dcmnt.setContent(newContent);
    GateDocumentUpdater gdu = new GateDocumentUpdater(dcmnt);
    gdu.handleNewAnnotation(GateDocumentUpdater.HandleNewAnns.ADD_WITH_BDOC_ID);
    gdu.fromBdoc(bdoc);
  }

  /**
   * Method for unpacking with repo info: not supported.
   *
   * @param dcmnt document
   * @param ri repo info
   * @param ri1 repo info
   * @throws DocumentFormatException exception
   */
  @Override
  public void unpackMarkup(Document dcmnt, RepositioningInfo ri, RepositioningInfo ri1) throws DocumentFormatException {
    throw new UnsupportedOperationException("This format does not support repositioning info");
  }
}
