@GrabResolver(name='gate-snapshots', root='http://repo.gate.ac.uk/content/groups/public/')
@Grab(value='org.codehaus.plexus:plexus-container-default:1.0-alpha-9-stable-1', transitive=false)
@Grab('ch.qos.logback:logback-classic:1.2.3')
// just downloading the grape may get an error but after doing 
// mvn dependency:get -Dartifact=org.apache.tika:tika-parsers:1.23
// it should work !?!?!
@Grab(value='org.apache.tika:tika-parsers:1.23', transitive=false)
@Grab('uk.ac.gate:gate-core:9.0-SNAPSHOT') 
import gate.*
import gate.creole.*
import gate.util.*
@Grab('uk.ac.gate.plugins:format-bdoc:1.0-SNAPSHOT')
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import gate.lib.basicdocument.docformats.SimpleJson;

import java.io.*;


Gate.init()

if(args.size() != 2) {
  System.err.println("Need two arguments: in_url_or_file outfile")
  System.exit(1)
}

infile = args[0]
outfile = args[1]

infileUrl = null
try {
  infileUrl = new URL(infile)
} catch(Exception ex) {
  infileUrl = new File(infile).toURI().toURL()
}

System.err.println("Trying to convert from "+infileUrl+" to "+outfile);

Gate.getCreoleRegister().registerPlugin(
  new Plugin.Maven("uk.ac.gate.plugins", "format-fastinfoset", "8.5-SNAPSHOT"));

parms = Factory.newFeatureMap();
parms.put("sourceUrl", infileUrl)
System.err.println("Loading document ...");
doc = Factory.createResource("gate.corpora.DocumentImpl", parms);

System.err.println("Converting document ...");
SimpleJson sj = new SimpleJson();
BdocDocumentBuilder builder = new BdocDocumentBuilder();
builder.fromGate(doc);
BdocDocument bdoc = builder.buildBdoc();
System.err.println("Writing document ...");
try {
  OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8") 
  sj.dump(bdoc, writer);
  writer.close();
} catch(Exception ex) {
  throw new GateRuntimeException("Problem writing the file", ex);
}





