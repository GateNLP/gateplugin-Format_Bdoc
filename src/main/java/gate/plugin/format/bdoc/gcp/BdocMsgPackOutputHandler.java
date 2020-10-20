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

package gate.plugin.format.bdoc.gcp;

import gate.Document;
import gate.cloud.batch.DocumentID;
import gate.cloud.io.file.AbstractFileOutputHandler;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import gate.lib.basicdocument.docformats.MsgPackFormatSupport;
import gate.util.GateException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static gate.cloud.io.IOConstants.PARAM_FILE_EXTENSION;

/**
 * Output handler for the Bdoc MsgPack format.
 * 
 * @author Johann Petrak
 */
public class BdocMsgPackOutputHandler extends AbstractFileOutputHandler {
  
  /**
   * Make sure the extension is set.
   *
   * @param configData config data 
   * @throws IOException if there is an IO error
   * @throws GateException  if there is another error
   */
  @Override
  protected void configImpl(Map<String, String> configData) 
          throws IOException, GateException 
  {
    if(!configData.containsKey(PARAM_FILE_EXTENSION)) {
      configData.put(PARAM_FILE_EXTENSION, ".bdocmp");
    }
    super.configImpl(configData);
  }
  
  /**
   * How to output as Bdoc MsgPack document.
   *
   * @param dcmnt document
   * @param did document id
   * @throws IOException error
   * @throws GateException  error
   */
  @Override
  protected void outputDocumentImpl(Document dcmnt, DocumentID did) 
          throws IOException, GateException 
  {
    BdocDocumentBuilder builder = new BdocDocumentBuilder();
    builder.fromGate(dcmnt);
    BdocDocument bdoc = builder.buildBdoc();
    try ( OutputStream os = getFileOutputStream(did);) {
      new MsgPackFormatSupport().save(bdoc, os);
    }    
  }
  
}
