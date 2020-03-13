package net.senmori.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class PasteUtil
{
    public static StringSelection copyStringToClipboard(String selection) {
        StringSelection sel = new StringSelection( selection );
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( sel, sel );
        return sel;
    }
}
