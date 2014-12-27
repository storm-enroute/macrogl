package org.macrogl.util



object TextResources {
  def get(resourcesName: String*) (callback: Array[TextFileContent] => Unit): Unit = {
    val size = resourcesName.length
    
    val filesReady: Array[Boolean] = new Array[Boolean](size)
    val filesData: Array[TextFileContent] = new Array[TextFileContent](size)
    
    var done = false
    
    var i = 0
    while(i < size) {
      filesReady(i) = false
      i += 1
    }
    
    def checkReady(): Unit = {
      var ready = true
      
      var i = 0
      while(i < size) {
        if(filesReady(i) == false) {
          ready = false
        }
        i += 1
      }
      
      if(ready && !done) {
        done = true // Just to make sure the callback isn't triggered twice
        callback(filesData)
      }
    }
    
    i = 0
    while(i < size) {
      val curIndex = i
      
      org.macrogl.Utils.getTextFileFromResources(resourcesName(curIndex)) { lines =>
        filesData(curIndex) = lines
        filesReady(curIndex) = true
        
        checkReady()
      }
      
      i += 1
    }
  }
}
