# Simple Full-text Search Index

## Overview

The goal of this project is to create a simplified implementation of in-memory
full-text search index to demonstrate its principle of work.

## Usage example

* Example in Kotlin:

    ```kotlin
    data class Document(
            @FtsId val id: UUID = UUID.randomUUID(),
            @FtsIndexed val title: String,
            @FtsIndexed val description: String?
    )
    
    object Application {
  
        @JvmStatic fun main(args: Array<String>) {
            val fts = FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>()
        
            fts.add(Document(
                    title = "Document #11",
                    description = "Description of document number eleven"))
            fts.add(Document(
                    title = "Document #12",
                    description = "Description of document number twelve"))
                    
            fts.search("eleven")            // returns [Document #11]
            fts.search("docuemnt twelve")   // returns [Document #12, Document #11]
            fts.search("docuemnt eleven")   // returns [Document #11, Document #12]
        }
      
    }
    ```
        
                
* Example in Java:
    
    _Java is not supported yet._