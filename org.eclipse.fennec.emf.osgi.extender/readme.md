# Gecko EMF Model Extender

This is an implementation of an OSGi Extender. It tracks all bundle Manifest for a special *Requirement*.

This defines the *ecore* model folders. The extender looks into these folders and tries to load the model files  for the given paths. The loaded models are then registered to the Gecko EMF infrastructure. That means each *ecore* model will get an own *EPackageConfigurator*

Each model will be registered with the service properties *emf.model.name* and *emf.model.nsURI*. Additional properties are appended to the service properties.

## Registering a model

The bundle that contains the model needs to define a OSGi Requirement:

```
Require-Capability: \
	osgi.extender;\
	filter:="(osgi.extender=emf.model)";\
	models:List<String>="/model;foo=bar;test=me,OSGI-INF/model;test=me;foo=baz,/toast/toast.ecore;toast=me;foo=toast"
```

We make use of the OSGi Extender here. So there is an own extender **emf.model**. The attribute *models* is of type String+ and contains a comma separated list of folder and properties.

### Registering a Single Model File

```
Require-Capability: \
	osgi.extender;\
	filter:="(osgi.extender=emf.model)";\
	models:List<String>="/test/test.ecore;foo=bar;test"
```

This example registers the *test.ecore* that is located in the JAR under */test/test.ecore*. The *EPAckageConfigurator will be registered with additional properties **foo=bar** and **test=***

### Registering a Model Folder

There is also the possiblility to scan folder for *ecore* files.

```
Require-Capability: \
	osgi.extender;\
	filter:="(osgi.extender=emf.model)"
```

The example above registers all *ecore* model found in the folder **/model**

For alternative folders look at this example:

```
Require-Capability: \
	osgi.extender;\
	filter:="(osgi.extender=emf.model)";\
	models:List<String>="OSGI-INF/model"
```

## Additional Properties

For each model location entry, additional key value pairs can be provided. They are appended to the model location using **;** (semicolon)

`/model;foo=bar;test=me`

This provided additional properties **foo=bar** and **test=me** to each *ecore* model in the folder *model*

For single model files it works as well.

`/model/test.ecore;foo=bar;test=me`