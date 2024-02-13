## Overview

This project is a fork of [wooio/htmltopdf-java](https://github.com/wooio/htmltopdf-java) and is based on [wkhtmltopdf](https://github.com/wkhtmltopdf/wkhtmltopdf), which converts HTML documents to PDF.
Access to wkhtmltopdf is performed via JNA, exposed through a Java-friendly layer.

This fork:
- Upgrades wkhtmltopdf version to 0.12.6
- adds ARM support for Linux
- Upgrades to JNA 5.12.1 as previous versions could not detect aarch64
- drops 32-bit support entirely

## Get it

Gradle:
```groovy
compile 'io.woo:htmltopdf:1.0.10.0'
```

Maven:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.MagentaHealth</groupId>
  <artifactId>htmltopdf-java</artifactId>
  <version>1.0.10.0</version>
</dependency>
```

## Getting started

The following examples should be sufficient to get you started, however there
are many more options discoverable by looking into the methods of `HtmlToPdf` and `HtmlToPdfObject`.

#### Saving HTML as a PDF file

```java
boolean success = HtmlToPdf.create()
    .object(HtmlToPdfObject.forHtml("<p><em>Apples</em>, not oranges</p>"))
    .convert("/path/to/file.pdf");
```

#### Saving a webpage from URL as a PDF file

```java
boolean success = HtmlToPdf.create()
    .object(HtmlToPdfObject.forUrl("https://github.com/wooio/htmltopdf-java"))
    .convert("/path/to/file.pdf");
```

#### Saving multiple objects as a PDF file

```java
boolean success = HtmlToPdf.create()
    .object(HtmlToPdfObject.forUrl("https://github.com/wooio/htmltopdf-java"))
    .object(HtmlToPdfObject.forHtml("<p>This is the second object...</p>"))
    // ...
    .convert("/path/to/file.pdf");
```

#### Converting to InputStream (instead of saving as file)

Converting to an InputStream would be useful if you intend on returning the resulting PDF document 
as an HTTP response or adding it as an email attachment

```java
HtmlToPdf htmlToPdf = HtmlToPdf.create()
    // ...
    .object(HtmlToPdfObject.forUrl("https://github.com/wooio/htmltopdf-java"));

try (InputStream in = htmlToPdf.convert()) {
    // "in" has PDF bytes loaded
} catch (HtmlToPdfException e) {
    // HtmlToPdfException is a RuntimeException, thus you are not required to
    // catch it in this scope. It is thrown when the conversion fails
    // for any reason.
}
```

## Concurrency limitations

While the library is thread-safe, it unfortunately cannot perform conversions concurrently.
Because wkhtmltopdf uses Qt behind the scenes to render webpages,
there is a single thread which performs such rendering across a single process. Therefore, at this point, it is only 
possible to perform one conversion at the same time per process.

## Troubleshooting

#### Missing native dependencies
If you get the following exception:
```
java.lang.UnsatisfiedLinkError: Unable to load library '/tmp/io.woo.htmltopdf/wkhtmltox/0.12.6/libwkhtmltox.amd64.so': Native library (tmp/io.woo.htmltopdf/wkhtmltox/0.12.6/libwkhtmltox.amd64.so) not found in resource path
```
Then that likely means that one of the native dependencies of wkhtmltopdf is not met.
It might be worth checking that the following packages are installed:

- libc6 (or glibc)
- libx11
- libxext
- libxrender
- libstdc++
- libssl1.0
- freetype
- fontconfig

## Developer Notes

Useful commands when trying to upgrade library files in `src/main/resources/wkhtmltox`:

### How to open a deb file example

```
mkdir -p wkhtmltox_0.12.6.1-2.bullseye_arm64 && tar xf wkhtmltox_0.12.6.1-2.bullseye_arm64.deb -C wkhtmltox_0.12.6.1-2.bullseye_arm64
cd wkhtmltox_0.12.6.1-2.bullseye_arm64
mkdir -p data && tar xf data.tar.xz -C data
```

### How to open a pkg file example

```
pkgutil --expand wkhtmltox-0.12.6-2.macos-cocoa.pkg wkhtmltox-0.12.6-2.macos-cocoa
cd wkhtmltox-0.12.6-2.macos-cocoa
tar xf Payload
cd usr/local/share/wkhtmltox-installer
tar xf wkhtmltox.tar.gz
```
