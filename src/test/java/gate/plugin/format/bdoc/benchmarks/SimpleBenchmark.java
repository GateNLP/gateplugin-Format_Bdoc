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
package gate.plugin.format.bdoc.benchmarks;

import gate.CreoleRegister;
import gate.Document;
import gate.DocumentExporter;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.Plugin;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FileUtils;

/**
 * Run simple benchmarking.
 *
 * @author Johann Petrak
 */
public class SimpleBenchmark {

  public static class BenchData {

    public int n_load = 0;
    public int n_save = 0;
    public double load = 0.0;  // seconds
    public double save = 0.0;  // seconds
    public double size_save = 0.0;  // kb

    public void add(BenchData other) {
      n_load += other.n_load;
      n_save += other.n_save;
      load += other.load;
      save += other.save;
      size_save += other.size_save;
    }

    public void addLoad(BenchData other) {
      n_load += other.n_load;
      load += other.load;
    }

    public void addSave(BenchData other) {
      n_save += other.n_save;
      save += other.save;
      size_save += other.size_save;
    }
  }

  public Document load(File from) throws ResourceInstantiationException {
    FeatureMap fm = Factory.newFeatureMap();
    // fm.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, mimtypeString);
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", fm);
    return doc;
  }

  public void save(Document doc, File to, DocumentExporter exporter) throws XMLStreamException, IOException {
    if (exporter == null) {
      gate.corpora.DocumentStaxUtils.writeDocument(doc, to);
    } else {
      exporter.export(doc, to);
    }
  }

  public BenchData copyDocuments(List<File> inFiles, File outDir, String format, DocumentExporter exporter)
          throws ResourceInstantiationException, XMLStreamException, IOException, InterruptedException {
    BenchData bd = new BenchData();
    System.gc();
    Thread.sleep(500);  // 0.5 seconds
    for (File inFile : inFiles) {
      File outFile = new File(outDir, inFile.getName() + "." + format);
      long start = System.currentTimeMillis();
      Document doc = load(inFile);
      bd.load += (System.currentTimeMillis() - start) / 1000.0;
      bd.n_load += 1;      
      start = System.currentTimeMillis();
      save(doc, outFile, exporter);
      bd.save += (System.currentTimeMillis() - start) / 1000.0;
      bd.n_save += 1;
      bd.size_save += FileUtils.sizeOf(outFile) / 1024.0;
      Factory.deleteResource(doc);
    }
    return bd;
  }

  public List<File> fileList(File inDir) {
    List<File> dirFiles = new ArrayList<>();
    for (File f : inDir.listFiles()) {
      dirFiles.add(f);
    }
    return dirFiles;
  }

