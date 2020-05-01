/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.basicdocument.docformats;

import gate.lib.basicdocument.BdocDocument;
import gate.util.GateRuntimeException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author johann
 */
public class Loader {
  protected Format format = Format.JSON_MAP;
  protected boolean haveSrc = false;
  protected boolean haveFormat = false;
  protected boolean gzipped = false;
  protected File file = null;
  protected URL url = null;
  protected InputStream is = null; 
  
  private void checkNoSrcAndSet() {
    if(haveSrc) {
      throw new GateRuntimeException("Only one source / from() method may be used");
    }
    haveSrc = true;
  }
  private void checkNoFormatAndSet() {
    if(haveFormat) {
      throw new GateRuntimeException("Only one format() method may be used");
    }
    haveFormat = true;
  }
  private void checkHaveNeeded() {
    if(!haveSrc) {
      throw new GateRuntimeException("No source specified with from()");
    }
    if(!haveFormat) {
      throw new GateRuntimeException("No format specified with format()");
    }
  }
  
  public Loader from(String path) {
    checkNoSrcAndSet();
    file = new File(path);
    return this;
  }
  public Loader from(File file) {
    checkNoSrcAndSet();
    this.file = file;
    return this;
  }
  public Loader from(InputStream is) {
    checkNoSrcAndSet();
    this.is = is;
    return this;
  }
  public Loader from(URL url) {
    checkNoSrcAndSet();
    this.url = url;
    return this;
  }
  public Loader format(Format fmt) {
    checkNoFormatAndSet();
    format = fmt;
    return this;
  }
  public Loader gzipped(boolean flag) {
    gzipped = flag;
    return this;
  }
  public BdocDocument load_bdoc() {
    checkHaveNeeded();
    try {
      if(file != null) {
        is = new BufferedInputStream(new FileInputStream(file));
      } else if(url != null) {
        is = url.openStream();
      }
      if(gzipped) {
        is = new GZIPInputStream(is);
      }
      switch (format) {
        case JSON_MAP:
          return new JsonFormatSupportMap().load_bdoc(is);
        case JSON_ARR:
          return new JsonFormatSupportArr().load_bdoc(is);
        default:
          return new MsgPackFormatSupport().load_bdoc(is);
      }
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not write", ex);
    } finally {
      if(is != null) {
        try {
          is.close();
        } catch(IOException ex) {
          // ignore
        }
      }
    }
    
  }
}
