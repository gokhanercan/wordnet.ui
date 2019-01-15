package isik.wordnet.ui;

import javax.servlet.annotation.WebServlet;

import WordNet.*;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class WordNetUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        WordNet turkish = new WordNet();
        VerticalLayout wrapper = new VerticalLayout();
        SearchBox searchBox = new SearchBox("Enter Your Word", VaadinIcons.SEARCH);
//        TextField searchBox = new TextField("Enter your word");
//        Button searchBtn = new Button("Search");
//        search.addComponents(searchBox, searchBtn);
        Button searchBtn = searchBox.getButton();

        TextField temp = new TextField();
        Tree<String> tree = new Tree<>();
        TreeData<String> treeData = new TreeData<>();
        TreeDataProvider<String> inMemoryDataProvider = new TreeDataProvider<>(treeData);
        Button btn = new Button();
        btn.addClickListener(e -> {
            temp.setValue("I am clicked1");
            ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchBox.getSearchLiteral());
            System.out.println("Length of results " + results.size());
            for (SynSet result : results) {
                treeData.addItem(null, result.representative());
                treeData.addItem(result.representative(), result.getDefinition());
                //treeData.removeItem("Mercury");
                inMemoryDataProvider.refreshAll();
                temp.setValue(temp.getValue() + result.representative());
            }

        });

        searchBtn.addClickListener(e -> {
            temp.setValue("I am clicked2");
            ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchBox.getSearchLiteral());
            Collection<String> x = turkish.literalList();
            System.out.println("Length of literals" + x.size());
            temp.setValue(temp.getValue()  + " " + searchBox.getSearchLiteral() + " " + results.size());
            for (SynSet result : results) {
                treeData.addItem(null, result.representative());
                treeData.addItem(result.representative(), result.getDefinition());
                //treeData.removeItem("Mercury");
                inMemoryDataProvider.refreshAll();
            }
        });
        /*
        // Couple of childless root items
        treeData.addItem(null, "Mercury");
        treeData.addItem(null, "Venus");

        // Items with hierarchy
        treeData.addItem(null, "Earth");
        treeData.addItem("Earth", "The Moon");
        */
        tree.setDataProvider(inMemoryDataProvider);
        //tree.expand("Earth");

        wrapper.addComponents(searchBox, tree, btn, temp);
        setContent(wrapper);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WordNetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
