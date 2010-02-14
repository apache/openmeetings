//* A_LZ_COPYRIGHT_BEGIN ******************************************************
//* Copyright 2009 Laszlo Systems, Inc.  All Rights Reserved.                 *
//* Use is subject to license terms.                                          *
//* A_LZ_COPYRIGHT_END ********************************************************

/* W3 DOM Document API.
 * This implements a little bit of the W3 DOM ECMAScript API, for convenience
 * when working with LZX.
 *
 * Usage:
 *   <script src="incubator/domapi.js"/>
 * 
 * These functions are currently implemented, with
 * the same semantics as in ECMAScript:
 * 
 * var document; // a synonym for canvas
 * document.getElementById();
 * LzNode.createElement(name, attributes); // see the note in the source
 * LzNode.getChildNodes();
 * LzNode.removeChild();
 *
 * For compatability with the prototype.js library:
 * function $(name); // => LzNode
 * function $(n1, n2, ...); // => Array of LzNode
 */

var document = canvas;

/* The W3 DOM API has a function document.createElement(name), which
 * creates an element that can then be attached to the document
 * through an API call such as node.appendChild.
 *
 * Due to technical limitations, it is non-trivial to implement this
 * for LZX.  Instead, this function creates a node in situ, as a
 * child of the receiver.
 *
 * Example: myview.createElement('button', {text: 'label'})
 *
 * Note that the auto-include mechanism will not see reference to
 * the button class, above.  You will need to use <include> to
 * include the class (in this case button) explicitly, if it is
 * not otherwise used as a tag in the program source.
 *
 * @param String|Class name: a class, or a string that names one
 * @param Object attributes: a hash of initialization attributes
 * @return LzNode
 */
LzNode.prototype.createElement = function (name, attributes) {
  if (typeof name == 'string')
    return new lz.eval(name)(this, attributes);
  else
    return new name(this, attributes);
}

/* @param String id
 * @return LzNode or nil
 */
LzCanvas.prototype.getElementById = function (id) {
  // LZX doesn't keep an index of nodes by id.
  // Instead, it just binds the global variable with the id name to
  // the node.  So first, evaluate the string.
  var e = lz.eval(id);
  // This filters globals such as "var name = {id: 'name'}"
  if (!(e instanceof lz.node)) return null;
  // This filters globals such as "var name = <<expr>>", where
  // expression retrieves a node from the canvas hierarchy.
  if (e['id'] != id) return null;
  // This filters nodes that have an id, but haven't been
  // attached to the document hierarchy.
  // This case is disabled because there doesn't seem to be
  // any way to create a node that isn't in the document
  // hierarchy.
  //var p = e;
  //while (p && p != canvas) p = p.parent;
  //if (p != canvas) return null;
  // Whew!
  return e;
}

/* @return Array of LzNode
 */
LzNode.prototype.getChildNodes = function () {
  return this.subnodes;
}

/* @param LzNode child
 */
LzNode.prototype.removeChild = function (child) {
  if ($debug) {
    if (child.parent != this) {
      Debug.error("removeChild: " + child + " is not a child of " + this);
      return;
    }
  }
  child.destroy();
}

/* The LZX version of the function from the prototype.js library.
 * @param variable number of String or LzNode
 * @return Array of LzNode.  If the argument list is a singleton,
 * a singleton is returned.
 */
function $() {
  var elements = [];
  // AVM 'for...each' iterates backwards, so use this instead
  for (var i = 0; i < arguments.length; i++) {
    var element = arguments[i];
    if (typeof element == 'string') element = canvas.getElementById(element);
    elements.push(element);
  }
  if (arguments.length == 1) return elements[0];
  return elements;
}
