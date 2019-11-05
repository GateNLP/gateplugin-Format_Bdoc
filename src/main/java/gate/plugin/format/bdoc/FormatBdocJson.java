package gate.plugin.format.bdoc;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import gate.*;
import gate.DocumentContent;
import gate.GateConstants;
import gate.Resource;
import gate.corpora.MimeType;
import gate.corpora.RepositioningInfo;
import gate.corpora.TextualDocumentFormat;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.GateDocumentUpdater;
import gate.lib.basicdocument.docformats.SimpleJson;
import gate.util.DocumentFormatException;
import gate.util.InvalidOffsetException;
import gate.util.Out;
import java.io.InputStream;
import java.net.URL;
import org.apache.http.impl.cookie.BasicDomainHandler;

@CreoleResource(name = "GATE Bdoc-Json Format", isPrivate = true,
    autoinstances = {@AutoInstance(hidden = true)},
    comment = "Support for JSON-serialised GATE basic document",
    helpURL = "")

public class FormatBdocJson extends DocumentFormat {
  private static final long serialVersionUID = 687802003643563918L;
  
  @Override
  public Boolean supportsRepositioning() {
    return false;
  } 

@Override
  public Resource init() throws ResourceInstantiationException {
    MimeType mime = new MimeType("text", "bdocjson");
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),this);
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    suffixes2mimeTypeMap.put("bdocjson", mime);
    setMimeType(mime);
    return this;
  }
  @Override
  public void unpackMarkup(Document dcmnt) throws DocumentFormatException {
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

  @Override
  public void unpackMarkup(Document dcmnt, RepositioningInfo ri, RepositioningInfo ri1) throws DocumentFormatException {
    throw new UnsupportedOperationException("Should not be needed"); 
  }
  
}

