/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 * Export document in BdocJson Format.
 * 
 * @author Johann Petrak
 */
@CreoleResource(
        name = "Bdoc/JSON Exporter", 
        tool = true, 
        autoinstances = @AutoInstance, 
        comment = "Export GATE documents in Bdoc/Json format.", 
        helpURL = ""
)
public class ExporterBdocJson extends DocumentExporter {

  private static final long serialVersionUID = 7769437689112346068L;

  /**
   * Constructor.
   */
  public ExporterBdocJson() {
    super("Bdoc/Json", "bdocjson", "text/bdocjson");
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
    sj.dump(bdoc, out);
  }
  
}
