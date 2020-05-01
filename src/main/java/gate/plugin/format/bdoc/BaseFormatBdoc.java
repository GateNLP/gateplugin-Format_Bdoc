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