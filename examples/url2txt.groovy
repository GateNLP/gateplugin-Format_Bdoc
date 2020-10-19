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

import java.io.*;


Gate.init()

mimetype = null
if(args.size() == 2) {
  infile = args[0]
  outfile = args[1]
} else if (args.size() == 3) {
  infile = args[0]
  outfile = args[1]
  mimetype = args[2]
} else {
  System.err.println("Need two arguments: in_url_or_file outfile")
  System.exit(1)
}

try {
  infileUrl = new URL(infile)
} catch(Exception ex) {
  infileUrl = new File(infile).toURI().toURL()
}

System.err.println("Trying to convert from "+infileUrl+" to "+outfile);

Gate.getCreoleRegister().registerPlugin(
  new Plugin.Maven("uk.ac.gate.plugins", "format-fastinfoset", "8.6"));

parms = Factory.newFeatureMap();
parms.put("sourceUrl", infileUrl)
if(mimetype != null) {
  parms.put("mimeType", mimetype)
}
System.err.println("Loading document ...");
doc = Factory.createResource("gate.corpora.DocumentImpl", parms);
System.err.println("Document size: "+doc.getContent().size())
text = doc.getContent().toString();
System.err.println("Writing document ...");
try {
  OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8") 
  writer.write(text)
  writer.close();
} catch(Exception ex) {
  throw new GateRuntimeException("Problem writing the file", ex);
}





