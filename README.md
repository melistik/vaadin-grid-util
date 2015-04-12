Vaadin GridUtil
==============

A Toolkit in order to simplify the use of the Grid. Mainly it add's an easy way to set Filters and adds some missing Renders.

![screenshot](assets/screenshot.jpg)

Workflow
========

Add the dependency to your pom the GWT inherits will get automatically added by the maven-vaadin-plugin.

```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>vaadin-grid-util</artifactId>
    <version>${vaadin-grid-util-version}</version>
</dependency>
```

```xml
<inherits name="org.vaadin.sliderpanel.Widgetset" />
```

Details to the addon you can find on [Vaadin](https://vaadin.com/directory#addon/grid-util)

Filtering
========
Design your Grid as usal. In order to assign a Filter HeaderRow go like this:

```java
// init filter this add's a HeaderRow to the given grid
final GridCellFilter filter = new GridCellFilter(grid);
filter.setNumberFilter("id");

// set gender Combo with custom icons
ComboBox genderCombo = filter.setComboBoxFilter("gender", Arrays.asList(Gender.MALE, Gender.FEMALE));
genderCombo.setItemIcon(Gender.MALE, FontAwesome.MALE);
genderCombo.setItemIcon(Gender.FEMALE, FontAwesome.FEMALE);

// simple filters
filter.setTextFilter("name", true, true);
filter.setNumberFilter("bodySize");
filter.setDateFilter("birthday");
filter.setBooleanFilter("onFacebook");
```

The GridCellFilter allows to clear all filters and supports a Listener mode:

```java
// init filter this add's a HeaderRow to the given grid
new Button("clearAllFilters", new Button.ClickListener() {
	@Override
	public void buttonClick(final ClickEvent event) {
		filter.clearAllFilters();
	}
});
// listener's on filter
filter.addCellFilterChangedListener(new CellFilterChangedListener() {
	@Override
	public void changedFilter(final GridCellFilter cellFilter) {
		Notification.show("cellFilter changed " + new Date().toLocaleString(), Type.TRAY_NOTIFICATION);
	}
});
```

Renderer
========
The current version contains a BooleanRenderer that will convert the values into FontAwesome Icons. More Renderer are planned.

Converter
========

In order to reduce lines of code a SimpleStringConverter is introduced:

```java
// shorter Convert version for Object to HTML convertion e.g.
grid.addColumn("country", Country.class)
		.setRenderer(new HtmlRenderer(), new SimpleStringConverter<Country>(Country.class) {
			@Override
			public String convertToPresentation(final Country value, final Class<? extends String> targetType, final Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				return String.format("%s <i>(%d)</i>", value.getName(), value.getPopulation());
			}
		});
		
// old version
grid.addColumn("country", Country.class)
		.setRenderer(new HtmlRenderer(), new Converter<String,Country>() {
			@Override
			public String convertToPresentation(final Country value, final Class<? extends String> targetType, final Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				return String.format("%s <i>(%d)</i>", value.getName(), value.getPopulation());
			}
			@Override
			public Country convertToModel(String value, Class<? extends Country> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				return null;
			}
			@Override
			public Class<Country> getModelType() {
				return Country.class;
			}
			@Override
			public Class<String> getPresentationType() {
				return String.class;
			}
		});
```

The MIT License (MIT)
-------------------------

Copyright (c) 2015 Non-Rocket-Science.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

