// CHECKOUT https://github.com/msgpack/msgpack-java/blob/develop/msgpack-core/src/test/java/org/msgpack/core/example/MessagePackExample.java

@Grab(value='org.codehaus.plexus:plexus-container-default:1.0-alpha-9-stable-1', transitive=false)
@GrabResolver(name='gate-snapshots', root='http://repo.gate.ac.uk/content/groups/public/')
@Grab('uk.ac.gate:gate-core:8.6')
import gate.*;
import gate.creole.*;
import gate.util.persistence.*;
import static gate.Utils.*;

@Grab('uk.ac.gate.plugins:format-bdoc:1.7-SNAPSHOT')
import gate.plugin.format.bdoc.*;
import gate.lib.basicdocument.*;
import gate.lib.basicdocument.docformats.*;


@Grab('org.msgpack:jackson-dataformat-msgpack:0.8.20')
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jr.ob.JSON;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.core.*;
import org.msgpack.value.*;


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

// try to store
MessagePacker packer = MessagePack.newDefaultPacker(new FileOutputStream("debug1.msgpack"));

class MsgPacker {
  private MessagePacker packer;
  public MsgPacker(MessagePacker packer) {
    this.packer = packer;
  }
  public pack(String obj) {
    packer.packString((String)obj);
  }
  public pack(Integer obj) {
    packer.packInt((Integer)obj);
  }
  public pack(List obj) {
    List l = (List)obj;
    packer.packInt(l.size());
    for(int i = 0; i<l.size(); i++) {
      pack(l.get(i));
    }
  }
  public pack(Map obj) {
    packer.packInt(T_MAP);
    Map m = (Map<?,?>)obj;
    packer.packInt(m.size());
    for(Map.Entry<Object, Object> e : obj) {
      pack(e.getKey());
      pack(e.getValue());
    }
  }

}

// we store a bdoc in a specific sequence of fields
MsgPacker p = new MsgPacker(packer);
p.pack("bdoc");  // bdoc-related
p.pack(1);  // version
p.pack(bdoc.gatenlp_type);
p.pack(bdoc.offset_type);
p.pack(bdoc.text);
p.pack(bdoc.features);

packer.close();

// now try to get it back 

MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream("debug1.msgpack"));

while(unpacker.hasNext()) {
  MessageFormat format = unpacker.getNextFormat(); 
