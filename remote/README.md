# ONLYOFFICE Docs integration remote service with Atlassian

Service for integrating document editors into Atlassian cloud products.
This is an intermediate service between Forge Atlassian Applications, Atlassian API and ONLYOFFICE Document Editors.

## Table of Contents
- [About](#about)
- [Technologies](#technologies)

## About

- Key features
  - Ability to open Atlassian documents for viewing or editing.
  - Creating new documents in Atlassian products.
  - Storing information about connections to the demo server of ONLYOFFICE editors

## Supported formats

**For viewing:**
* **WORD**: DOC, DOCM, DOCX, DOT, DOTM, DOTX, EPUB, FB2, FODT, HTM, HTML, HWP, HWPX, MD, MHT, MHTML, ODT, OTT, PAGES, RTF, STW, SXW, TXT, WPS, WPT, XML
* **CELL**: CSV, ET, ETT, FODS, NUMBERS, ODS, OTS, SXC, XLS, XLSM, XLSX, XLT, XLTM, XLTX
* **SLIDE**: DPS, DPT, FODP, KEY, ODG, ODP, OTP, POT, POTM, POTX, PPS, PPSM, PPSX, PPT, PPTM, PPTX, SXI
* **PDF**: DJVU, DOCXF, OFORM, OXPS, PDF, XPS
* **DIAGRAM**: VSDM, VSDX, VSSM, VSSX, VSTM, VSTX

**For editing:**

* **WORD**: DOCM, DOCX, DOTM, DOTX
* **CELL**: XLSB, XLSM, XLSX, XLTM, XLTX
* **SLIDE**: POTM, POTX, PPSM, PPSX, PPTM, PPTX
* **PDF**: PDF

**For editing with possible loss of information:**

* **WORD**: EPUB, FB2, HTML, ODT, OTT, RTF, TXT
* **CELL**: CSV, ODS, OTS
* **SLIDE**: ODP, OTP

## Technologies
List the main technologies and libraries used:
- Java 21
- Spring Boot 3
- Lombok
