// @GrabResolver(name='gate-snapshots', root='http://repo.gate.ac.uk/content/groups/public/')
// the following on a line by itself will not work with groovysh and an error will be shown
// for the PersitenceManager import
// @Grab('uk.ac.gate:gate-core:8.5.1')
// The following works with groovysh but not with groovy
// groovy.grape.Grape.grab(group:'uk.ac.gate', module:'gate-core', version:'8.5.1')
//import gate.*;
//import gate.creole.*;
//import gate.util.persistence.PersistenceManager;
//import java.io.File

// See also: http://docs.groovy-lang.org/latest/html/documentation/grape.html#Grape-UsingGrapeFromtheGroovyShell
// Oddly, putting that one import on the same line makes it work with @Grab in groovysh and this
// should work with both groovy and groovysh
@Grab('uk.ac.gate:gate-core:8.6.1') 
import gate.*
import gate.creole.*
import java.io.File

inDir = ""
outDir = ""
cmd = ""
if(args.size() == 2 && (args[0].equals("load") || args[0].equals("populate"))) {
  inDir = args[1]
  cmd = args[0]
} else if (args.size() == 4 && args[0].equals("copy")) {
  inDir = args[1]
  outDir = args[2]
  outdirfile = new File(outDir)
  cmd = args[0]
  fmt = args[3]
} else {
  System.err.println("Need arguments: cmd(load/populate/copy), loaddir, [savedir, fmt]")
  System.err.println("  fmt one of: xml, finf, bdocjs, bdocjsgz, bdocym, bdocymgz, bdocmp")
  System.err.println("  load: load the document and immediately remove")
  System.err.println("  populate: populate a corpus with all documents")
  System.err.println("  copy: load document, save, remove from memory")
  System.exit(1)
}

Gate.init()
// load the various format plugins
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
corpus = Factory.newCorpus()

if(cmd.equals("populate")) {
  println("Populating...")
  inUrl = new File(inDir).toURI().toURL()
  start = System.currentTimeMillis()
  corpus.populate(inUrl, null, "", false)
  finish = System.currentTimeMillis()
  println("Documents loaded: "+corpus.size())
  println("Loading time: "+((finish-start)/1000.0))
} else if (cmd.equals("load")) {
  println("Loading...")
  files = []
  new File(inDir).eachFile { file ->
    files.add(file)
  }
  println("Got files: "+files.size())
  start = System.currentTimeMillis()
  files.each { file ->
    parms = Factory.newFeatureMap();
    parms.put("sourceUrl", file.toURI().toURL())
    System.err.println("Loading document ..."+file);
    doc = Factory.createResource("gate.corpora.DocumentImpl", parms);
    Factory.deleteResource(doc)
  }
  println("Loading time: "+((finish-start)/1000.0))
} else if (cmd.equals("copy")) {
  println("Loading and saving ...")
  files = []
  new File(inDir).eachFile { file ->
    files.add(file)
  }
  println("Got files: "+files.size())
  start = System.currentTimeMillis()
  files.each { file ->
    parms = Factory.newFeatureMap();
    parms.put("sourceUrl", file.toURI().toURL())
    // System.err.println("Loading document ..."+file+" name="+file.getName());
    doc = Factory.createResource("gate.corpora.DocumentImpl", parms);
    outfile = new File(outdirfile, file.getName()+"."+fmt)
    try {
      if(fmt=="xml") {
        gate.corpora.DocumentStaxUtils.writeDocument(doc, outfile)
      } else if (fmt=="txt") {
        text = doc.getContent().toString()
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8") 
        writer.write(text)
        writer.close();  
      } else {
        exp.export(doc, outfile, Factory.newFeatureMap())
      }
    } catch(Exception ex) {
      throw new RuntimeException("Problem writing the file", ex);
    }
    Factory.deleteResource(doc)
  }
  println("Copying time: "+((finish-start)/1000.0))
} 


