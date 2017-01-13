package org.stepik.plugin.projectWizard.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.HyperlinkAdapter;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

/**
 * @author meanmail
 */
public class CourseDescriptionPane extends JTextPane {
    public CourseDescriptionPane() {
        setEditable(false);
        setContentType("text/html");
        addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            protected void hyperlinkActivated(final HyperlinkEvent e) {
                BrowserUtil.browse(e.getURL());
            }
        });
    }
}
