package lanctole.view.panel;

import javax.swing.*;
import javax.swing.text.*;

public class LineBreakerTextPane extends JTextPane {
    public LineBreakerTextPane() {
        setEditorKit(new WrapEditorKit());
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }

    private static class WrapEditorKit extends StyledEditorKit {
        private final ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    private static class WrapColumnFactory implements ViewFactory {
        @Override
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                return switch (kind) {
                    case AbstractDocument.ContentElementName -> new WrapLabelView(elem);
                    case AbstractDocument.ParagraphElementName -> new ParagraphView(elem);
                    case AbstractDocument.SectionElementName -> new BoxView(elem, View.Y_AXIS);
                    case StyleConstants.ComponentElementName -> new ComponentView(elem);
                    case StyleConstants.IconElementName -> new IconView(elem);
                    default -> new LabelView(elem);
                };
            }
            return new LabelView(elem);
        }
    }

    private static class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            return switch (axis) {
                case View.X_AXIS -> 0;
                case View.Y_AXIS -> super.getMinimumSpan(axis);
                default -> throw new IllegalArgumentException("Invalid axis: " + axis);
            };
        }
    }
}
