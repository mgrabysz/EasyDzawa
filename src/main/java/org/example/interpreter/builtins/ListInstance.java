package org.example.interpreter.builtins;

import lombok.Getter;
import org.example.interpreter.accessible.ObjectInstance;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ListInstance extends ObjectInstance {

    private final List<Object> list;

    public ListInstance() {
        super(ListDefinition.LIST, ListDefinition.methods);
        this.list = new ArrayList<>();
    }

    public ListInstance(List<Object> list) {
        super(ListDefinition.LIST, ListDefinition.methods);
        this.list = list;
    }

}
