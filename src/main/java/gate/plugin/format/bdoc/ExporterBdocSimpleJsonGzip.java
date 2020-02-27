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
package gate.plugin.format.bdoc;

import gate.Document;
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import java.io.IOException;
import java.io.OutputStream;
import gate.lib.basicdocument.docformats.SimpleJson;
import java.util.zip.GZIPOutputStream;

/**
 * Export document in Gzip-compressed Bdoc Simple Json Format.
 * 
 * @author Johann Petrak
 */
@CreoleResource(
        name = "Bdoc/SimpleJson Gzipped Exporter", 
        tool = true, 
        autoinstances = @AutoInstance, 
        comment = "Export GATE documents in Gzipped Bdoc/SimpleJson format.", 
        helpURL = "https://github.com/GateNLP/gateplugin-Format_Bdoc"
)
public class ExporterBdocSimpleJsonGzip extends DocumentExporter {

  private static final long serialVersionUID = 7769438945112346068L;

  /**
   * Constructor.
   */
  public ExporterBdocSimpleJsonGzip() {
    super("Bdoc/SimpleJson+Gzip", "bdocsjson.gz", "text/bdocsjson+gzip");
  }

  /**
   * Export the document.
   * @param dcmnt document
   * @param out output stream
   * @param fm features
   * @throws IOException  if error
   */
  @Override
  public void export(Document dcmnt, OutputStream out, FeatureMap fm) throws IOException {
    SimpleJson sj = new SimpleJson();
    BdocDocumentBuilder builder = new BdocDocumentBuilder();
    builder.fromGate(dcmnt);
    BdocDocument bdoc = builder.buildBdoc();
    try ( GZIPOutputStream gos = new GZIPOutputStream(out)) {
      sj.dump(bdoc, gos);
    }
    
  }
  
}
