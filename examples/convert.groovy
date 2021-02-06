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

if(args.size() != 3) {
  System.err.println("Need three arguments: in_url_or_file outfile fmt")
  System.err.println("  fmt one of: xml, finf, bdocjs, bdocjsgz, bdocym, bdocymgz, bdocmp")
  System.exit(1)
}

infile = args[0]
outfile = args[1]
fmt = args[2]

infileUrl = null
try {
  infileUrl = new URL(infile)
} catch(Exception ex) {
  infileUrl = new File(infile).toURI().toURL()
}

System.err.println("Trying to convert from "+infileUrl+" to "+outfile);

Gate.getCreoleRegister().registerPlugin(
            new Plugin.Maven("uk.ac.gate.plugins", "format-fastinfoset", "8.5"));
Gate.getCreoleRegister().registerPlugin(
            new Plugin.Maven("uk.ac.gate.plugins", "format-bdoc", "1.10"));
cr = Gate.getCreoleRegister()

fmt2exp = [:]
fmt2exp["xml"] = null
fmt2exp["finf"] = cr.get("gate.corpora.FastInfosetExporter").getInstantiations().iterator().next()
fmt2exp["bdocjs"] = cr.get("gate.plugin.format.bdoc.ExporterBdocJson").getInstantiations().iterator().next()
fmt2exp["bdocjsgz"] = cr.get("gate.plugin.format.bdoc.ExporterBdocJsonGzip").getInstantiations().iterator().next()
fmt2exp["bdocym"] = cr.get("gate.plugin.format.bdoc.ExporterBdocYaml").getInstantiations().iterator().next()
fmt2exp["bdocymgz"] = cr.get("gate.plugin.format.bdoc.ExporterBdocYamlGzip").getInstantiations().iterator().next()
fmt2exp["bdocmp"] = cr.get("gate.plugin.format.bdoc.ExporterBdocMsgPack").getInstantiations().iterator().next()
exp = fmt2exp[fmt]
println("Using exporter: "+exp)

parms = Factory.newFeatureMap();
parms.put("sourceUrl", infileUrl)
System.err.println("Loading document ...");
doc = Factory.createResource("gate.corpora.DocumentImpl", parms);

System.err.println("Converting document ...");

System.err.println("Writing document ...");
try {
  if(fmt=="xml") {
    gate.corpora.DocumentStaxUtils.writeDocument(doc, new File(outfile))
  } else if (fmt=="txt") {
    text = doc.getContent().toString()
    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8") 
    writer.write(text)
    writer.close();  
  } else {
    exp.export(doc, new File(outfile), Factory.newFeatureMap())
  }
} catch(Exception ex) {
  throw new GateRuntimeException("Problem writing the file", ex);
}





