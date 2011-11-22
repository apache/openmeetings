import org.xml.sax.*;
import java.util.*;
import java.io.File;
import javax.xml.parsers.SAXParserFactory;

/**
* DTDGenerator<BR>
* Generates a possible DTD from an XML document instance.
* Pure SAX version of the Saxon DTDGenerator
* The program has no remaining dependencies on Saxon; all it needs is:
*    JAXP 1.1
*    SAX2
*    A JAXP 1.1 conformant XML parser
*    Java 1.2
* @author M.H.Kay
* @version 7.0: separated from Saxon source, now works with any JAXP 1.1 XML parser 
*/

public class DTDGenerator extends org.xml.sax.helpers.DefaultHandler {
                                // DTDSAXGen is a ContentHandler, created for convenience
                                // by extending the default handler that comes with SAX2

    protected static int MIN_ENUMERATION_INSTANCES = 10;   
                                // minimum number of appearances of an attribute for
                                // it to be considered a candidate for an enumeration type
    
    protected static int MAX_ENUMERATION_VALUES = 20;   
                                // maximum number of distinct attribute values to be 
                                // included in an enumeration

    protected static int MIN_ENUMERATION_RATIO = 3;   
                                // an attribute will be regarded as an enumeration attribute
                                // only if the number of instances divided by the number of
                                // distinct values is >= this ratio
                                
    protected static int MIN_FIXED = 5;
                                // minimum number of attributes that must appear, with
                                // the same value each time, for the value to be regarded
                                // as FIXED                                
                                
    protected static int MIN_ID_VALUES = 10;     
                                // minumum number of attribute values that must appear
                                // for the attribute to be regarded as an ID value

    protected static int MAX_ID_VALUES = 100000; 
                                // maximum number of attribute values to be saved
                                // while checking for uniqueness

    TreeMap elementList;   // alphabetical list of element types appearing in the document;
                           // each has the element name as a key and an ElementDetails object
                           // as the value

    Stack elementStack;    // stack of elements currently open; each entry is a StackEntry
                           // object    

    /**
    * Entry point
    * Usage:  java DTDSAXGen input-file >output-file
    */

    public static void main (String args[]) throws java.lang.Exception
    {
				// Check the command-line arguments.

                // Instantiate and run the application
        DTDGenerator app = new DTDGenerator();

        app.run("library.lzx");
        app.printDTD();
    }

    public DTDGenerator () 
    {
        elementList = new TreeMap();
        elementStack = new Stack();
    }

    private void run(String filename)  {
        try {
            InputSource is = new InputSource(new File(filename).toURL().toString());
            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            parser.setContentHandler(this);
            parser.parse(is);
        } catch (java.io.FileNotFoundException nf) {
            System.err.println("File " + filename + " not found");
        } catch (Exception err) {
            System.err.println("Failed while parsing source file");
            System.err.println(err.getMessage());
            err.printStackTrace();
            System.exit(2);
        }
    }
        

    /**
    * Test whether a string is an XML name.
    * TODO: This is currently an incomplete test, it treats all non-ASCII characters
    * as being valid in names.
    */

    private boolean isValidName(String s) {
        if (!isValidNMTOKEN(s)) return false;
        int c = s.charAt(0);
        return ! ((c>=0x30 && c<=0x39) || c=='.' || c=='-' );
    }

    /**
    * Test whether a string is an XML NMTOKEN.
    * TODO: This is currently an incomplete test, it treats all non-ASCII characters
    * as being valid in NMTOKENs.
    */

    private boolean isValidNMTOKEN(String s) {
        if (s.length()==0) return false;
        for (int i=0; i<s.length(); i++) {
            int c = s.charAt(i);
            if (!( (c>=0x41 && c<=0x5a) ||
                   (c>=0x61 && c<=0x7a) ||
                   (c>=0x30 && c<=0x39) ||
                    c=='.' ||
                    c=='_' ||
                    c=='-' ||
                    c==':' ||
                    c>128 ))
                return false;
        }
        return true;
    }
  
    /**
    * When the whole document has been analysed, construct the DTD
    */
    
