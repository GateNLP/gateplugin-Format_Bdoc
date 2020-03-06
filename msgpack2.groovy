// CHECKOUT https://github.com/msgpack/msgpack-java/blob/develop/msgpack-core/src/test/java/org/msgpack/core/example/MessagePackExample.java

@Grab(value='org.codehaus.plexus:plexus-container-default:1.0-alpha-9-stable-1', transitive=false)
@GrabResolver(name='gate-snapshots', root='http://repo.gate.ac.uk/content/groups/public/')
@Grab('uk.ac.gate:gate-core:8.6')
import gate.*;
import gate.creole.*;
import gate.util.persistence.*;
import static gate.Utils.*;

@Grab('uk.ac.gate.plugins:format-bdoc:1.1-SNAPSHOT')
import gate.plugin.format.bdoc.*;
import gate.lib.basicdocument.*;
import gate.lib.basicdocument.docformats.*;


@Grab('org.msgpack:jackson-dataformat-msgpack:0.8.20')
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jr.ob.JSON;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.core.*;
import org.msgpack.value.*;

//@Grab('com.fasterxml.jackson.core:jackson-core:2.10.2')
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;


Gate.init()

// create a doc with some text, anns and features
Document doc = Factory.newDocument("some document")
doc.getFeatures().put("example_doc_feature", "some value")
AnnotationSet set = doc.getAnnotations()
set.add(0,2,"Type", Factory.newFeatureMap())
set = doc.getAnnotations()
FeatureMap fm = Factory.newFeatureMap()
fm.put("key1", "value1")
fm.put("key2", 2)
set.add(1,3,"AnotherType", fm)

// convert to Bdoc
sj = new SimpleJson();
builder = new BdocDocumentBuilder();
builder.fromGate(doc);
bdoc = builder.buildBdoc();

ObjectMapper om = new ObjectMapper(new MessagePackFactory());
om.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

OutputStream out = new FileOutputStream("debug2.msgpack");

om.writeValue(out,"bdoc"); // this is a bdoc-related file
om.writeValue(out,1);  // version
om.writeValue(out,bdoc.gatenlp_type); // its a document
om.writeValue(out,bdoc.offset_type);
om.writeValue(out,bdoc.text);
om.writeValue(out,bdoc.features);
// need special treatment for the annotation sets:
// we first save a list of the annotation set names,
// then in the order of the names, each set as a sequence
// if fields

out.close();


// Read back in
InputStream is = new FileInputStream("debug2.msgpack");
om = new ObjectMapper(new MessagePackFactory());
om.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
String what = om.readValue(is, String.class);
Integer version = om.readValue(is, Integer.class);
String typ = om.readValue(is, String.class);
System.err.println("Got so far: what="+what+", version="+version+", typ="+typ);
bdoc2 = new BdocDocument();
bdoc2.offset_type = om.readValue(is, String.class);
bdoc2.text = om.readValue(is, String.class);
bdoc2.features = om.readValue(is, Map.class);
System.err.println("features: "+bdoc2.features);

is.close();

