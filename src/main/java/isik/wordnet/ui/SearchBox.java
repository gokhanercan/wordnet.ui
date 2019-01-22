package isik.wordnet.ui;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

class SearchBox extends CssLayout {

    private final TextField textField;
    private final Button button;

    SearchBox(String caption, Resource icon) {
        setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        setCaption(caption);
        textField = new TextField();
        textField.setWidth(100, Unit.PERCENTAGE);

        button = new Button(icon);
        button.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
//        button.addClickListener(listener);

        addComponents(textField, button);
    }

    Button getButton() {
        return button;
    }

    String getSearchLiteral() {
        return textField.getValue().trim();
    }

    void setSearchLiteral(String literal) {
        textField.setValue(literal);
    }
}