    private void printDTD ()
    {
        // process the element types encountered, in turn

        Iterator e=elementList.keySet().iterator();
        while ( e.hasNext() )
        {
            String elementname = (String) e.next();
            ElementDetails ed = (ElementDetails) elementList.get(elementname); 
            TreeMap children = ed.children;
            Set childKeys = children.keySet();

            //EMPTY content
            if (childKeys.size()==0 && !ed.hasCharacterContent) 
                System.out.print("<!ELEMENT " + elementname + " EMPTY >\n");

            //CHARACTER content
            if (childKeys.size()==0 && ed.hasCharacterContent)
                System.out.print("<!ELEMENT " + elementname + " ( #PCDATA ) >\n");

            //ELEMENT content
            if (childKeys.size()>0 && !ed.hasCharacterContent) {
                System.out.print("<!ELEMENT " + elementname + " ( ");

                if (ed.sequenced) {
                    
                    // all elements of this type have the same child elements
                    // in the same sequence, retained in the childseq vector
                    
                    Enumeration c = ed.childseq.elements();
                    while (true) {
                        ChildDetails ch = (ChildDetails)c.nextElement();
                        System.out.print(ch.name);
                        if (ch.repeatable && !ch.optional) 
                            System.out.print("+");
                        if (ch.repeatable && ch.optional) 
                            System.out.print("*");
                        if (ch.optional && !ch.repeatable) 
                            System.out.print("?");
                        if (c.hasMoreElements())
                            System.out.print(", ");
                        else
                            break;
                    }
                    System.out.print(" ) >\n");
                }
                else {
                    
                    // the children don't always appear in the same sequence; so
                    // list them alphabetically and allow them to be in any order
                    
                    Iterator c1 = childKeys.iterator();
                    while (c1.hasNext()) {
                        System.out.print((String)c1.next());
                        if (c1.hasNext()) System.out.print(" | ");
                    }
                    System.out.print(" )* >\n");
                }
            };

            //MIXED content
            if (childKeys.size()>0 && ed.hasCharacterContent) {
                System.out.print("<!ELEMENT " + elementname + " ( #PCDATA");
                Iterator c2 = childKeys.iterator();
                while (c2.hasNext()) {
                    System.out.print(" | " + (String)c2.next());
                }
                System.out.print(" )* >\n");
            };

            // Now examine the attributes encountered for this element type

            TreeMap attlist = ed.attributes;
            boolean doneID = false;       // to ensure we have at most one ID attribute per element
            Iterator a=attlist.keySet().iterator();
            while ( a.hasNext() )
            {
                String attname = (String) a.next();
                AttributeDetails ad = (AttributeDetails) attlist.get(attname);

                // If the attribute is present on every instance of the element, treat it as required
                boolean required = (ad.occurrences==ed.occurrences);

                // If every value of the attribute is distinct, 
                // and there are at least MIN_ID_VALUES, treat it as an ID
                // TODO: this may give the wrong answer, we should check whether the value sets of two
                // candidate-ID attributes overlap, in which case they can't both be IDs !!)
                boolean isid = ad.allNames &&           // ID values must be Names
                                (!doneID) &&            // Only allowed one ID attribute per element type
                                (ad.unique) &&
                                (ad.occurrences>=MIN_ID_VALUES);

                // if there is only one attribute value, and at least MIN_FIXED occurrences of it,
                // treat it as FIXED 
                boolean isfixed = required && ad.values.size()==1 && ad.occurrences >= MIN_FIXED;

                // if the number of distinct values is small compared with the number of occurrences,
                // treat it as an enumeration
                boolean isenum = ad.allNMTOKENs &&      // Enumeration values must be NMTOKENs
                                (ad.occurrences>=MIN_ENUMERATION_INSTANCES) && 
                                (ad.values.size()<=ad.occurrences/MIN_ENUMERATION_RATIO) &&
                                (ad.values.size()<=MAX_ENUMERATION_VALUES);

                System.out.print("<!ATTLIST " + elementname + " " + attname + " ");
                String tokentype = (ad.allNMTOKENs ? "NMTOKEN" : "CDATA");
                
                if (isid) { 
                    System.out.print("ID");
                    doneID = true;
                }
                else if (isfixed) {
                    String val = (String) ad.values.first();                        
                    System.out.print(tokentype + " #FIXED \"" + escape(val) + "\" >\n");
                }
                else if (isenum) {
                    System.out.print("( ");
                    Iterator v = ad.values.iterator();
                    while (v.hasNext()) {
                        System.out.print((String) v.next());
                        if (!v.hasNext()) break;
                        System.out.print(" | ");
                    };
                    System.out.print(" )");
                }
                else
                    System.out.print(tokentype);

                if (!isfixed) {
                    if (required)
                        System.out.print(" #REQUIRED >\n");
                    else
                        System.out.print(" #IMPLIED >\n");
                }
            };
            System.out.print("\n");
        };
   
    }
    

