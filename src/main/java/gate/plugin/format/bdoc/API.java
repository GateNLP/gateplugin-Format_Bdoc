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
import gate.DocumentContent;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.NameBearerHandle;
import gate.gui.ResourceHelper;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.GateDocumentUpdater;
import gate.lib.basicdocument.docformats.Loader;
import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;

/**
 * A resource helper to invoke the API.
 * 
 * 
 * @author Johann Petrak
 */

// TODO: eventually we may want to make this hidden!
@CreoleResource(name = "Format Bdoc API Executor", tool=true, autoinstances = @AutoInstance)
public class API extends ResourceHelper {
  
  private static final long serialVersionUID = 776874654360368L;
  
  @Override
  @SuppressWarnings("unchecked") 
  public Object call(String action, Resource resource, Object... params) {
    BdocDocument bdoc;
    Document gdoc;
    switch(action) {
      case "bdoc_from_string":
        String json = (String)params[0];
        bdoc = new Loader().from(json).load_bdoc();
        return bdoc;
      case "update_document":
        gdoc = (Document)resource;
        bdoc = (BdocDocument)params[0];
        DocumentContent newContent = new DocumentContentImpl(bdoc.text);
        gdoc.setContent(newContent);
        GateDocumentUpdater gdu = new GateDocumentUpdater(gdoc);
        gdu.handleNewAnnotation(GateDocumentUpdater.HandleNewAnns.ADD_WITH_BDOC_ID);
        gdu.fromBdoc(bdoc);
        return gdoc;
      default:
        throw new GateRuntimeException("Not a known action: "+action);
    }
  }

  @Override
  protected List<Action> buildActions(NameBearerHandle handle) {
    return new ArrayList<>();
  }
}
