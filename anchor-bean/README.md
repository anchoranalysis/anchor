# anchor-bean

A library for defining and loading/writing Anchor-Beans. These beans are the core object-model of many parts of the platform.

## Code Structure

| Package Name | Description |
|--------------|-------------|
| org.anchoranalysis.bean | for some important beans (or abstract base classes) |
| org.anchoranalysis.bean.annotation | annotations which identify and constrain bean properties |
| org.anchoranalysis.bean.error | some exceptions that are thrown when performing bean operations |
| org.anchoranalysis.bean.gui | gui-dialogs that are called by factories (in limited circumstances) |
| org.anchoranalysis.bean.init | scaffolding for beans that need to be initialized with a parameter |
| org.anchoranalysis.bean.init.params | scaffolding for the parameters that can be initialized |
| org.anchoranalysis.bean.init.property | how to initialize particular bean-property types |
| org.anchoranalysis.bean.permute | methods for permuting different values over bean properties |
| org.anchoranalysis.bean.permute.property | how to permute a particular property |
| org.anchoranalysis.bean.permute.setter | how to apply a permutation to a bean |
| org.anchoranalysis.bean.xml | creating beans from xml-definitions |
| org.anchoranalysis.bean.xml.error | exceptions that can be thrown doing the above (and internally due to parsing errors)
| org.anchoranalysis.bean.xml.factory | additional bean factories |

## Wiki

See the [wiki page](https://bitbucket.org/anchorimageanalysis/anchor/wiki/Anchor%20Beans) for an introduction to Anchor Beans.