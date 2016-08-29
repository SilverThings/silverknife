package layouts;

import core.Pin;
import core.Root;
import core.networking.Networking;
import javafx.scene.layout.GridPane;

import java.util.List;

public class CubieBoardLayout implements EmbeddedLayout{

    private final static int GRID_PANE_POSITION_IN_ROOT_CHILDREN = 3;
    private final static String NOT_SUPPORTED_IMAGE = "-fx-background-image: url(\"/not_supported.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center";

    private Root root;

    private Networking networking;

    private GridPane gridPane;

    public void setMainApp(Root root, Networking networking) {
        this.root = root;
        this.networking = networking;
        init();
    }

    private void init() {
        gridPane = (GridPane) root.getRootLayout().getChildren().get(GRID_PANE_POSITION_IN_ROOT_CHILDREN);
        gridPane.setStyle(NOT_SUPPORTED_IMAGE);
    }

    @Override
    public List<Pin> getCheckedPins() {
        return null;
    }

    @Override
    public void setColorOnPins(List<Pin> pins) {
    }
}
