@GrabResolver(name='gate-snapshots', root='http://repo.gate.ac.uk/content/groups/public/')
@Grab(value='org.codehaus.plexus:plexus-container-default:1.0-alpha-9-stable-1', transitive=false)

@Grab('uk.ac.gate:gate-core:9.0-SNAPSHOT') 
import gate.*
import gate.creole.*
import gate.util.*

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

text = doc.getContent().toString();
System.err.println("Writing document ...");
try {
  OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8") 
  writer.write(text)
  writer.close();
} catch(Exception ex) {
  throw new GateRuntimeException("Problem writing the file", ex);
}





