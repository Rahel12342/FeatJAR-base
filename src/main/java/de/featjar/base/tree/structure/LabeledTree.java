/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of util.
 *
 * util is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with util. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-util> for further information.
 */
package de.featjar.base.tree.structure;

import java.util.Objects;

/**
 * A tree of nodes labeled with some data.
 * Can be used, for example, to represent a tree of integers or strings.
 *
 * @param <T> the type of label
 * @author Sebastian Krieter
 */
public class LabeledTree<T> extends Tree<LabeledTree<T>> {
    protected T label;

    public LabeledTree() {
    }

    public LabeledTree(T label) {
        this.label = label;
    }

    public T getLabel() {
        return label;
    }

    public void setLabel(T label) {
        this.label = label;
    }

    @Override
    public LabeledTree<T> cloneNode() {
        return new LabeledTree<>(label);
    }

    @Override
    public boolean equalsNode(LabeledTree<T> other) {
        return Objects.equals(getLabel(), other.getLabel());
    }

    @Override
    public String toString() {
        return String.format("LabeledTree[%s]", label);
    }
}