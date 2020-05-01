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
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;

/**
 * Read a document in gzipped "bdoc" format.
 * 
 * This will try to load a document in bdoc format, without known what the 
 * actual serialization that is used is. It will try to figure this out by 
 * reading part or all of the URL and then delegating to the proper 
 * serialization specific class. 
 * 
 * @author Johann Petrak
 */
@CreoleResource(
        name = "GATE Bdoc Gzipped Format", 
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = true)},
        comment = "Format BdocGzip",
        helpURL = "https://github.com/GateNLP/gateplugin-Format_Bdoc"
)
public class FormatBdocGzip 
        extends BaseFormatBdoc
{
  
  public FormatBdocGzip() {
    mimeType = "text";
    mimeSubtype = "bdoc+gzip";
    suffix = "bdoc.gz";
  }
  private static final long serialVersionUID = 284548435L;
    
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
    // figure out which format we really have
    boolean isJson;
    try (
            InputStream urlStream = sourceUrl.openStream();
            GZIPInputStream is = new GZIPInputStream(urlStream);
        ) {
      byte buf[] = new byte[8];
      int n = is.read(buf);
      if(n<2) {
        throw new GateRuntimeException("Could not read bdoc from URL "+sourceUrl+" not enough data");
      }
      // TODO: for now, a very simple heuristic: if it starts with "{" must be
      // JSON, otherwise msgpack. should maybe check the msgpack bytes 
      // For JSON should maybe ignore byte order encoding marker 
      isJson = (buf[0] == 123);  // 123 is ASCII code of "{"
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not read MsgPack data from URL "+sourceUrl, ex);
    }    
    // open the URL again and call the proper loading function
    BdocDocument bdoc;
    try (InputStream is = sourceUrl.openStream()) {
      if(isJson) {
        bdoc = new Loader().from(is).format(Format.JSON_MAP).gzipped(true).load_bdoc();
      } else {
        bdoc = new Loader().from(is).format(Format.MSGPACK).gzipped(true).load_bdoc();
      }
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not read Bdoc from URL "+sourceUrl, ex);
    } 
    updateDocument(dcmnt, bdoc);
  }
}
