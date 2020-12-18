package it.unito.prog.model.modules.observables;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public interface ObservablesManager<T> {
    void newObject(T name);

    void newList(T name);

    <K> void setObjectValue(T name, K value);

    <K> void addListEntry(T name, K value);

    <K> void removeListEntry(T name, K value);

    void clearList(T name);

    <K> ObservableList<K> getList(T name);

    <K> SimpleObjectProperty<K> getObject(T name);
}
