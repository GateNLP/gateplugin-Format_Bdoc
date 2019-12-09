package gate.plugin.format.bdoc;

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
import org.apache.log4j.Logger;

@CreoleResource(
        name = "GATE Bdoc-Json Format", 
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = false)},
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
    URL sourceURL = dcmnt.getSourceUrl();
    if(sourceURL == null) {
      throw new DocumentFormatException("Cannot create document, no sourceURL");
    }
    SimpleJson sj = new SimpleJson();
    String json = dcmnt.getContent().toString();
    BdocDocument bdoc = sj.loads_doc(json);
    DocumentContent newContent = new DocumentContentImpl(bdoc.text);
    try {
      dcmnt.edit(0L, dcmnt.getContent().size(), newContent);
    } catch (InvalidOffsetException ex) {
      throw new DocumentFormatException("Could not set document content", ex);
    }
    GateDocumentUpdater gdu = new GateDocumentUpdater(dcmnt);
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

