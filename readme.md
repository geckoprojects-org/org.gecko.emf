> [!CAUTION]
> **This repository is deprecated and archived.**
> It is no longer maintained. Development continues in [eclipse-fennec/emf.osgi](https://github.com/eclipse-fennec/emf.osgi).

[![CI Build](https://github.com/geckoprojects-org/org.gecko.emf/actions/workflows/build.yml/badge.svg)](https://github.com/geckoprojects-org/org.gecko.emf/actions/workflows/build.yml)[![License](https://github.com/geckoprojects-org/org.gecko.emf/actions/workflows/license.yml/badge.svg)](https://github.com/geckoprojects-org/org.gecko.emf/actions/workflows/license.yml )[![Sonar](https://github.com/geckoprojects-org/org.gecko.emf/actions/workflows/sonar.yml/badge.svg)](https://github.com/geckoprojects-org/org.gecko.emf/actions/workflows/sonar.yml )[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=bugs)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=code_smells)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=coverage)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=alert_status)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=security_rating)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=sqale_index)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=geckoprojects-org_org.geckoprojects.emf&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=geckoprojects-org_org.geckoprojects.emf)

# EMF for pure OSGi

## GeckoEMF

EMF is one of the most powerful MDSD tools. Unfortunately it comes with strong ties to Eclipse and Equinox, because it uses Extension Points. It can be used in Java SE and other OSGi frameworks, but it usually requires a lot of manual work to register EPackages, ResoureFactories etc.

### Usage

GeckoEMF sets out to provide a way to use EMF in pure OSGi environments regardless of the framework you use. It is an extension on top of EMF, so EMF can be used as before, without any changes. The project is based on the [eModelling](https://github.com/BryanHunt/eModeling) project of Bryan Hunt.

The sense of Gecko EMF is to register all EMF models as OSGi services. So EMF `ResourceSet` can be injected as service using:

```java
@Reference
private ResourceSet resourceSet;
```

This is achieved registering a prototype service factory for the resource sets. Alternatively a `ResourceSetFactory` can also be injected. 

To ensure that a model is regsitered in a resource set, the target filtering against the *emf.name* and other supported properties can be used:

```java
@Reference(target="(emf.name=foo)")
private ResourceSet resourceSet;
```
### Configurators

There are different interfaces that can be implemented and registered as service. For them the *emf.configurator.name* property can be set, to give the implamantation a name. This can be used for filtering afterwards:

* `org.gecko.emf.osgi.ResourceFactoryConfigurator` - Configurator for a resource factory. The used ResourceFactory registry will be provided by the framework.
* `org.gecko.emf.osgi.ResourceSetConfigurator` - Configurator for a resource set. These configurators are usually called before creating a new *ResourceSet* instance
* `org.gecko.emf.osgi.EPackageConfigurator` - Configurator for registering a new model using the EPeckage and the EPackageRegsitry.

There are additional properties that can be provided.

### Service Properties

Supported properties are defined in the *EMFNamespaces* constant:

* **emf.name** - the model name (String[])
* **emf.nsUri** - the model package namenspace uri
* **emf.fileExtension** - file extension for resource factories
* **emf.protocol** - protocol value for resource factories
* **emf.contentType** - content type definition for resource factories
* **emf.version** - the model version
* **emf.feature** - property for special feature of a model
* **emf.configuratorName** - name of the configurator, when creating an own one
* **emf.dynamicEcoreUri** - Uri to the *ecore* when using the dynamic pacakge registration

Usually the right properties are autoamtically set during registration via code generator or extender or the configuration based model registration.

When you create an own configurator based implementation, these properties can be used and are forwarded to the service factory for set *ResourceSet*. The properties are merged to a list of values in the factory service property map.

If a configurator disappears, the properties of the *ResourceSet* service factory are updated in a way that the removed properties also disappear. 

Example:

* *FooEPackageConfigurator* with **emf.configuratorName=foo**
* *BarEPackageConfigurator* with **emf.configuratorName=bar**
* *ResourceSet* would get merged property **emf.configuratorName=[foo, bar]**

The service lifecycle dynamics are handled by Gecko EMF!

### The emf.feature Prefix Property for emf.feature.* 

As described above, the *emf.feature* property can indicate any feature-string. You can filter against this in the *ResourceSet* or *ResourceSetFactory*.

In addition to that sometimes you may want to forward a certain, self defined property to the whole Gecko EMF. You can use the prefix *emf.feature.* for your property:

Puttinf *emf.feature.foo=bar* as service property of a configurator, will end up as *foo=bar* in the *ResourceSet* or *ResourceSetFactory* service properties.

This functionality works for all configurators and also for the dynamic model registration.

### Model Registration

The model registration can happen:

* using the **GeckoEMF Code Generator**
* using the **GeckoEMF Extender** from *org.gecko.emf.osgi.extender*
* using **Dynamic Model Registration** with a configuration based setup



## Gecko EMF Code Generator

The provided Code generator is based on the default EMF code generator as declared in the dependencies. As the use is not intended for Eclipse PDE use, no Manifest, plugin.xml or any other PDE Project specific files will be created. 

A few additions have been made though. It will create a Component that will register your EPackage, EPackageFactory and a Condition for you. If a ResourceFactory is generated, it will be an OSGi Component as well. All generated Packages will be served with a `package-info.java` as well. If a `EAnnotation` with the source `Version` and a detailed entry with `value` as key is present on any `EPackage` this will define you exported version. If non is present the default is `1.0`.

This generator is triggered setting the *Genmodel - All - OSGi Campatible* to *true*.

After that a new *configuration* package is created and two two classes are generated:

* *EPackageConfigurator* - Class to configure the EPackage in a proper way
* *ConfigurationComponent* - to register all needed stuff as service in a safe way

The following services are registered with the generated code and can be used:

* *EPackage, GeneratedEPackage* - Service with the generated EPackage instance, with the provided properties e.g. *emf.name, emf.fileExt, emf.contentType, emf.protocol*
* *EFactory, GeneratedEFactory* - Service with the generated EFactory instance, with the provided properties e.g. *emf.name, emf.fileExt, emf.contentType, emf.protocol*
* *Resource.Factory, GeneratedResourceFactory* - Service with the generated ResourceFactory and provided properties e.g. *emf.name, emf.fileExt, emf.contentType, emf.protocol*
* *Condition* - OSGi Condition service with all model related properties

Compared to the static access to EFactory or EPackage, one can access these instances using services, by either injecting the generated EPackage or EFactory interface or the abstract EFactory or EPackage directly. To filter the right model the target filter can be used against the service properties e.g. *emf.name=<your-model>*

## Gecko EMF Extender

To use this mechanism the bundle *org.gecko.emf.osgi.extender* is needed.

Follow the instructions here: [Extender Documentation](org.gecko.emf.osgi.extender/readme.md)

## Dynamic Package Registration

To dynamically register a EMF model package via configuration you can use a configuration PID **DynamicModelConfigurator** with a custom identifier. A sample configuration can look like this:

```json
{
	":configurator:resource-version": 1,
	":configurator:symbolicname": "org.gecko.emf.osgi.demo",
	":configurator:version": "0.0.0",
	"DynamicPackageLoader~demo": {
		"emf.dynamicEcoreUri": "https://mymodelpage.org/demo/demo.ecore",
		"emf.feature": ["foo", "bar"],
		"emf.feature.my": "own"
	}
}

```

This configuration registers a model package loaded from the given *dynamicEcoreUri* property. The package is then registered with the given properties as *EPackage* and *EPackageConfigurator* service. The package nsUri is also provided from the package.

In this case the properties are:

* *emf.name* - `EPackage#getName()`
* *emf.nsUri* - `EPackage#getNsUri()`
* *emf.feature* - forwarded from the configuration, if set
* *emf.feature.\** - forwarded as described in the feature property section. From the example above the result of *emf.feature.my* would be *my=own*

This setup works with the configurator as well with the config admin. 

If a package location uri via *dynamicEcoreUri* property changes, this will lead into an un-registration of the former model and a try to reload the model from the new location.

If some of the other properties change, the properties will be updated without any re-registration of the *EPackage*.


## Dependencies

The latest Version is named here:

https://github.com/geckoprojects-org/org.geckoprojects.emf/blob/master/cnf/ext/version.bnd#L1

### Maven BOM

We provide a maven BOM on central under the following coordinates:


```xml
<dependency>
      <groupId>org.geckoprojects.emf</groupId>
      <artifactId>org.gecko.emf.osgi.bom</artifactId>
      <version>${geckoemf.version}</version>
</dependency>
```

or as short GAV

```
org.geckoprojects.emf:org.gecko.emf.osgi.bom:${geckoemf.version}
```

### BND

Besides the BOM we provide an  [OSGi Repository](http://devel.data-in-motion.biz/public/repository/gecko/release/geckoEMF/) as well.

We additionally provide a Workspace extension that provides some more comfort including a bnd code generator and project templates for EMF projects.

### BND Library

Since bnd Version 6.1.0 the concept of [libraries](https://bnd.bndtools.org/instructions/library.html) was introduced, which provides some easy extensions for your bnd workspace setup.

Add the following maven dependency from maven central to you workspace:

```
org.geckoprojects.emf:org.gecko.emf.osgi.bnd.library.workspace:${geckoemf.version}
```

You can now activate the library bi adding the following instruction to your workspace (e.g. build.bnd) 



```properties
# If you are brave you can use the develop for the latest and greatest. We are ususally pretty stable
-library: geckoEMF
```

This will include a repository with all required dependencies together with the codegenerator and BND Tools Template for an example Project (klick next in the wizard or you will miss the required template variables).

An example project bnd file can look as follows:

```properties
# sets the usually required buildpath, you can extend it with the normal -buildpath to your liking
-library: enable-emf

# The code generation takes a bit of time and makes the build a bit slower.
# It might be a good idea to put comments around it, when you don't need it
-generate:\
	model/mymodel.genmodel;\
		generate=geckoEMF;\
		genmodel=model/mymodel.genmodel;\
		output=src
# Add this attribute to find some logging information
#		logfile=test.log;\

# always add the model in the same folder in jar as in your project
-includeresource: model=model

Bundle-Version: 1.0.0.SNAPSHOT
```

## Links

* [Documentation](https://github.com/geckoprojects-org/org.gecko.emf)
* [Source Code](https://github.com/geckoprojects-org/org.gecko.emf) (clone with `scm:git:git@github.com:geckoprojects-org/org.gecko.emf.git`)


## Developers

* **Juergen Albert** (jalbert) / [j.albert@data-in-motion.biz](mailto:j.albert@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *architect*, *developer*
* **Mark Hoffmann** (mhoffmann) / [m.hoffmann@data-in-motion.biz](mailto:m.hoffmann@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *developer*, *architect*
* **Stefan Bischof** (bipolis) / [stbischof@bipolis.org](mailto:stbischof@bipolis.org) - *developer*
## Licenses

**EPL 2.0**

## Copyright

Data In Motion Consuling GmbH - All rights reserved

---
Data In Motion Consuling GmbH - [info@data-in-motion.biz](mailto:info@data-in-motion.biz)
