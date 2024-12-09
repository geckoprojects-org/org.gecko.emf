# RDF Models

These models are derived from the xmi files provided by the OMG

* *RDFProfile.xmi* - UML Profile for RDF [http://www.omg.org/spec/ODM/20131101/RDFProfile.xmi](http://www.omg.org/spec/ODM/20131101/RDFProfile.xmi)
* *RDFLibrary.xmi* - The Library for RDF [https://www.omg.org/spec/ODM/20131101/RDFLibrary.xmi](https://www.omg.org/spec/ODM/20131101/RDFLibrary.xmi)

## RDF Profile

This is the UML Profile Description.

The *RDFProfile.ecore* was generated out of the *RDFProfile.xmi* via *RDFProfile.genmodel.

**NOTE:** Several warnings occur, when trying to load the *xmi* file during import. The corresponding warnings describe, how to setup the wizard genmodel UML import page. Usually the *report* entries have to set to *ignore* until no errors occur

Some manual modifcations are neccessary in the ecore. Because of duplicate feature warnings, new interface have been introduced to solve the multiple inheritace problems. The interfaces start with *Base* and are marked as interfaces. Usually these interfaces only contain one feature.

## RDF Library

This is the RDF MetaModel. The ecores *rdf.ecore* and *rdfs.ecore* have been generated out of the *RDFLibrary.xmi* via *RDFLibrary.genmodel*.

**NOTE:** Several warnings occur, when trying to load the *xmi* file during import. The corresponding warnings describe, how to setup the wizard genmodel UML import page. Usually the *report* entries have to set to *ignore* until no errors occur

Some manual modifcations are neccessary in the ecore. The main problems are errors because of bi-directional feature references. Therefore the classes with the *_*-reference are wrapper classes, for the container. E.G. *subject* became *Subject*. The EOpposite is *Statement*. So in *Statement*, the *subject* feature was set from type *Resource* to the wrapper class *Subject*. Now in *Subject*, the EOpposite can be set to *subject::Subject*. 

This wrapping pattern, can be used for all errors. Important is to realize, that e.g. in *Resource* or *Class*, there is a need to add correspinding EReferences with type of the wrapper. After that the corresponding EOpposite is able to be set. 
