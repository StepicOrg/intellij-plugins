package org.stepik.plugin.projectWizard.ui;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;

import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author meanmail
 */
class CourseListBoxEditor extends BasicComboBoxEditor {
    private CourseListModel model;
    private CourseListBox owner;

    CourseListBoxEditor() {
        editor.addKeyListener(new OwnerKeyListener());
    }

    void setModel(@NotNull CourseListModel model) {
        this.model = model;
    }

    void setOwner(CourseListBox owner) {
        this.owner = owner;
    }

    private class OwnerKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String text = editor.getText();
                model.setSelectedItem(text);
                int caretPos = text.length();
                editor.select(caretPos, caretPos);
                owner.hidePopup();
                e.consume();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            char keyChar = e.getKeyChar();
            if (keyChar < KeyEvent.VK_SPACE || keyChar == KeyEvent.VK_DELETE) {
                return;
            }

            String text = editor.getText();
            String inputtedText;
            int caretPos = editor.getSelectionStart();
            if (editor.getSelectionStart() != editor.getSelectionEnd()) {
                inputtedText = text.substring(0, caretPos);
            } else {
                if (editor.getSelectionEnd() != text.length()) {
                    return;
                }
                inputtedText = text;
            }

            final String finalInputtedText = inputtedText.toLowerCase();

            List<StudyObject> candidates = model.getCourses().stream()
                    .filter(course -> course.getTitle().toLowerCase().startsWith(finalInputtedText))
                    .sorted(Comparator.comparingInt(o -> o.getTitle().length()))
                    .limit(1)
                    .collect(Collectors.toList());

            if (candidates.size() > 0) {
                StudyObject candidate = candidates.get(0);
                model.setSelectedItem(candidate);
                String newText = candidate.getTitle();
                editor.setText(newText);
                editor.setSelectionStart(caretPos);
                editor.setSelectionEnd(newText.length());
            }
        }
    }
}
