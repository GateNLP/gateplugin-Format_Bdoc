@GrabResolver(name='gate-snapshots', root='http://repo.gate.ac.uk/content/groups/public/')
@Grab('uk.ac.gate:gate-core:8.6') 
import gate.*
import gate.creole.*
import java.io.File

Gate.init()

if(args.size() != 2) {
  System.err.println("Need two arguments: in_url_or_file outfile")
  System.exit(1)
}

infile = args[0]
outfile = args[1]

System.out.println("Trying to convert from "+infile+" to "+outfile);

Gate.getCreoleRegister().registerPlugin(
  new Plugin.Maven("uk.ac.gate.plugins", "format-fastinfoset", "8.5-SNAPSHOT"));
Gate.getCreoleRegister().registerPlugin(
  new Plugin.Maven("uk.ac.gate.plugins", "format-bdoc", "1.1-SNAPSHOT"));

parms = Factory.newFeatureMap();
parms.put("sourceURL", infile)
doc = Factory.createResource("gate.corpora.DocumentImpl", parms);





