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
 * Read a document in "bdoc json" format.
 * 
 * This will try to load a document in bdoc format, without known what the 
 * actual serialization that is used is. It will try to figure this out by 
 * reading part or all of the URL and then delegating to the proper 
 * serialization specific class. 
 * 
 * @author Johann Petrak
 */
@CreoleResource(
        name = "GATE BdocJson Format", 
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = true)},
        comment = "Format BdocJson",
        helpURL = "https://github.com/GateNLP/gateplugin-Format_Bdoc"
)
public class FormatBdocJson 
        extends BaseFormatBdoc
{
  public FormatBdocJson() {
    mimeType = "text";
    mimeSubtype = "bdocjs";
    suffix = "bdocjs";
  }
   
  private static final long serialVersionUID = 28234743535L;
  
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
    System.err.println("Unpacking using "+this.getClass());    
    URL sourceUrl = dcmnt.getSourceUrl();
    if(sourceUrl == null) {
      throw new GateRuntimeException("Source URL is null");
    }
    BdocDocument bdoc;
    try (InputStream is = sourceUrl.openStream()) {
      bdoc = new Loader().from(is).format(Format.JSON_MAP).load_bdoc();
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not read Bdoc from URL "+sourceUrl, ex);
    } 
    updateDocument(dcmnt, bdoc);
  }

}
