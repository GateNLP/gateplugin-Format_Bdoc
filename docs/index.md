# GATE Plugin Format Bdoc

This plugin adds support for loading and saving GATE documents represented as "Bdoc" (BasicDocument) instances. 
This representation tries to be as simple as possible while still representing everything that can be 
represented in a GATE `SimpleDocument` instance. The representation is also almost identical to how 
`Document` instances are represented in the Python `gatenlp` package and thus ideal for exchanging GATE 
documents between Java GATE and Python `gatenlp`. The representation is also aware of the differences between
Python and Java of how Unicode strings are represented and allows to convert annotation offsets between 
the two. 


This plugin allows to save and load GATE Documents represented as BasicDocument instances in the following serialization formats (see section Formats below for details):

* JSON
* JSON, GZip compressed
* MessagePack (see [https://msgpack.org/index.html](https://msgpack.org/index.html))
* YAML (see [https://yaml.org/](https://yaml.org/))
* YAML, GZip compressed

In addition it can load and process `gatenlp` ChangeLog instances (data that represents changes to be made to a GATE document).

Saving and loading work exactly as for the default GATE XML format with the following exceptions:

* Only YAML and YAML, Gzip supports restoring shared data, e.g. two features having the same list object as a value.
* All formats save and restore the document name instead of generating the document name from the file name. Only if the serialized JSON/msgpack/yaml file does not have a name stored or the name is empty, the file name is used to create the document name as usual. 

Maven Coordinates for the plugin:
* groupId: uk.ac.gate.plugins
* artifactId: format-bdoc

## Formats

The following formats are supported for loading and saving (all formats are supported by the [Python `gatenlp`](https://gatenlp.github.io/python-gatenlp/) package):

### JSON

* File extension: `.bdocjs`
  * also recognized but should not be used: `.bdocjson`, `.bdocsjson`
* Mime type: `text/bdocjs` 
* Document is represented as a JSON map
* Used in the [Python plugin](http://gatenlp.github.io/gateplugin-Python/)
* Shared objects (e.g. two different features referencing the same list) cannot be represented and are instead converted into separate equal objects

### JSON, Gzip compressed

* File extension:  `.bdocjs.gz` 
  * also recognized but should not be used: `.bdocjson.gz`, `.bdocsjson.gz`
* Mime type: `text/bdocjs+gzip` 
* Document is represented as a JSON map, then gzip compressed

### YAML

* File extension: `.bdocym`
* Mime type: `text/bdocym`
* Document is represented as YAML map without a class tag (see [https://yaml.org/](https://yaml.org/))
* Shared objects are properly saved and restored

### YAML, Gzip compressed

* File extension: `.bdocym.gz`
* Mime type: `text/bdocym+gzip`
* Same as YAML, but gzip compressed

### MessagePack

* File extension: `.bdocmp`
* Mime type: `text/bdocmp`
* Document is represented in the binary MessagePack format ([https://msgpack.org/index.html](https://msgpack.org/index.html)). This results in very small files. 
* As with JSON, shared objects are not preserved and instead converted into separate equal objects. 

## ResourceHelper API

The ResourceHelper API allows for a run-time only programmatic use of some of the plugin features without the need
to make the using code depend on the plugin. Instead the generic [GATE ResourceHelper Interface](https://jenkins.gate.ac.uk/job/gate-core/javadoc/index.html)
is used to invoke plugin functionality. 

The [`call`](https://javadoc.io/doc/uk.ac.gate.plugins/format-bdoc/latest/gate/plugin/format/bdoc/API.html) method takes the following
parameters: 

* `action` (String): the name of the action to perform or what should get returned
* `resource` (gate.Resource): some GATE resource like a Document 
* `params...` (Object): an arbitrary number of additional objects to pass on

The method returns some Object or null. 

The following `action` values can be used, the expected parameters and their types are listed in parentheses:

* `json_from_doc` (resource: Document):  return a JSON string representation of the document
* `fmap_to_map` (param0: FeatureMap): return a Map representation of the feature map
* `bdoc_from_string` (param0: String): return a BdocDocument from the JSON string representation
* `bdoc_from_doc` (resource: Document): return a BdocDocument from the GATE document
* `bdocmap_from_doc` (resource: Document, param0: Collection<String> or null, param1: Boolean or null): create 
  the Map representation of a BdocDocument created from the document, if param0 is specified and it is a collection
  of Strings, only include the annotation sets with those names in the result. If param1 is present and it is 
  a boolean that is true, include empty placeholder sets for all sets not included in the result
* `log_from_string` (param0: String): create ChangeLog from the JSON String representation
* `log_from_map` (param0: Map<String,Object>): create a ChangeLog from the Map representation 
* `update_document_from_bdoc` (resource: Document, param0: BdocDocument): update a given GATE document from the 
  information in a BdocDocument.
* `update_document_from_bdocjson` (resource: Document, param0: String): update GATE document from the 
  BdocDocument created from the JSON String
* `update_document_from_log` (resrouce: Document, param0: ChangeLog): update GATE document from the given
  ChangeLog
* `update_document_from_logjson` (resource: Document, param: String): update and return GATE document from
  the ChangeLog created from the JSON String



## Speed and Size comparison with GATE XML and FastInfoset formats

### Single Threaded 

* Benchmark: using SimpleBenchmark class
* (internal corpus "gatexml")
* Number of documents: 2015
* Documents with lots of annotations
* Number of formats: 7
* Number of iterations: 3
* Total number of load/save per doc: 21
* load/save: average ms per document
* size: average kB per document

| Format | load | save | size | 
| ------ | ---- | ---- | ---- | 
| xml | 0.009 | 0.022 | 0.458 |
| finf | 0.008 | 0.028 | 0.309 |
| bdocjs | 0.008 | 0.097 | 0.173 |
| bdocjs.gz | 0.007 | 0.104 | 0.153 |
| bdocmp | 0.008 | 0.065 | 0.065 |
| bdocym | 0.008 | 0.075 | 0.169 |
| bdocym.gz | 0.007 | 0.092 | 0.152 |

## JavaDocs

See https://javadoc.io/doc/uk.ac.gate.plugins/format-bdoc/latest/index.html

