package gate.plugin.format.bdoc;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import gate.*;
import gate.DocumentContent;
import gate.GateConstants;
import gate.Resource;
import gate.corpora.TextualDocumentFormat;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import gate.util.InvalidOffsetException;
import gate.util.Out;

@CreoleResource(name = "GATE Bdoc-Json Format", isPrivate = true,
    autoinstances = {@AutoInstance(hidden = true)},
    comment = "Format parser for Bdoc JSON Files",
    helpURL = "")

public class FormatBdoc extends TextualDocumentFormat {
  private static final long serialVersionUID = 687802003643563918L;
}

