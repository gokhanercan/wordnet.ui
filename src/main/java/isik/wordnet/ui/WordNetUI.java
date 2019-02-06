package isik.wordnet.ui;

import WordNet.WordNet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;
import java.util.HashSet;
import java.util.Set;

import static isik.wordnet.ui.Utils.searchInWordnet;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SuppressWarnings("unchecked")
@Theme("mytheme")
public class WordNetUI extends UI {

    static WordNet turkish, english;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        turkish = WordNetService.getTurkishWordNet();
        english = WordNetService.getEnglishWordNet();
        VerticalLayout wrapper = new VerticalLayout();
        SearchBox searchBox = new SearchBox("Enter Your Word", VaadinIcons.SEARCH);
        Button searchBtn = searchBox.getButton();

        Tree<TreeItem> tree = new Tree<>();
        TreeData<TreeItem> treeData = new TreeData<>();
        TreeDataProvider<TreeItem> inMemoryDataProvider = new TreeDataProvider<>(treeData);
        tree.setDataProvider(inMemoryDataProvider);
        Set<String> turkishLeaves = new HashSet<>();
        Set<String> englishLeaves = new HashSet<>();

        searchBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String searchLiteral = searchBox.getSearchLiteral();
                searchInWordnet(searchLiteral, treeData, inMemoryDataProvider, turkishLeaves, englishLeaves);
                searchBox.setSearchLiteral(searchLiteral);
            }
        });

        tree.addItemClickListener(new Tree.ItemClickListener() {
            @Override
            public void itemClick(Tree.ItemClick itemClick) {
                String searchLiteral = itemClick.getItem().toString();
                if (turkishLeaves.contains(searchLiteral)) {
                    searchLiteral = searchLiteral.split("-")[0];
                    searchInWordnet(searchLiteral, treeData, inMemoryDataProvider, turkishLeaves, englishLeaves);
                    searchBox.setSearchLiteral(searchLiteral);
                } else if (englishLeaves.contains(searchLiteral)) {
                    String baseURL = "http://wordnetweb.princeton.edu/perl/webwn?s=";
                    String modifiedLiteral = searchLiteral.substring(0, searchLiteral.indexOf("ENG") - 1).
                            replace(" ", "+");
                    String searchURL = baseURL + modifiedLiteral;
                    getUI().getPage().open(searchURL, "_blank", true);
                }

            }
        });

//        wrapper.addComponents(searchBox, tree, debugTextField);
        wrapper.addComponents(searchBox, tree);

        setContent(wrapper);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WordNetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
