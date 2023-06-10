package org.example.interpreter.accessible;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValueReference implements Cloneable {

    private Object value = null;

    @Override
    public ValueReference clone() {
        try {
            return (ValueReference) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
