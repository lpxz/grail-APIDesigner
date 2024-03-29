// ParseState.java
// $Id: ParseState.java,v 1.1 2010/06/15 12:27:43 smhuang Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.webdav;

class ParseState {
    int ioff = -1;		// input offset
    int ooff = -1;		// output ofset (where parsing should continue)
    int start = -1 ;		// Start of parsed item (if needed)
    int end = -1;		// End of parsed item (if needed)
    int bufend = -1 ;		// End of the buffer to parse

    boolean isSkipable = true ;	// Always skip space when this make sense
    boolean isQuotable = true ; // Support quted string while parsing next item
    boolean spaceIsSep = true;

    byte separator = (byte) ','; 	// Separator for parsing list

    final void prepare() {
	ioff  = ooff;
	start = -1;
	end   = -1;
    }

    final void prepare(ParseState ps) {
	this.ioff   = ps.start;
	this.bufend = ps.end;
    }

    final String toString(byte raw[]) {
	return new String(raw, 0, start, end-start);
    }

    final String toString(byte raw[], boolean lower) {
	if ( lower ) {
	    // To lower case:
	    for (int i = start; i < end ; i++)
		raw[i] = (((raw[i] >= 'A') && (raw[i] <= 'Z'))
			  ? (byte) (raw[i] - 'A' + 'a')
			  : raw[i]);
	} else {
	    // To upper case:
	    for (int i = start; i < end ; i++)
		raw[i] = (((raw[i] >= 'a') && (raw[i] <= 'z'))
			  ? (byte) (raw[i] - 'a' + 'A')
			  : raw[i]);
	}
	return new String(raw, 0, start, end-start);
    }

    ParseState(int ioff) {
	this.ioff = ioff;
    }

    ParseState(int ioff, int bufend) {
	this.ioff   = ioff;
	this.bufend = bufend;
    }

    ParseState() {
    }

}


