package it.unito.prog.model.modules.observables;

import it.unito.prog.utils.PlatformUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class ObservablesManagerImpl<T> implements ObservablesManager<T> {
    private final ConcurrentHashMap<T, ObservableList<?>> obLists;
    private final ConcurrentHashMap<T, SimpleObjectProperty<?>> obObjs;

    public ObservablesManagerImpl() {
        obLists = new ConcurrentHashMap<>();
        obObjs = new ConcurrentHashMap<>();
    }

    @Override
    public void newObject(T name) {
        obObjs.putIfAbsent(name, new SimpleObjectProperty<>());
    }

    @Override
    public void newObject(T name, Object initValue) {
        obObjs.putIfAbsent(name, new SimpleObjectProperty<>(initValue));
    }

    @Override
    public void newList(T name) {
        obLists.putIfAbsent(name, FXCollections.observableArrayList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> void setObjectValue(T name, K value) {
        SimpleObjectProperty<K> simpleObjectProperty = (SimpleObjectProperty<K>) obObjs.get(name);
        if (simpleObjectProperty == null) return;

        PlatformUtil.safeRun(() -> simpleObjectProperty.set(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> void addListEntry(T name, K value) {
        ObservableList<K> observableList = (ObservableList<K>) obLists.get(name);
        if (observableList == null) return;

        PlatformUtil.safeRun(() -> observableList.add(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> void removeListEntry(T name, K value) {
        ObservableList<K> observableList = (ObservableList<K>) obLists.get(name);
        if (observableList == null) return;

        PlatformUtil.safeRun(() -> observableList.remove(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> void fillList(T name, List<K> list) {
        ObservableList<K> observableList = (ObservableList<K>) obLists.get(name);
        if (observableList == null) return;

        PlatformUtil.safeRun(() -> observableList.addAll(list));
    }

    @Override
    public void clearList(T name) {
        ObservableList<?> observableList = obLists.get(name);
        if (observableList == null) return;

        PlatformUtil.safeRun(observableList::clear);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> ObservableList<K> getList(T name) {
        try {
            ObservableList<?> observableList = obLists.get(name);
            return (ObservableList<K>) observableList;
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> SimpleObjectProperty<K> getObject(T name) {
        try {
            SimpleObjectProperty<?> observableList = obObjs.get(name);
            return (SimpleObjectProperty<K>) observableList;
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
