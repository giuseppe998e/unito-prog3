package it.unito.prog.model.modules.observables;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;

public interface ObservablesManager<T> {
    void newObject(T name);

    void newObject(T name, Object initValue);

    void newList(T name);

    <K> void setObjectValue(T name, K value);

    <K> void addListEntry(T name, K value);

    <K> void removeListEntry(T name, K value);

    <K> void fillList(T name, List<K> list);

    void clearList(T name);

    <K> ObservableList<K> getList(T name);

    // void bindList(T name, Control listView);

    <K> SimpleObjectProperty<K> getObject(T name);

    // void bindObject(T name, Control objectProperty);
}
