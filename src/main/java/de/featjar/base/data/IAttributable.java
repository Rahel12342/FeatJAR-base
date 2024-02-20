/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-base.
 *
 * base is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * base is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with base. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-base> for further information.
 */
package de.featjar.base.data;

import java.util.LinkedHashMap;

/**
 * An object that can be annotated with {@link Attribute} values to store
 * additional metadata.
 *
 * @author Elias Kuiter
 */
public interface IAttributable {
    LinkedHashMap<IAttribute<?>, Object> getAttributes();

    default <T> Result<T> getAttributeValue(Attribute<T> attribute) {
        return attribute.apply(this);
    }

    default boolean hasAttributeValue(Attribute<?> attribute) {
        return getAttributeValue(attribute).isPresent();
    }

    default IMutatableAttributable mutate() {
        return (IMutatableAttributable) this;
    }

    static interface IMutatableAttributable extends IAttributable {
        default <S> void setAttributeValue(Attribute<S> attribute, S value) {
            if (value == null) {
                removeAttributeValue(attribute);
                return;
            }
            if (!attribute.getType().isInstance(value)) {
                throw new IllegalArgumentException(String.format(
                        "cannot set attribute of type %s to value of type %s", attribute.getType(), value.getClass()));
            }
            if (!attribute.getValidator().test(this, value)) {
                throw new IllegalArgumentException(
                        String.format("failed to validate attribute %s for value %s", attribute, value));
            }
            getAttributes().put(attribute, value);
        }

        default boolean toggleAttributeValue(Attribute<Boolean> attribute) {
            Boolean value = getAttributeValue(attribute).get();
            if (value == null) {
                value = attribute.getDefaultValue(this).orElse(Boolean.FALSE);
            }
            boolean toggledValue = !value;
            setAttributeValue(attribute, toggledValue);
            return toggledValue;
        }

        @SuppressWarnings("unchecked")
        default <S> S removeAttributeValue(Attribute<S> attribute) {
            return (S) getAttributes().remove(attribute);
        }
    }
}
