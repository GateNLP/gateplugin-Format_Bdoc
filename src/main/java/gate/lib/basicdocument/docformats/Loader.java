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

package gate.lib.basicdocument.docformats;

import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.ChangeLog;
import gate.util.GateRuntimeException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
  protected String fromString = null;
  
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
  public Loader fromString(String json) {
    checkNoSrcAndSet();
    fromString = json;
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
      if(fromString != null) {
        if(format == Format.MSGPACK) {
          throw new GateRuntimeException("Cannot use MsgPack with String source");
        }
        is = new ByteArrayInputStream(fromString.getBytes("utf-8"));
      } else if(file != null) {
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
        case YAML:
          return new YamlFormatSupportMap().load_bdoc(is);
        default:
          return new MsgPackFormatSupport().load_bdoc(is);
      }
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not load", ex);
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
  
    public ChangeLog load_log() {
    checkHaveNeeded();
    try {
      if(fromString != null) {
        if(format == Format.MSGPACK) {
          throw new GateRuntimeException("Cannot use MsgPack with String source");
        }
        is = new ByteArrayInputStream(fromString.getBytes("utf-8"));
      } else if(file != null) {
        is = new BufferedInputStream(new FileInputStream(file));
      } else if(url != null) {
        is = url.openStream();
      }
      if(gzipped) {
        is = new GZIPInputStream(is);
      }
      switch (format) {
        case JSON_MAP:
          return new JsonFormatSupportMap().load_log(is);
        case YAML:
          return new YamlFormatSupportMap().load_log(is);
        default:
          return new MsgPackFormatSupport().load_log(is);
      }
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not load", ex);
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