  public static String d2str(double val) {
    return String.format("%.2f", val);
  }
  
  
  /**
   * Run the benchmarks.Need 2 parameters: input directory with pre-annotated
   * GATE documents in xml format; empty working directory
   * <p>
   * The program outputs the benchmark results to stdout.
   * <p>
   * To run this using maven, use something like:
   * <pre>
   * {@code
   * JAVA_TOOL_OPTIONS="-Xmx5G -Xms5G" mvn -Dexec.classpathScope=test test-compile exec:java -Dexec.mainClass="gate.plugin.format.bdoc.benchmarks.SimpleBenchmark" -Dexec.args="bench_in bench_out"
   * }
   * </pre>
   *
   * @param args program arguments
   * @throws java.io.IOException
   * @throws gate.util.GateException
   */
  public static void main(String[] args) throws IOException, GateException, ResourceInstantiationException, XMLStreamException, InterruptedException {
    if (args.length != 2) {
      throw new RuntimeException("Need two parameters: inDir, workDir");
    }
    SimpleBenchmark bench = new SimpleBenchmark();

    // Load format plugins
    Gate.init();
    Gate.getCreoleRegister().registerPlugin(
            new Plugin.Maven("uk.ac.gate.plugins", "format-fastinfoset", "8.5"));
    Gate.getCreoleRegister().registerPlugin(
            new Plugin.Maven("uk.ac.gate.plugins", "format-bdoc", "1.3-SNAPSHOT"));

    File inDir = new File(args[0]);
    File outDir = new File(args[1]);
    File outDir1 = new File(outDir, "bench1");
    File outDir2 = new File(outDir, "bench2");
    if (!inDir.exists()) {
      throw new RuntimeException("Input directory must exist and contain GATE XML files");
    }
    if (!outDir.exists()) {
      throw new RuntimeException("Output directory must exist and be empty");
    }
    if (outDir1.exists()) {
      FileUtils.forceDelete(outDir1);
    }
    if (outDir2.exists()) {
      FileUtils.forceDelete(outDir2);
    }
    FileUtils.forceMkdir(outDir1);
    FileUtils.forceMkdir(outDir2);

    Map<String, DocumentExporter> format2exporter = new HashMap<>();
    CreoleRegister cr = Gate.getCreoleRegister();
    format2exporter.put("xml", null);
    format2exporter.put("finf",
            (DocumentExporter) cr.get("gate.corpora.FastInfosetExporter").getInstantiations().iterator().next());
    format2exporter.put("bdocjs",
            (DocumentExporter) cr.get("gate.plugin.format.bdoc.ExporterBdocJson").getInstantiations().iterator().next());
    format2exporter.put("bdocjs.gz",
            (DocumentExporter) cr.get("gate.plugin.format.bdoc.ExporterBdocJsonGzip").getInstantiations().iterator().next());
    format2exporter.put("bdocmp",
            (DocumentExporter) cr.get("gate.plugin.format.bdoc.ExporterBdocMsgPack").getInstantiations().iterator().next());
    format2exporter.put("bdocsjs",
            (DocumentExporter) cr.get("gate.plugin.format.bdoc.ExporterBdocSimpleJson").getInstantiations().iterator().next());
    format2exporter.put("bdocsjs.gz",
            (DocumentExporter) cr.get("gate.plugin.format.bdoc.ExporterBdocSimpleJsonGzip").getInstantiations().iterator().next());

    // Get the list of all xml files in indir
    List<File> inFiles = bench.fileList(inDir);
    int n = inFiles.size();
    if (n > 0) {
      System.err.println("Number of input XML files: " + n);
    } else {
      throw new RuntimeException("Input directory does not contain any XML files");
    }

    // Initially we have the XML files.
    // we copy to XML files to all formats including XML to outDir1 
    // every time we then have the documents in that format in outDir1 we 
    // copy to all formats to OutDir2 
    // * we have 5 formats
    // * every format is written to outDir1 once
    // * every format is read from outDir1 5 times
    // * every format is written to outDir2 5 times
    Map<String, BenchData> format2data = new HashMap<>();
    for (String fmt : format2exporter.keySet()) {
      format2data.put(fmt, new BenchData());
    }

    // outermost loop: run everything 10 times
    for (int i = 0; i < 10; i++) {
      // outer loop: read from inDir and write to outDir1
      for (String fmt_from : format2exporter.keySet()) {
        System.err.println("Iteration " + i + " - Reading XML and writing format " + fmt_from + " to "+outDir1);
        FileUtils.forceDelete(outDir1);
        FileUtils.forceMkdir(outDir1);
        bench.copyDocuments(inFiles, outDir1, fmt_from, format2exporter.get(fmt_from));
        for (String fmt_to : format2exporter.keySet()) {
          FileUtils.forceDelete(outDir2);
          FileUtils.forceMkdir(outDir2);
          System.err.println("Copying from "+ outDir1 +  " from format " + fmt_from + " to " + outDir2 + " to format " + fmt_to);
          List<File> fromFiles = bench.fileList(outDir1);
          BenchData data = bench.copyDocuments(inFiles, outDir2, fmt_to, format2exporter.get(fmt_to));
          //System.err.println("Time for loading from "+fmt_from+": "+data.load);
          //System.err.println("Time for saving to    "+fmt_from+": "+data.save);
          //System.err.println("Total size saving to  "+fmt_from+": "+data.size_save);
          format2data.get(fmt_from).addLoad(data);
          format2data.get(fmt_to).addSave(data);
        }
      }
    }
    File tsvFile = new File(outDir, "bench.tsv");
    try (FileOutputStream fos = new FileOutputStream(tsvFile);
            PrintStream ps = new PrintStream(fos)) {
      ps.println("| Format | load | save | size | ");
      ps.println("| ------ | ---- | ---- | ---- | ");
      for (String fmt : format2data.keySet()) {
        BenchData data = format2data.get(fmt);
        ps.print("| ");
        ps.print(fmt);
        ps.print(" | ");
        ps.print(d2str(data.load));
        ps.print(" | ");
        ps.print(d2str(data.save));
        ps.print(" | ");
        ps.print(d2str(data.size_save));
        ps.print(" |");
        ps.println();
      }
    }
    System.err.println("Created file " + tsvFile);
    File mdFile = new File(outDir, "bench.md");
    try (FileOutputStream fos = new FileOutputStream(mdFile);
            PrintStream ps = new PrintStream(fos)) {
      ps.println("Format\tload\tsave\tsize");
      for (String fmt : format2data.keySet()) {
        BenchData data = format2data.get(fmt);
        ps.print(fmt);
        ps.print("\t");
        ps.print(d2str(data.load));
        ps.print("\t");
        ps.print(d2str(data.save));
        ps.print("\t");
        ps.print(d2str(data.size_save));
        ps.println();
      }
    }
    System.err.println("Created file " + mdFile);

  }
}
