
package gate.lib.basicdocument.docformats;

import com.fasterxml.jackson.databind.ObjectMapper;
import gate.lib.basicdocument.BdocDocument;
import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * How to serialize/deserialize JSON Map format.
 * 
 * @author Johann Petrak
 */
public class JsonFormatSupportMap implements FormatSupport {

  @Override
  public void save(BdocDocument bdoc, OutputStream os) {
    ObjectMapper om = new ObjectMapper();
    try {
      om.writeValue(os, bdoc);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not convert Bdoc to JSON", ex);
    }
  }

  @Override
  public BdocDocument load_bdoc(InputStream is) {
    ObjectMapper om = new ObjectMapper();
    BdocDocument bdoc;
    try {
      bdoc = om.readValue(is, BdocDocument.class);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not JSON to Bdoc", ex);
    }
    return bdoc;
  }


}
