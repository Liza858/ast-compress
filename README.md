# Description

This application can compress Java code trees.
The path to the directory with the project is passed as a command line argument.
The program finds files with the java extension and compresses the trees of the code.

# How to execute

### There are two ways to run it in command line:

+ ./gradlew run --args="pathToProject numberOfCompressions directoryToSave fileName"

    where:
    
    * pathToProject - the path to the project where you want to compress the syntax trees of the source code
    
    * numberOfCompressions - number of compressions >= 0
    
    * directoryToSave - path to the directory where you want to save the compressed trees
    
    * fileName - the filename (relative to the directoryToSave) where you want to save the compressed trees


* ./gradlew run --args="pathToCompressedTreesFile"

  where:
    
    * pathToCompressedTreesFile - path to the file where the compressed trees are stored
    
    This command prints trees to the screen