# gateplugin-Format_Bdoc

This plugin adds support for loading and saving GATE documents represented as "Bdoc" (BasicDocument) instances. 
This representation tries to be as simple as possible while still representing everything that can be 
represented in a GATE `SimpleDocument` instance. The representation is also almost identical to how 
`Document` instances are represented in the Python `gatenlp` package and thus ideal for exchanging GATE 
documents between Java GATE and Python `gatenlp`. 

This plugin allows to save and load GATE Documents represented as BasicDocument instances in the following formats:
* JSON
* JSON, GZip compressed
* MessagePack (see https://msgpack.org/index.html)
* YAML (see https://yaml.org/)
* YAML, GZip compressed

In addition it can load and process `gatenlp` ChangeLog instances (data that represents changes to be made to a GATE document).

Maven Coordinates for the plugin:
* groupId: uk.ac.gate.plugins
* artifactId: format-bdoc

## Versions

* Version 1.0-SNAPSHOT is compatible with GATE 8.6.1 and later
  * this version is in branch `v1.0-pre9.0`
* Version 1.1-SNAPSHOT and later require GATE 9.0-SNAPSHOT or later

## Bdoc representation

The package gate.lib.basicdocument provides classes to represent the main 
components of a GATE document in a much more straightforward way. No nodes,
no complex hierarchy of classes and subclasses and interfaces, and currently
there is no API on the Java side.

However the same representation is also used in the Python [gatenlp](https://gatenlp.github.io/python-gatenlp/) package which does have an API defined to manipulate them. 

The following components are currently implemented:
* BdocAnnotation 
* BdocAnnotationSet
* BdocDocument
* ChangeLog: a sequence of changes that has been applied to a BdocDocument

In addition the following helper classes:
* GateDocumentUpdate: create or update a GATE document from all or parts of
  a BdocDocument or ChangeLog
* BdocDocumentBuilder: create a BdocDocument from all or parts of a 
  GATE document

