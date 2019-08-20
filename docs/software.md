# Software Developer's View

## Building
1. git clone https://github.com/svanteschubert/en16931-data-extractor.git
2. Call 'mvn install' in the root directory of the project once, to build the [ODF reader (ODFDOM library)](https://github.com/svanteschubert/odftoolkit/tree/odf-changes/odfdom)</br>
   NOTE: This ODF library will become the 1.0.0 version of the [ODF Toolkit from "The Document Foundation"](https://github.com/tdf/odftoolkit), but copied as yet no Maven artifacts are available for download.

## Running as Test (e.g. debug)
1. Create a test document by saving either the UBL or UN/CEFACT 16931-3 specification from original DOCX to OpenDocument Text format (ODT) format into the folder en16931-3-reader/src/test/resources
2. Provide its name in the test file: en16931-3-reader/src/test/java/de/prototypefund/en16931/ExtractionTest.java
3. Call via command line 'mvn install' in the 'en16931-3-reader' folder (or use an IDE like [Netbeans](https://netbeans.apache.org/download/), [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)).</br>
   You may also find the new JAR in the "target" directory and use it as explained to the "technical writer" before.

## Architecture
The Data Extractor for the European e-Invoice Specification (en16931) exist of two modules:
1. The [ODFDOM library](https://tdf.github.io/odftoolkit/docs/odfdom/), which unzips the OpenDocument Text document, access the XML, i.e. the content.xml file.
2. The EN16931 reader, which uses ODFDOM to find the tables an get accesss to the data of every cell of each table row.


