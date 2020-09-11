# gateplugin-Format_Bdoc

Plugin to save/load GATE documents as JSON, gzipped JSON, MessagePack, YAML, gzipped YAML.

Documentation: https://gatenlp.github.io/gateplugin-Format_Bdoc/

This plugin adds support for loading and saving GATE documents represented as "Bdoc" (Basianges to be made to a GATE document).

Maven Coordinates for the plugin:
* groupId: uk.ac.gate.plugins
* artifactId: format-bdoc


NOTE: as long as we depend on gate-plugin-parent 8.6.1 we need to use java 8 for testing, 
there is a problem with the jacoco maven plugin version (see https://github.com/GateNLP/gateplugin-Format_Bdoc/issues/8)
