
The "inspector" will let you visually inspect the view (or node) hierarchy.  Initially only the canvas will be displayed (LzCanvas).  If you click on it, its children will be displayed below it, in an indented list.  You can then click on any of its children to display their children, and so on.

For each item that is displayed, there are a number of attributes and controls displayed (from left to rigtht):

... click on this to output the object to the debugger, you can then click on the blue text in the debugger, and then type: global.foo = _, then you can refer to foo in the debugger (for example, eval foo.datapath)

o/x visible, invisible: click to toggle.  Be careful not to make the canvas!  Since the inspector is a child of the canvas, it will also disappear.

Background color swatch: mouse down to show a color menu, and drag and let go to pick another color.  This allows you to easily see the bounds of a view or choose null to make the view transparent and see underneath it.

x, y, width, height: these numbers will update dynamically. If you click on one of them an editable text field will appear which will allow you to modify the attribute.


To do
-----
- o/x should only be displayed for views (not nodes, like layouts and datapaths)
- option to show only views, on by default?

