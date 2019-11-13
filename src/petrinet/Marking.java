package petrinet;

import java.util.Set;

abstract class Marking {
    abstract int getCount(Place place);
    abstract Set<Place> getKeys();
}
