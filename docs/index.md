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


