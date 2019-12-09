package gate.plugin.format.bdoc;

import gate.*;
import gate.Resource;
import gate.corpora.MimeType;
import gate.corpora.RepositioningInfo;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.GateDocumentUpdater;
import gate.lib.basicdocument.docformats.SimpleJson;
import gate.util.DocumentFormatException;
import java.io.InputStream;
import java.net.URL;
import org.apache.log4j.Logger;

@CreoleResource(
        name = "GATE Bdoc-Json Format", 
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = true)},
        comment = "Support for JSON-serialised GATE basic document",
        helpURL = ""
)
public class FormatBdocJson extends DocumentFormat {
  private static final long serialVersionUID = 687802003643563918L;
  
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
    MimeType mime = new MimeType("text", "bdocjson");
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),this);
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    suffixes2mimeTypeMap.put("bdocjson", mime);
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
    suffixes2mimeTypeMap.remove("bdocjson");
  }
  
  /**
   * Method to read a file with this format.
   * @param dcmnt the document, we need the sourceURL from this.
   * @throws DocumentFormatException if error
   */
  @Override
  public void unpackMarkup(Document dcmnt) throws DocumentFormatException {
    logger.info("Got this document: "+dcmnt);
    URL sourceURL = dcmnt.getSourceUrl();
    if(sourceURL == null) {
      throw new DocumentFormatException("Cannot create document, no sourceURL");
    }
    SimpleJson sj = new SimpleJson();
    try (
            InputStream urlStream = sourceURL.openStream();
            ) {
      BdocDocument bdoc = sj.load_doc(urlStream);
      GateDocumentUpdater gdu = new GateDocumentUpdater(dcmnt);
      gdu.fromBdoc(bdoc);
    } catch (Exception ex) {
      throw new DocumentFormatException("Exception when trying to read the document "+sourceURL,ex);
    }        
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

