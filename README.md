# gateplugin-Format_Bdoc

This plugin adds support for loading and saving GATE documents represented as "Bdoc" (BasicDocument)
serialized as JSON or GZIP compressed JSON. This representation and serialization can be exchanged
with the Python `gatenlp` package which is also able to read and write documents that way.


Maven Coordinates:
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
* docformats.SimpleJson: convert BdocDocument instances to and from JSON

