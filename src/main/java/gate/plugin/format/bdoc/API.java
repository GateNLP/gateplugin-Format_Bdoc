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
import gate.FeatureMap;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.NameBearerHandle;
import gate.gui.ResourceHelper;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import gate.lib.basicdocument.BdocUtils;
import gate.lib.basicdocument.ChangeLog;
import gate.lib.basicdocument.GateDocumentUpdater;
import gate.lib.basicdocument.docformats.Format;
import gate.lib.basicdocument.docformats.Loader;
import gate.util.GateRuntimeException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  
  private Document update_document_from_bdoc(Document gdoc, BdocDocument bdoc) {
    DocumentContent newContent = new DocumentContentImpl(bdoc.text);
    gdoc.setContent(newContent);
    GateDocumentUpdater gdu = new GateDocumentUpdater(gdoc);
    gdu.handleNewAnnotation(GateDocumentUpdater.HandleNewAnns.ADD_WITH_BDOC_ID);
    gdu.fromBdoc(bdoc);    
    return gdoc;
  }
  
  private Document update_document_from_log(Document gdoc, ChangeLog log) {
    GateDocumentUpdater gdu = new GateDocumentUpdater(gdoc);
    gdu.handleNewAnnotation(GateDocumentUpdater.HandleNewAnns.ADD_WITH_BDOC_ID);
    gdu.fromChangeLog(log);    
    return gdoc;
  }
  
  @Override
  @SuppressWarnings("unchecked") 
  public Object call(String action, Resource resource, Object... params) {
    BdocDocument bdoc;
    BdocDocumentBuilder builder;
    Document gdoc;
    ChangeLog log;
    String json;
    Map<String,Object> map;
    switch(action) {
      case "json_from_doc":
        gdoc = (Document)resource;
        json = new BdocDocumentBuilder().fromGate(gdoc).toJsonString();
        return json;
      case "fmap_to_map":
        FeatureMap fm = (FeatureMap)params[0];
        return BdocUtils.featureMap2Map(fm, null);
      case "bdoc_from_string":
        json = (String)params[0];
        bdoc = new Loader().format(Format.JSON_MAP).fromString(json).load_bdoc();
        return bdoc;
      case "doc_from_json":
        json = (String)params[0];
        bdoc = new Loader().format(Format.JSON_MAP).fromString(json).load_bdoc();
        Document tmpdoc = null;
        try {
          tmpdoc = gate.Factory.newDocument(bdoc.text);
        } catch (ResourceInstantiationException ex) {
          throw new GateRuntimeException("Could not create GATE document from JSON", ex);
        }
        tmpdoc = new GateDocumentUpdater(tmpdoc).fromBdoc(bdoc);
        return tmpdoc;
      case "bdoc_from_doc":
        gdoc = (Document)resource;
        bdoc = new BdocDocumentBuilder().fromGate(gdoc).buildBdoc();
        return bdoc;
      case "bdocmap_from_doc":
        // if the first params parameter is present then if it is null, all sets are added, otherwise
        // a collection of names to include is expected. If the second params is present it is expected to
        // be a boolean indicating if placeholder sets should get added in case not all sets are included.
        // Placeholder sets are empty sets with the names of those not included, but containing a
        // next annotation id. If the 2nd parameter is missing, the default is to not include
        // placeholder sets.
        gdoc = (Document)resource;
        builder = new BdocDocumentBuilder().fromGate(gdoc);
        if(params.length > 0 && params[0] != null) {
          boolean includePlaceholderSets = false;
          if(params.length > 1) includePlaceholderSets = (Boolean)params[1];
          builder.setAnnotationSetNames((Collection<String>)params[0]);
          builder.setIncludePlaceholderSets(includePlaceholderSets);
        }
        bdoc = builder.buildBdoc();
        return bdoc.toMap();
      case "log_from_string":
        json = (String)params[0];
        log = new Loader().format(Format.JSON_MAP).fromString(json).load_log();
        return log;      
      case "log_from_map":
        map = (Map<String,Object>)params[0];
        log = ChangeLog.fromMap(map);
        return log;      
      case "update_document_from_bdoc":        
        gdoc = (Document)resource;
        bdoc = (BdocDocument)params[0];
        return update_document_from_bdoc(gdoc, bdoc);
      case "update_document_from_bdocjson":        
        gdoc = (Document)resource;
        json = (String)params[0];
        bdoc = new Loader().format(Format.JSON_MAP).fromString(json).load_bdoc();
        return update_document_from_bdoc(gdoc, bdoc);
      case "update_document_from_log":        
        gdoc = (Document)resource;
        log = (ChangeLog)params[0];
        return update_document_from_log(gdoc, log);
      case "update_document_from_logmap":        
        gdoc = (Document)resource;
        map = (Map<String,Object>)params[0];
        log = ChangeLog.fromMap(map);
        return update_document_from_log(gdoc, log);
      case "update_document_from_logjson":        
        gdoc = (Document)resource;
        json = (String)params[0];
        log = new Loader().format(Format.JSON_MAP).fromString(json).load_log();
        return update_document_from_log(gdoc, log);
      default:
        throw new GateRuntimeException("Not a known action: "+action);
    }
  }

  @Override
  protected List<Action> buildActions(NameBearerHandle handle) {
    return new ArrayList<>();
  }
}
