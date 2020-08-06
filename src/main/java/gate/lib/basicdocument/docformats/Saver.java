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
import gate.util.GateRuntimeException;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author johann
 */
public class Saver {
  
  protected boolean gzipped = false;
  protected Format format = Format.JSON_MAP;

  protected boolean haveDest = false;
  protected boolean haveFormat = false;
  protected boolean toString = false;
  protected File file = null;
  protected OutputStream os = null; 
  
  private void checkNoDestAndSet() {
    if(haveDest) {
      throw new GateRuntimeException("Only one destination / to() method may be used");
    }
    haveDest = true;
  }
  private void checkNoFormatAndSet() {
    if(haveFormat) {
      throw new GateRuntimeException("Only one format() method may be used");
    }
    haveFormat = true;
  }
  private void checkHaveNeeded() {
    if(!haveDest) {
      throw new GateRuntimeException("No destination specified with to()");
    }
    if(!haveFormat) {
      throw new GateRuntimeException("No format specified with format()");
    }
  }
  public Saver to(String path) {
    checkNoDestAndSet();
    file = new File(path);
    return this;
  }
  public Saver to(File file) {
    checkNoDestAndSet();
    this.file = file;
    return this;
  }
  public Saver to(OutputStream os) {
    checkNoDestAndSet();
    this.os = os;
    return this;
  }
  public Saver asString() {
    checkNoDestAndSet();
    this.toString = true;
    return this;
  }
  public Saver format(Format fmt) {
    checkNoFormatAndSet();
    format = fmt;
    return this;
  }
  public Saver gzipped(boolean flag) {
    gzipped = flag;
    return this;
  }
  public String save(BdocDocument bdoc) {
    checkHaveNeeded();
    try {
      String ret = null;
      if(toString) {
        if(format == Format.MSGPACK) {
          throw new GateRuntimeException("Format MsgPack cannot be converted to String");
        }
        os = new ByteArrayOutputStream();
      } else if(file != null) {
        os = new BufferedOutputStream(new FileOutputStream(file));
      } // else: we already have the output stream
      if(gzipped) {
        os = new GZIPOutputStream(os);
      }
      switch (format) {
        case JSON_MAP:
          new JsonFormatSupportMap().save(bdoc, os);
          break;
        case YAML:
          new YamlFormatSupportMap().save(bdoc, os);
          break;
        default:
          new MsgPackFormatSupport().save(bdoc, os);
          break;
      }
      if(toString) {
        ret = ((ByteArrayOutputStream)os).toString("utf-8");
      }
      return ret;
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not write", ex);
    } finally {
      if(os != null) {
        try {
          os.close();
        } catch(IOException ex) {
          // ignore
        }
      }
    }
  }
}
