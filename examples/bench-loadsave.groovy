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
} else if (args.size() == 3 && args[1].equals("copy")) {
  inDir = args[1]
  outDir = args[2]
  cmd = args[0]
} else {
  System.err.println("Need arguments: cmd(load/populate/copy), loaddir, [savedir]")
  System.exit(1)
}

Gate.init()
// load the various format plugins
Gate.getCreoleRegister().registerPlugin(
            new Plugin.Maven("uk.ac.gate.plugins", "format-fastinfoset", "8.5"));
Gate.getCreoleRegister().registerPlugin(
            new Plugin.Maven("uk.ac.gate.plugins", "format-bdoc", "1.6.1"));

fmt2exp = [:]
fmt["finf"] = cr.get("gate.corpora.FastInfosetExporter").getInstantiations().iterator().next()
fmt["bdocjs"] = cr.get("gate.plugin.format.bdoc.ExporterBdocJson").getInstantiations().iterator().next()
fmt["bdocmp"] = cr.get("gate.plugin.format.bdoc.ExporterBdocMsgPack").getInstantiations().iterator().next()
fmt["bdocjsgz"] = cr.get("gate.plugin.format.bdoc.ExporterBdocJsonGzip").getInstantiations().iterator().next()
corpus = Factory.newCorpus()

if(cmd.equals("populate")) {
  inUrl = new File(inDir).toURI().toURL()
  start = System.currentTimeMillis()
  corpus.populate(inUrl, null, "", false)
  finish = System.currentTimeMillis()
  println("Documents loaded: "+corpus.size())
  println("Loading time: "+((finish-start)/1000.0))
} else if (cmd.equals("load")) {
  files = []
  new File(inDir).eachFile { file ->
    files.add(file)
  }
  files.each { file ->
    println("Would load "+file)
  }
}

