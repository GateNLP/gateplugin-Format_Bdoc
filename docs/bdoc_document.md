# Basic Document Representation

The "Basic Document" or "Bdoc" representation is a simple way to represent GATE documents, features, annotation sets and annotations
through basic datatypes like strings, integers, maps and arrays so that the same representation can be easily used 
from several programming languages. The representation is limited to the following data types: string, integer, float, boolean, array/list, map (basically what is supported by basic JSON). 

The various serialization formats supported by the plugin (JSON, YAML, MsgPack) simply serialize that representation using
one of those formats. There may be additional fields in the serialization representation in order to deal with format versions
or for distinguishing object types. These are mentioned below.


## The abstract BdocDocument representation 

A document is map with the following keys:

* `name` (String): the document name
* `text` (String): the document text 
* `offset_type` (String, either "p" or "j"): how offsets represent the individual code points in the text: either as the number of code point as in in Python (p) or as the number of UTF-16 code units as in Java (j)
* `annotation_sets` (Map): annotation set names mapped to a map representing an annotation set (see below). 
* `features` (Map, see below): the document features 

The document text must be able to represent any Unicode text and different serialization methods may use different ways of how to encode the text. 

Features are represented as a map:

* map a feature name to a feature value
* a feature name must be a non-null String
* a feature value must be one of the following basic data types: string, integer, float, boolean, array/list, map (essentially what is supported by basic JSON)
* shared references to the same data object (e.g. two features referencing the same list) are possible but may not be preserved by a specific serialization format. 
* recursive data structures (e.g. an element of an array being the array itself) are not allowed 

An Annotation set is represented as a map with the following keys:

* `annotations` (List): a list of maps representing the annotation (see below)
* `next_annid` (Integer): the next annotation id that can be assigned in this set


Annotations are represented as a map with the following keys:

* `start` (Integer):  the start offset of the annotation
* `end` (Integer): the end offset of the annotation
* `type` (String): the annotation type name of the annotation, should not be an empty string or a string containing only white space
* `id` (Integer): the "annotation id" a unique number assigned to each annotation in a set
* `features` (Map): features as described above

## Examples

Here is a simple examle document serialized as JSON (bdocjs):

```
{
   "offset_type" : "p",
   "name" : "",
   "features" : {
      "feat1" : "value1"
   },
   "annotation_sets" : {
      "" : {
         "annotations" : [
            {
               "end" : 2,
               "id" : 0,
               "features" : {
                  "a" : 1,
                  "b" : true,
                  "c" : "some string"
               },
               "start" : 0,
               "type" : "Type1"
            }
         ],
         "name" : "",
         "next_annid" : 1
      },
      "Set2" : {
         "annotations" : [
            {
               "id" : 0,
               "start" : 2,
               "features" : {},
               "type" : "Type2",
               "end" : 8
            }
         ],
         "next_annid" : 1,
         "name" : "Set2"
      }
   },
   "text" : "A simple document"
}
```

The same document serialized as YAML (bdocym):

```
annotation_sets:
  ? ''
  : annotations:
    - end: 2
      features:
        a: 1
        b: true
        c: some string
      id: 0
      start: 0
      type: Type1
    name: ''
    next_annid: 1
  Set2:
    annotations:
    - end: 8
      features: {}
      id: 0
      start: 2
      type: Type2
    name: Set2
    next_annid: 1
features:
  feat1: value1
name: ''
offset_type: p
text: A simple document
```