    /**
    * Escape special characters for display.
    * @param ch The character array containing the string
    * @param start The start position of the input string within the character array
    * @param length The length of the input string within the character array
    * @return The XML/HTML representation of the string<br>
    * This static method converts a Unicode string to a string containing
    * only ASCII characters, in which non-ASCII characters are represented
    * by the usual XML/HTML escape conventions (for example, "&lt;" becomes "&amp;lt;").
    * Note: if the input consists solely of ASCII or Latin-1 characters,
    * the output will be equally valid in XML and HTML. Otherwise it will be valid
    * only in XML.
    * The escaped characters are written to the dest array starting at position 0; the
    * number of positions used is returned as the result
    */
    
    private static int escape(char ch[], int start, int length, char[] out)
    {        
        int o = 0;
        for (int i = start; i < start+length; i++) {
            if (ch[i]=='<') {("&lt;").getChars(0,4,out,o); o+=4;}
            else if (ch[i]=='>') {("&gt;").getChars(0,4,out,o); o+=4;}
            else if (ch[i]=='&') {("&amp;").getChars(0,5,out,o); o+=5;}
            else if (ch[i]=='\"') {("&#34;").getChars(0,5,out,o); o+=5;}
            else if (ch[i]=='\'') {("&#39;").getChars(0,5,out,o); o+=5;}
            else if (ch[i]<=0x7f) {out[o++]=ch[i];}
            else {
                String dec = "&#" + Integer.toString((int)ch[i]) + ';';
                dec.getChars(0, dec.length(), out, o);
                o+=dec.length();
            }            
        }
        return o;
    }

    /**
    * Escape special characters in a String value.
    * @param in The input string
    * @return The XML representation of the string<br>
    * This static method converts a Unicode string to a string containing
    * only ASCII characters, in which non-ASCII characters are represented
    * by the usual XML/HTML escape conventions (for example, "&lt;" becomes
    * "&amp;lt;").<br>
    * Note: if the input consists solely of ASCII or Latin-1 characters,
    * the output will be equally valid in XML and HTML. Otherwise it will be valid
    * only in XML.
    */
    
    private static String escape(String in)
    {
        char[] dest = new char[in.length()*8];
        int newlen = escape( in.toCharArray(), 0, in.length(), dest);
        return new String(dest, 0, newlen);
    }
       
    /**
    * Handle the start of an element. Record information about the position of this
    * element relative to its parent, and about the attributes of the element. 
    */
    
    public void startElement (String uri, String localName, String name, Attributes attributes)
	throws SAXException
    {
        StackEntry se = new StackEntry();

        // create an entry in the Element List, or locate the existing entry        
        ElementDetails ed = (ElementDetails) elementList.get(name);
        if (ed==null)  { 
            ed = new ElementDetails(name);
            elementList.put(name,ed);
        };

        // retain the associated element details object
        se.elementDetails = ed;

        // initialise sequence numbering of child element types
        se.sequenceNumber = -1;
        
        // count occurrences of this element type
        ed.occurrences++;

        // Handle the attributes accumulated for this element.
        // Merge the new attribute list into the existing list for the element

        for (int a=0; a<attributes.getLength(); a++) {
            String attName = attributes.getQName(a);
            String val = attributes.getValue(a);
 
            AttributeDetails ad = (AttributeDetails) ed.attributes.get(attName);
            if (ad==null) {
               ad=new AttributeDetails(attName);
               ed.attributes.put(attName, ad);
            };
            
            if (!ad.values.contains(val)) {
                
                // We haven't seen this attribute value before
                                  
                ad.values.add(val);
                
                // Check if attribute value is a valid name
                if (ad.allNames && !isValidName(val)) {
                    ad.allNames = false;     
                }
                
                // Check if attribute value is a valid NMTOKEN
                if (ad.allNMTOKENs && !isValidNMTOKEN(val)) {
                    ad.allNMTOKENs = false;
                }

                // For economy, don't save the new value unless it's needed;
                // it's needed only if we're looking for ID values or enumerated values

                if (ad.unique && ad.allNames && ad.occurrences <= MAX_ID_VALUES) {
                    ad.values.add(val);
                } else if (ad.values.size() <= MAX_ENUMERATION_VALUES) {
                    ad.values.add(val);
                }
                
            } else {
                // We've seen this attribute value before
                ad.unique = false;
            }
            ad.occurrences++;
        };

        // now keep track of the nesting and sequencing of child elements
        if (!elementStack.isEmpty()) {
            StackEntry parent = (StackEntry)elementStack.peek();
            ElementDetails parentDetails = parent.elementDetails;
            int seq = parent.sequenceNumber;

            // for sequencing, we're interested in consecutive groups of the same child element type
            boolean isFirstInGroup = (parent.latestChild==null || (!parent.latestChild.equals(name)));
            if (isFirstInGroup) {
                seq++;
                parent.sequenceNumber++;
            }
            parent.latestChild = name;

            // if we've seen this child of this parent before, get the details
            TreeMap children = parentDetails.children;
            ChildDetails c = (ChildDetails)children.get(name);
            if (c==null) {
                // this is the first time we've seen this child belonging to this parent
                c = new ChildDetails();
                c.name = name;
                c.position = seq;
                c.repeatable = false;
                c.optional = false;
                children.put(name, c);
                parentDetails.childseq.addElement(c);

                // if the first time we see this child is not on the first instance of the parent,
                // then we allow it as an optional element
                if (parentDetails.occurrences!=1) {
                    c.optional = true;
                }

            } else {

                // if it's the first occurrence of the parent element, and we've seen this
                // child before, and it's the first of a new group, then the child occurrences are
                // not consecutive
                if (parentDetails.occurrences==1 && isFirstInGroup) {
                    parentDetails.sequenced = false;
                }
                
                // check whether the position of this group of children in this parent element is
                // the same as its position in previous instances of the parent.
                if (parentDetails.childseq.size()<=seq ||
                        !((ChildDetails)parentDetails.childseq.elementAt(seq)).name.equals(name))
                {
                    parentDetails.sequenced = false;
                }
            }

            // if there's more than one child element, mark it as repeatable
            if (!isFirstInGroup) {
                c.repeatable = true;
            }
        }
        elementStack.push(se);
    }

