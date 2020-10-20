# directory test-gcp

Notes and some prepared stuff to test the plugin with gcp.


## HOW TO: gcp

* Download latest release of gcp https://github.com/GateNLP/gcp/releases and unpack
  or build package from local repo and unpack
* create an input directory with format bdocjs files: indir
* create an empty output directory 
* make sure the plugin Format Bdoc version in empty.xgapp is correct
* make sure `GCP_HOME` and `GATE_HOME` are unset or set correctly
* run `java -jar $GCP_HOME/gcp-cli.jar -m 5G -t 8 batchfile1.xml`

## HOW TO: gcp-direct

* same preparation steps as above
* run `$GCP_HOME/

