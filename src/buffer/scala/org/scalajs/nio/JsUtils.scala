package org.scalajs.nio

import scala.scalajs.js
import js.Dynamic.{ global => g }

object JsUtils {
  private val typeRegex = js.Dynamic.newInstance(g.RegExp)("^\\[object\\s(.*)\\]$")
  
  /*
   * Return the type of the JavaScript object as a String. Examples:
   * 1.5 -> Number
   * true -> Boolean
   * "Hello" -> String
   * null -> Null
   */
  def typeName(jsObj: js.Any): String = {
    val fullName = g.Object.prototype.selectDynamic("toString").call(jsObj).asInstanceOf[js.String]
    val execArray = typeRegex.exec(fullName).asInstanceOf[js.Array[js.String]]
    val name = execArray(1)
    name
  }
}