    /**
    * End of element. If sequenced, check that all expected children are accounted for.
    */

    public void endElement (String uri, String localName, String name)
	throws SAXException
    {

        // If the number of child element groups in this parent element is less than the
        // number in previous elements, then the absent children are marked as optional
        ElementDetails ed = (ElementDetails) elementList.get(name);
        if (ed.sequenced) {
            StackEntry se = (StackEntry)elementStack.peek();
            int seq = se.sequenceNumber;
            for (int i=seq+1; i<ed.childseq.size(); i++) {
                ((ChildDetails)ed.childseq.elementAt(i)).optional = true;
            }
        }
        elementStack.pop();
    }
    
    /**
    * Handle character data.
    * Make a note whether significant character data is found in the element
    */

    public void characters (char ch[], int start, int length)
	throws SAXException
    {
        ElementDetails ed = ((StackEntry)elementStack.peek()).elementDetails;
        if (!ed.hasCharacterContent) {
            for (int i=start; i<start+length; i++) {
                if ((int)ch[i] > 0x20) {
                    ed.hasCharacterContent = true;
                    break;
                }
            }
        }
    }

    /**
    * ElementDetails is a data structure to keep information about element types
    */

    private class ElementDetails {
        String name;
        int occurrences;
        boolean hasCharacterContent;
        boolean sequenced;
        TreeMap children;
        Vector childseq;
        TreeMap attributes;

        public ElementDetails ( String name ) {
            this.name = name;
            this.occurrences = 0;
            this.hasCharacterContent = false;
            this.sequenced = true;
            this.children = new TreeMap();
            this.childseq = new Vector();
            this.attributes = new TreeMap();
        }
    }

    /**
    * ChildDetails records information about the presence of a child element within its
    * parent element. If the parent element is sequenced, then the child elements always
    * occur in sequence with the given frequency.
    */

    private class ChildDetails {
        String name;
        int position;
        boolean repeatable;
        boolean optional;
    }
    

    /**
    * AttributeDetails is a data structure to keep information about attribute types
    */

    private class AttributeDetails {
        String name;            // name of the attribute
        int occurrences;        // number of occurrences of the attribute
        boolean unique;         // true if no duplicate values encountered
        TreeSet values;         // set of all distinct values encountered for this attribute 
        boolean allNames;       // true if all the attribute values are valid names
        boolean allNMTOKENs;    // true if all the attribute values are valid NMTOKENs

        public AttributeDetails ( String name ) {
            this.name = name;
            this.occurrences = 0;
            this.unique = true;
            this.values = new TreeSet();
            this.allNames = true;
            this.allNMTOKENs = true;
        }
    }
    
    /**
    * StackEntry is a data structure we put on the stack for each nested element
    */
    
    private class StackEntry {
        ElementDetails elementDetails;
        int sequenceNumber;
        String latestChild;
    }


} // end of outer class DTDSAXGen

