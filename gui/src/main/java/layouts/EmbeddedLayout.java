package layouts;

import core.Pin;

import java.util.List;

public interface EmbeddedLayout {

    List<Pin> getCheckedPins();

    void updatePinsStatus(List<Pin> pins);
}
