package net.senmori.versioning;

import java.util.Locale;
import java.util.Stack;
import net.senmori.versioning.types.IntegerItem;
import net.senmori.versioning.types.ListItem;
import net.senmori.versioning.types.StringItem;

public class ComparableVersion implements Comparable<ComparableVersion> {

    private String value;
    private String canonical;

    private ListItem items;

    private ComparableVersion() {
        throw new RuntimeException( "Cannot instantiate ComparableVersion without a version string" );
    }

    public ComparableVersion(String version) {
        parseVersion( version );
    }

    public String getCanonical() {
        return canonical;
    }

    @Override
    public int compareTo(ComparableVersion o) {
        return items.compareTo( o.items );
    }

    @Override
    public boolean equals(Object other) {
        return ( other instanceof ComparableVersion ) && canonical.equalsIgnoreCase( ( ( ComparableVersion ) other ).canonical );
    }

    @Override
    public int hashCode() {
        return canonical.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    public final void parseVersion(String version) {
        this.value = version;

        items = new ListItem();

        version = version.toLowerCase( Locale.ENGLISH );

        ListItem list = items;

        Stack<Item> stack = new Stack<>();
        stack.push( list );

        boolean isDigit = false;

        int startIndex = 0;

        for ( int i = 0; i < version.length(); i++ ) {
            char c = version.charAt( i );

            if ( c == '.' ) {
                if ( i == startIndex ) {
                    list.add( IntegerItem.ZERO );
                } else {
                    list.add( parseItem( isDigit, version.substring( startIndex, i ) ) );
                }
                startIndex = i + 1;
            } else if ( c == '-' ) {
                if ( i == startIndex ) {
                    list.add( IntegerItem.ZERO );
                } else {
                    list.add( parseItem( isDigit, version.substring( startIndex, i ) ) );
                }
                startIndex = i + 1;

                if ( isDigit ) {
                    list.normalize(); // 1.0-* = 1-*

                    if ( ( i + 1 < version.length() ) && Character.isDigit( version.charAt( i + 1 ) ) ) {
                        // new ListItem only if previous were digits and new char is a digit,
                        // ie need to differentiate only 1.1 from 1-1
                        list.add( list = new ListItem() );

                        stack.push( list );
                    }
                }
            } else if ( Character.isDigit( c ) ) {
                if ( !isDigit && i > startIndex ) {
                    list.add( new StringItem( version.substring( startIndex, i ), true ) );
                    startIndex = i;
                }

                isDigit = true;
            } else {
                if ( isDigit && i > startIndex ) {
                    list.add( parseItem( true, version.substring( startIndex, i ) ) );
                    startIndex = i;
                }

                isDigit = false;
            }
        }

        if ( version.length() > startIndex ) {
            list.add( parseItem( isDigit, version.substring( startIndex ) ) );
        }

        while ( !stack.isEmpty() ) {
            list = ( ListItem ) stack.pop();
            list.normalize();
        }

        canonical = items.toString();
    }

    private static Item parseItem(boolean isDigit, String buf) {
        return isDigit ? new IntegerItem( buf ) : new StringItem( buf, false );
    }
}
