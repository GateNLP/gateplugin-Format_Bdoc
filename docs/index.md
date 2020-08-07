# GATE Plugin Format Bdoc

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

The plugin requires GATE version 9.0-SNAPSHOT or later.

## Formats


