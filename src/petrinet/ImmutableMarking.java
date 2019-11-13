package petrinet;

import java.util.Map;
import java.util.Set;

public class ImmutableMarking extends Marking {
    final Map<Place, Integer> mapView;
    ImmutableMarking(Map<Place, Integer> map) {
        mapView = Map.copyOf(map);
    }

    ImmutableMarking(MutableMarking marking) {
        this(marking.map);
    }

    @Override
    int getCount(Place place) {
        return mapView.getOrDefault(place, 0);
    }

    @Override
    Set<Place> getKeys() {
        return mapView.keySet();
    }
}
