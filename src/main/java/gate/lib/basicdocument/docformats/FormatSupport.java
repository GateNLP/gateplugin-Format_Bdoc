
package gate.lib.basicdocument.docformats;

import gate.lib.basicdocument.BdocDocument;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface of all FormatSupport classes.
 * 
 * Format Support classes do the actual heavy lifting of converting to and
 * from a specific serialization format in a specific way. 
 * For each main format (JSON, MsgPack) there is a base class which can be 
 * used to automatically delegate to the proper subclasses: for saving the 
 * base class will delegate to the most recent or preferred format, for loading
 * the base class will parse the input to detect which format it is in and 
 * pass controll to the appropriate subclass for the format. 
 * 
 * @author Johann Petrak
 */
public interface FormatSupport {
  public void save(BdocDocument bdoc, OutputStream os);
  public BdocDocument load_bdoc(InputStream is);
}
